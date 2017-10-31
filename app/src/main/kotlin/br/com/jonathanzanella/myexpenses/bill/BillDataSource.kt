package br.com.jonathanzanella.myexpenses.bill

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.expense.ExpenseDataSource
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import io.reactivex.Flowable
import io.reactivex.Observable
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import timber.log.Timber
import javax.inject.Inject

interface BillDataSource {
    fun all(): Flowable<List<Bill>>
    fun unsync(): Flowable<List<Bill>>
    fun monthly(month: DateTime): Flowable<List<Bill>>

    fun find(uuid: String): Observable<Bill>
    fun greaterUpdatedAt(): Observable<Long>

    fun save(bill: Bill): Observable<ValidationResult>
    fun syncAndSave(unsync: Bill): Observable<ValidationResult>
}

class BillRepository @Inject constructor(val dao: BillDao, private val expenseDataSource: ExpenseDataSource): BillDataSource {
    override fun all(): Flowable<List<Bill>> = Flowable.fromCallable { dao.all() }
    override fun unsync(): Flowable<List<Bill>> = Flowable.fromCallable { dao.unsync() }

    override fun monthly(month: DateTime): Flowable<List<Bill>> {
        return Flowable.fromCallable { dao.monthly(month.millis) }
                .flatMap {
                    val expenses = expenseDataSource.monthly(month)

                    Flowable.just(it.filter {
                        @Suppress("UnnecessaryVariable")
                        val bill = it
                        expenses
                            .filter { it.billUuid != null }
                            .map { find(it.billUuid!!) }
                            .any { it.blockingFirst().uuid == bill.uuid }
                    })
                }.doOnNext {
                    it.map { it.month = month }
                }
    }

    override fun find(uuid: String): Observable<Bill> = Observable.fromCallable { dao.find(uuid) }

    override fun greaterUpdatedAt(): Observable<Long> =
            Observable.fromCallable { dao.greaterUpdatedAt().firstOrNull()?.updatedAt ?: 0L }

    @WorkerThread
    override fun save(bill: Bill): Observable<ValidationResult> {
        return Observable.fromCallable {
            val result = validate(bill)
            if (result.isValid) {
                if (bill.id == 0L && bill.uuid == null)
                    bill.uuid = java.util.UUID.randomUUID().toString()
                bill.sync = false
                bill.id = dao.saveAtDatabase(bill)
            }
            result
        }
    }

    private fun validate(bill: Bill): ValidationResult {
        val result = ValidationResult()
        if (StringUtils.isEmpty(bill.name))
            result.addError(ValidationError.NAME)
        if (bill.amount <= 0)
            result.addError(ValidationError.AMOUNT)
        if (bill.dueDate <= 0)
            result.addError(ValidationError.DUE_DATE)
        if (bill.initDate == null)
            result.addError(ValidationError.INIT_DATE)
        if (bill.endDate == null)
            result.addError(ValidationError.END_DATE)
        if (bill.initDate != null && bill.endDate != null && bill.initDate!!.isAfter(bill.endDate))
            result.addError(ValidationError.INIT_DATE_GREATER_THAN_END_DATE)
        return result
    }

    @WorkerThread
    override fun syncAndSave(unsync: Bill): Observable<ValidationResult> {
        return Observable.fromCallable {
            val result = validate(unsync)
            if (!result.isValid) {
                Timber.tag("Bill sync valid failed")
                        .w(unsync.getData() + "\nerrors: " + result.errorsAsString)
                result
            } else {
                val bill = find(unsync.uuid!!).blockingFirst()
                if (bill.id != unsync.id) {
                    if (bill.updatedAt != unsync.updatedAt)
                        Timber.tag("Bill overwritten")
                                .w(unsync.getData())
                    unsync.id = bill.id
                }

                unsync.sync = true
                unsync.id = dao.saveAtDatabase(unsync)

                result
            }
        }
    }
}
