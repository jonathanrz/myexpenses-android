package br.com.jonathanzanella.myexpenses.account

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import java.util.*
import javax.inject.Inject

interface AccountDataSource {
    fun all(): List<Account>
    fun forResumeScreen(): List<Account>
    fun unsync(): List<Account>

    fun find(uuid: String): Account?
    fun greaterUpdatedAt(): Long

    fun save(account: Account): ValidationResult
    fun syncAndSave(unsync: Account): ValidationResult
}

class AccountRepository @Inject constructor(val dao: AccountDao): AccountDataSource {
    @WorkerThread
    override fun all(): List<Account> {
        return dao.all().blockingFirst()
    }

    @WorkerThread
    override fun forResumeScreen(): List<Account> {
        return dao.showInResume().blockingFirst()
    }

    @WorkerThread
    override fun unsync(): List<Account> {
        return dao.unsync().blockingFirst()
    }

    @WorkerThread
    override fun find(uuid: String): Account? {
        return dao.find(uuid).blockingFirst().firstOrNull()
    }

    @WorkerThread
    override fun greaterUpdatedAt(): Long {
        return dao.greaterUpdatedAt().blockingFirst().firstOrNull()?.updatedAt ?:0
    }

    @WorkerThread
    override fun save(account: Account): ValidationResult {
        val result = validate(account)
        if (result.isValid) {
            if (account.id == 0L && account.uuid == null)
                account.uuid = UUID.randomUUID().toString()
            account.sync = false
            account.id = dao.saveAtDatabase(account)
        }
        return result
    }

    private fun validate(account: Account): ValidationResult {
        val result = ValidationResult()
        if (StringUtils.isEmpty(account.name))
            result.addError(ValidationError.NAME)
        return result
    }

    @WorkerThread
    override fun syncAndSave(unsync: Account): ValidationResult {
        val result = validate(unsync)
        if (!result.isValid) {
            Timber.tag("Account validation fail")
                    .w(unsync.getData() + "\nerrors: " + result.errorsAsString)
            return result
        }

        val account = find(unsync.uuid!!)

        if (account != null && account.id != unsync.id) {
            if (account.updatedAt != unsync.updatedAt)
                Timber.tag("Account overwritten")
                        .w(unsync.getData())
            unsync.id = account.id
        }

        unsync.sync = true
        unsync.id = dao.saveAtDatabase(unsync)

        return result
    }
}