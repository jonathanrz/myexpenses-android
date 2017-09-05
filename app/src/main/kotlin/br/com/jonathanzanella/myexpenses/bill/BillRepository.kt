package br.com.jonathanzanella.myexpenses.bill

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import timber.log.Timber
import java.util.*

open class BillRepository(private val expenseRepository: ExpenseRepository, private val dao: BillDao = App.database.billDao()) {
    @WorkerThread
    fun find(uuid: String): Bill? {
        return dao.find(uuid).blockingFirst().firstOrNull()
    }

    @WorkerThread
    fun all(): List<Bill> {
        return dao.all().blockingFirst()
    }

    @WorkerThread
    fun greaterUpdatedAt(): Long {
        return dao.greaterUpdatedAt().blockingFirst().firstOrNull()?.updatedAt ?: 0L
    }

    @WorkerThread
    fun unsync(): List<Bill> {
        return dao.unsync().blockingFirst()
    }

    @WorkerThread
    fun monthly(month: DateTime): List<Bill> {
        val expenses = expenseRepository.monthly(month)
        val bills = dao.monthly(month.millis).blockingFirst() as MutableList<Bill>
        var i = 0
        while (i < bills.size) {
            val bill = bills[i]
            val billAlreadyPaid = expenses
                    .filter { it.billUuid != null }
                    .map { find(it.billUuid!!) }
                    .any { it != null && it.uuid == bill.uuid }
            if (billAlreadyPaid) {
                bills.removeAt(i)
                i--
            }
            i++
        }

        for (bill in bills)
            bill.month = month

        return bills
    }

    @WorkerThread
    fun save(bill: Bill): ValidationResult {
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
    fun syncAndSave(unsync: Bill): ValidationResult {
        val result = validate(unsync)
        if (!result.isValid) {
            Timber.tag("Bill sync valid failed")
                    .w(unsync.getData() + "\nerrors: " + result.errorsAsString)
            return result
        }

        val bill = find(unsync.uuid!!)
        if (bill != null && bill.id != unsync.id) {
            if (bill.updatedAt != unsync.updatedAt)
                Timber.tag("Bill overwritten")
                        .w( unsync.getData())
            unsync.id = bill.id
        }

        unsync.sync = true
        unsync.id = dao.saveAtDatabase(unsync)

        return result
    }
}
