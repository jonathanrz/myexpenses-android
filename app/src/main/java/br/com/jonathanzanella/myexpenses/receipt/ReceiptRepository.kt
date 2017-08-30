package br.com.jonathanzanella.myexpenses.receipt

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.database.ModelRepository
import br.com.jonathanzanella.myexpenses.helpers.DateHelper
import br.com.jonathanzanella.myexpenses.log.Log
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import java.util.*

open class ReceiptRepository : ModelRepository<Receipt> {

    @WorkerThread
    fun find(uuid: String): Receipt? {
        return MyApplication.database.receiptDao().find(uuid).blockingFirst().firstOrNull()
    }

    @WorkerThread
    fun all(): List<Receipt> {
        return MyApplication.database.receiptDao().all().blockingFirst()
    }

    @WorkerThread
    fun monthly(month: DateTime): List<Receipt> {
        return MyApplication.database.receiptDao().monthly(DateHelper.firstDayOfMonth(month).millis,
                DateHelper.lastDayOfMonth(month).millis).blockingFirst()
    }

    @WorkerThread
    fun monthly(month: DateTime, account: Account): List<Receipt> {
        return MyApplication.database.receiptDao().monthly(DateHelper.firstDayOfMonth(month).millis,
                DateHelper.lastDayOfMonth(month).millis, account.uuid!!).blockingFirst()
    }

    @WorkerThread
    fun resume(month: DateTime): List<Receipt> {
        return MyApplication.database.receiptDao().resume(DateHelper.firstDayOfMonth(month).millis,
                DateHelper.lastDayOfMonth(month).millis).blockingFirst()
    }

    @WorkerThread
    fun greaterUpdatedAt(): Long {
        return MyApplication.database.receiptDao().greaterUpdatedAt().blockingFirst().firstOrNull()?.updatedAt ?: 0L
    }

    @WorkerThread
    fun unsync(): List<Receipt> {
        return MyApplication.database.receiptDao().unsync().blockingFirst()
    }

    @WorkerThread
    fun save(receipt: Receipt): ValidationResult {
        val result = validate(receipt)
        if (result.isValid) {
            if (receipt.id == 0L && receipt.uuid == null)
                receipt.uuid = UUID.randomUUID().toString()
            receipt.sync = false
            receipt.id = MyApplication.database.receiptDao().saveAtDatabase(receipt)
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
        if (!receipt.isDatePresent())
            result.addError(ValidationError.DATE)
        return result
    }

    @WorkerThread
    override fun syncAndSave(unsync: Receipt): ValidationResult {
        val result = validate(unsync)
        if (!result.isValid) {
            Log.warning("Receipt sync validation failed", unsync.getData() + "\nerrors: " + result.errorsAsString)
            return result
        }

        val receipt = find(unsync.uuid!!)
        if (receipt != null && receipt.id != unsync.id) {
            if (receipt.updatedAt != unsync.updatedAt)
                Log.warning("Receipt overwritten", unsync.getData())
            unsync.id = receipt.id
        }

        unsync.sync = true
        unsync.id = MyApplication.database.receiptDao().saveAtDatabase(unsync)

        return result
    }
}