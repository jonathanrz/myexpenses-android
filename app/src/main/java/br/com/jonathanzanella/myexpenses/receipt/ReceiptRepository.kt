package br.com.jonathanzanella.myexpenses.receipt

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.helpers.firstDayOfMonth
import br.com.jonathanzanella.myexpenses.helpers.lastDayOfMonth
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import timber.log.Timber
import java.util.*

open class ReceiptRepository(private val dao: ReceiptDao = MyApplication.database.receiptDao()) {

    @WorkerThread
    fun find(uuid: String): Receipt? {
        return dao.find(uuid).blockingFirst().firstOrNull()
    }

    @WorkerThread
    fun all(): List<Receipt> {
        return dao.all().blockingFirst()
    }

    @WorkerThread
    fun monthly(month: DateTime): List<Receipt> {
        return dao.monthly(month.firstDayOfMonth().millis,
                month.lastDayOfMonth().millis).blockingFirst()
    }

    @WorkerThread
    fun monthly(month: DateTime, account: Account): List<Receipt> {
        return dao.monthly(month.firstDayOfMonth().millis,
                month.lastDayOfMonth().millis, account.uuid!!).blockingFirst()
    }

    @WorkerThread
    fun resume(month: DateTime): List<Receipt> {
        return dao.resume(month.firstDayOfMonth().millis,
                month.lastDayOfMonth().millis).blockingFirst()
    }

    @WorkerThread
    fun greaterUpdatedAt(): Long {
        return dao.greaterUpdatedAt().blockingFirst().firstOrNull()?.updatedAt ?: 0L
    }

    @WorkerThread
    fun unsync(): List<Receipt> {
        return dao.unsync().blockingFirst()
    }

    @WorkerThread
    fun save(receipt: Receipt): ValidationResult {
        val result = validate(receipt)
        if (result.isValid) {
            if (receipt.id == 0L && receipt.uuid == null)
                receipt.uuid = UUID.randomUUID().toString()
            receipt.sync = false
            receipt.id = dao.saveAtDatabase(receipt)
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
    fun syncAndSave(unsync: Receipt): ValidationResult {
        val result = validate(unsync)
        if (!result.isValid) {
            Timber.tag("Receipt sync validation failed").w(unsync.getData() + "\nerrors: " + result.errorsAsString)
            return result
        }

        val receipt = find(unsync.uuid!!)
        if (receipt != null && receipt.id != unsync.id) {
            if (receipt.updatedAt != unsync.updatedAt)
                Timber.tag("Receipt overwritten").w(unsync.getData())
            unsync.id = receipt.id
        }

        unsync.sync = true
        unsync.id = dao.saveAtDatabase(unsync)

        return result
    }
}