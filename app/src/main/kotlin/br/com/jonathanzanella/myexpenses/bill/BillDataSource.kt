package br.com.jonathanzanella.myexpenses.bill

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.database.DataSourceObserver
import br.com.jonathanzanella.myexpenses.database.DatabaseObservable
import br.com.jonathanzanella.myexpenses.database.DatabaseObservableWithValue
import br.com.jonathanzanella.myexpenses.expense.ExpenseDataSource
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import io.reactivex.Observable
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import timber.log.Timber
import javax.inject.Inject

class BillNotFoundException : Exception()

interface BillDataSource {
    fun all(): Observable<List<Bill>>
    fun unsync(): Observable<List<Bill>>
    fun monthly(month: DateTime): Observable<List<Bill>>

    fun find(uuid: String): Observable<Bill>
    fun greaterUpdatedAt(): Observable<Long>

    fun save(bill: Bill): Observable<ValidationResult>
    fun syncAndSave(unsync: Bill): Observable<ValidationResult>
    fun deleteAll()
}

class BillRepository @Inject constructor(val dao: BillDao, val expenseDataSource: ExpenseDataSource): BillDataSource {
    private val generateMonthlyData: (DateTime) -> List<Bill> = { month ->
        val bills = dao.monthly(month.millis)

        val expenses = expenseDataSource.monthly(month)

        bills.filter {
            @Suppress("UnnecessaryVariable")
            val bill = it
            !expenses
                    .filter { it.billUuid != null }
                    .map { find(it.billUuid!!).blockingFirst()!! }
                    .any { it.uuid == bill.uuid }
        }.map {
            it.month = month
            it
        }
    }

    private val expenseDataObserver = object: DataSourceObserver {
        override fun onDataChanged() {
            monthlyData.emit()
        }
    }

    private val allData: DatabaseObservable<List<Bill>> = DatabaseObservable { dao.all() }
    private val unsyncData: DatabaseObservable<List<Bill>> = DatabaseObservable  { dao.unsync() }
    private val monthlyData: DatabaseObservableWithValue<DateTime, List<Bill>> = DatabaseObservableWithValue(generateMonthlyData)

    init { expenseDataSource.registerDataSourceObserver(expenseDataObserver) }

    private fun refreshObservables() {
        allData.emit()
        unsyncData.emit()
        monthlyData.emit()
    }

    override fun all(): Observable<List<Bill>> = allData.cache()
    override fun unsync(): Observable<List<Bill>> = unsyncData.cache()
    override fun monthly(month: DateTime) = monthlyData.cache(month)

    override fun find(uuid: String): Observable<Bill> = Observable.fromCallable { dao.find(uuid).firstOrNull() ?: throw BillNotFoundException() }

    override fun greaterUpdatedAt(): Observable<Long> =
            Observable.fromCallable { dao.greaterUpdatedAt().firstOrNull()?.updatedAt ?: 0L }

    override fun save(bill: Bill): Observable<ValidationResult> {
        return Observable.fromCallable {
            val result = validate(bill)
            if (result.isValid) {
                if (bill.id == 0L && bill.uuid == null)
                    bill.uuid = java.util.UUID.randomUUID().toString()
                bill.sync = false
                bill.id = dao.saveAtDatabase(bill)

                refreshObservables()
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

    override fun syncAndSave(unsync: Bill): Observable<ValidationResult> {
        return Observable.fromCallable {
            val result = validate(unsync)
            if (!result.isValid) {
                Timber.tag("Bill sync valid failed")
                        .w(unsync.getData() + "\nerrors: " + result.errorsAsString)
                result
            } else {
                try {
                    val bill = find(unsync.uuid!!).blockingFirst()
                    if (bill.id != unsync.id) {
                        if (bill.updatedAt != unsync.updatedAt)
                            Timber.tag("Bill overwritten")
                                    .w(unsync.getData())
                        unsync.id = bill.id
                    }
                } catch (ignored: RuntimeException) {}

                unsync.sync = true
                unsync.id = dao.saveAtDatabase(unsync)

                refreshObservables()

                result
            }
        }
    }

    @WorkerThread
    override fun deleteAll() {
        dao.deleteAll()
        refreshObservables()
    }
}
