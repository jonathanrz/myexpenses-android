package br.com.jonathanzanella.myexpenses.receipt

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.database.Fields
import br.com.jonathanzanella.myexpenses.database.ModelRepository
import br.com.jonathanzanella.myexpenses.database.Repository
import br.com.jonathanzanella.myexpenses.database.Where
import br.com.jonathanzanella.myexpenses.helpers.DateHelper
import br.com.jonathanzanella.myexpenses.log.Log
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import java.util.*

class ReceiptRepository(private val repository: Repository<Receipt>) : ModelRepository<Receipt> {
    private val table = ReceiptTable()

    @WorkerThread
    fun find(uuid: String): Receipt? {
        return repository.find(table, uuid)
    }

    @WorkerThread
    fun all(): List<Receipt> {
        return repository.query(table, Where(null).orderBy(Fields.DATE))
    }

    @WorkerThread
    fun monthly(month: DateTime): List<Receipt> {
        return repository.query(table, monthlyQuery(month, null))
    }

    @WorkerThread
    fun monthly(month: DateTime, account: Account): List<Receipt> {
        return repository.query(table, monthlyQuery(month, account))
    }

    @WorkerThread
    private fun monthlyQuery(month: DateTime, account: Account?): Where {
        var where = Where(Fields.DATE).greaterThanOrEq(DateHelper.firstDayOfMonth(month).millis)
                .and(Fields.DATE).lessThanOrEq(DateHelper.lastDayOfMonth(month).millis)
                .and(Fields.REMOVED).eq(false)
                .orderBy(Fields.DATE)
        if (account != null)
            where = where.and(Fields.ACCOUNT_UUID).eq(account.uuid!!)

        return where
    }

    @WorkerThread
    fun resume(month: DateTime): List<Receipt> {
        return repository.query(table, Where(Fields.DATE).greaterThanOrEq(month.millis)
                .and(Fields.DATE).lessThanOrEq(month.plusMonths(1).millis)
                .and(Fields.IGNORE_IN_RESUME).eq(false)
                .and(Fields.REMOVED).eq(false)
                .orderBy(Fields.DATE))
    }

    @WorkerThread
    fun greaterUpdatedAt(): Long {
        return repository.greaterUpdatedAt(table)
    }

    @WorkerThread
    fun unsync(): List<Receipt> {
        return repository.unsync(table)
    }

    @WorkerThread
    fun save(receipt: Receipt): ValidationResult {
        val result = validate(receipt)
        if (result.isValid) {
            if (receipt.id == 0L && receipt.uuid == null)
                receipt.uuid = UUID.randomUUID().toString()
            receipt.sync = false
            repository.saveAtDatabase(table, receipt)
        }
        return result
    }

    private fun validate(receipt: Receipt): ValidationResult {
        val result = ValidationResult()
        if (StringUtils.isEmpty(receipt.name))
            result.addError(ValidationError.NAME)
        if (receipt.amount <= 0)
            result.addError(ValidationError.AMOUNT)
        if (receipt.source == null)
            result.addError(ValidationError.SOURCE)
        if (receipt.accountFromCache == null)
            result.addError(ValidationError.ACCOUNT)
        if (receipt.getDate() == null)
            result.addError(ValidationError.DATE)
        return result
    }

    @WorkerThread
    override fun syncAndSave(unsyncReceipt: Receipt): ValidationResult {
        val result = validate(unsyncReceipt)
        if (!result.isValid) {
            Log.warning("Receipt sync validation failed", unsyncReceipt.getData() + "\nerrors: " + result.errorsAsString)
            return result
        }

        val receipt = find(unsyncReceipt.uuid!!)
        if (receipt != null && receipt.id != unsyncReceipt.id) {
            if (receipt.updatedAt != unsyncReceipt.updatedAt)
                Log.warning("Receipt overwritten", unsyncReceipt.getData())
            unsyncReceipt.id = receipt.id
        }

        unsyncReceipt.sync = true
        repository.saveAtDatabase(table, unsyncReceipt)

        return result
    }
}