package br.com.jonathanzanella.myexpenses.bill

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.database.Fields
import br.com.jonathanzanella.myexpenses.database.ModelRepository
import br.com.jonathanzanella.myexpenses.database.Repository
import br.com.jonathanzanella.myexpenses.database.Where
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.log.Log
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import java.util.*

open class BillRepository(private val repository: Repository<Bill>, private val expenseRepository: ExpenseRepository) : ModelRepository<Bill> {
    private val billTable = BillTable()

    @WorkerThread
    fun find(uuid: String): Bill? {
        return repository.find(billTable, uuid)
    }

    @WorkerThread
    fun all(): List<Bill> {
        return repository.query(billTable, Where(null).orderBy(Fields.NAME))
    }

    @WorkerThread
    fun greaterUpdatedAt(): Long {
        return repository.greaterUpdatedAt(billTable)
    }

    @WorkerThread
    fun unsync(): List<Bill> {
        return repository.unsync(billTable)
    }

    @WorkerThread
    fun monthly(month: DateTime): List<Bill> {
        val expenses = expenseRepository.monthly(month)
        val query = Where(Fields.INIT_DATE).lessThanOrEq(month.millis)
                .and(Fields.END_DATE).greaterThanOrEq(month.millis)
        val bills = repository.query(billTable, query) as MutableList<Bill>
        var i = 0
        while (i < bills.size) {
            val bill = bills[i]
            var billAlreadyPaid = false
            for (expense in expenses) {
                val b = repository.find(billTable, expense.billUuid)
                if (b != null && b.uuid == bill.uuid) {
                    billAlreadyPaid = true
                    break
                }
            }
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
            repository.saveAtDatabase(billTable, bill)
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
    override fun syncAndSave(unsyncBill: Bill): ValidationResult {
        val result = validate(unsyncBill)
        if (!result.isValid) {
            Log.warning("Bill sync validation failed", unsyncBill.getData() + "\nerrors: " + result.errorsAsString)
            return result
        }

        val bill = find(unsyncBill.uuid!!)
        if (bill != null && bill.id != unsyncBill.id) {
            if (bill.updatedAt != unsyncBill.updatedAt)
                Log.warning("Bill overwritten", unsyncBill.getData())
            unsyncBill.id = bill.id
        }

        unsyncBill.sync = true
        repository.saveAtDatabase(billTable, unsyncBill)

        return result
    }
}