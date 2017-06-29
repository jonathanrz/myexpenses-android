package br.com.jonathanzanella.myexpenses.account

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.database.Fields
import br.com.jonathanzanella.myexpenses.database.ModelRepository
import br.com.jonathanzanella.myexpenses.database.Repository
import br.com.jonathanzanella.myexpenses.database.Where
import br.com.jonathanzanella.myexpenses.log.Log
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.apache.commons.lang3.StringUtils
import java.util.*

class AccountRepository(private val repository: Repository<Account>) : ModelRepository<Account> {
    private val accountTable = AccountTable()

    @WorkerThread
    fun find(uuid: String): Account? {
        return repository.find(accountTable, uuid)
    }

    @WorkerThread
    fun all(): List<Account> {
        return repository.query(accountTable, Where(null).orderBy(Fields.NAME))
    }

    @WorkerThread
    internal fun forResumeScreen(): List<Account> {
        return repository.query(accountTable, Where(Fields.SHOW_IN_RESUME).eq(true).orderBy(Fields.NAME))
    }

    @WorkerThread
    fun greaterUpdatedAt(): Long {
        return repository.greaterUpdatedAt(accountTable)
    }

    @WorkerThread
    fun unsync(): List<Account> {
        return repository.unsync(accountTable)
    }

    @WorkerThread
    fun save(account: Account): ValidationResult {
        val result = validate(account)
        if (result.isValid) {
            if (account.id == 0L && account.uuid == null)
                account.uuid = UUID.randomUUID().toString()
            account.sync = false
            repository.saveAtDatabase(accountTable, account)
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
    override fun syncAndSave(unsyncAccount: Account): ValidationResult {
        val result = validate(unsyncAccount)
        if (!result.isValid) {
            Log.warning("Account sync validation failed", unsyncAccount.getData() + "\nerrors: " + result.errorsAsString)
            return result
        }

        val account = find(unsyncAccount.uuid!!)

        if (account != null && account.id != unsyncAccount.id) {
            if (account.updatedAt != unsyncAccount.updatedAt)
                Log.warning("Account overwritten", unsyncAccount.getData())
            unsyncAccount.id = account.id
        }

        unsyncAccount.sync = true
        repository.saveAtDatabase(accountTable, unsyncAccount)

        return result
    }
}