package br.com.jonathanzanella.myexpenses.bill

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.expense.ExpenseDataSource
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import io.reactivex.Flowable
import io.reactivex.Maybe
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import timber.log.Timber
import java.util.*
import javax.inject.Inject

interface BillDataSource {
    fun all(): Flowable<List<Bill>>
    fun unsync(): Flowable<List<Bill>>
    fun monthly(month: DateTime): Flowable<List<Bill>>

    fun find(uuid: String): Maybe<Bill>
    fun greaterUpdatedAt(): Long

    fun save(bill: Bill): ValidationResult
    fun syncAndSave(unsync: Bill): ValidationResult
}

class BillRepository @Inject constructor(val dao: BillDao, private val expenseDataSource: ExpenseDataSource): BillDataSource {
    @WorkerThread
    override fun all(): Flowable<List<Bill>> = dao.all()

    @WorkerThread
    override fun unsync(): Flowable<List<Bill>> = dao.unsync()

    @WorkerThread
    override fun monthly(month: DateTime): Flowable<List<Bill>> {
        return dao.monthly(month.millis)
                .flatMap {
                    val expenses = expenseDataSource.monthly(month)

                    Flowable.just(it.filter {
                        @Suppress("UnnecessaryVariable")
                        val bill = it
                        expenses
                            .filter { it.billUuid != null }
                            .map { find(it.billUuid!!) }
                            .any { !it.isEmpty.blockingGet() && it.blockingGet().uuid == bill.uuid }
                    })
                }.doOnNext {
                    it.map { it.month = month }
                }
    }

    @WorkerThread
    override fun find(uuid: String): Maybe<Bill> = dao.find(uuid)

    @WorkerThread
    override fun greaterUpdatedAt(): Long {
        return dao.greaterUpdatedAt().blockingFirst().firstOrNull()?.updatedAt ?: 0L
    }

    @WorkerThread
    override fun save(bill: Bill): ValidationResult {
        val result = validate(bill)
        if (result.isValid) {
            if (bill.id == 0L && bill.uuid == null)
                bill.uuid = UUID.randomUUID().toString()
            bill.sync = false
            bill.id = dao.saveAtDatabase(bill)
        }
        return result
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
    override fun syncAndSave(unsync: Bill): ValidationResult {
        val result = validate(unsync)
        if (!result.isValid) {
            Timber.tag("Bill sync valid failed")
                    .w(unsync.getData() + "\nerrors: " + result.errorsAsString)
            return result
        }

        val maybeBill = find(unsync.uuid!!)
        if(maybeBill.isEmpty.blockingGet()) {
            val bill = maybeBill.blockingGet()
            if (bill.id != unsync.id) {
                if (bill.updatedAt != unsync.updatedAt)
                    Timber.tag("Bill overwritten")
                            .w(unsync.getData())
                unsync.id = bill.id
            }
        }

        unsync.sync = true
        unsync.id = dao.saveAtDatabase(unsync)

        return result
    }
}
