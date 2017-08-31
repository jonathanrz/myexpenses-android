package br.com.jonathanzanella.myexpenses.account

import android.support.annotation.WorkerThread
import android.util.Log
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.database.ModelRepository
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.apache.commons.lang3.StringUtils
import java.util.*

open class AccountRepository : ModelRepository<Account> {
    @WorkerThread
    fun find(uuid: String): Account? {
        return MyApplication.database.accountDao().find(uuid).blockingFirst()
    }

    @WorkerThread
    fun all(): List<Account> {
        return MyApplication.database.accountDao().all().blockingFirst()
    }

    @WorkerThread
    internal fun forResumeScreen(): List<Account> {
        return MyApplication.database.accountDao().showInResume().blockingFirst()
    }

    @WorkerThread
    fun greaterUpdatedAt(): Long {
        return MyApplication.database.accountDao().greaterUpdatedAt().blockingFirst().updatedAt
    }

    @WorkerThread
    fun unsync(): List<Account> {
        return MyApplication.database.accountDao().unsync().blockingFirst()
    }

    @WorkerThread
    fun save(account: Account): ValidationResult {
        val result = validate(account)
        if (result.isValid) {
            if (account.id == 0L && account.uuid == null)
                account.uuid = UUID.randomUUID().toString()
            account.sync = false
            account.id = MyApplication.database.accountDao().saveAtDatabase(account)
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
            Log.w("Account validation fail", unsync.getData() + "\nerrors: " + result.errorsAsString)
            return result
        }

        val account = find(unsync.uuid!!)

        if (account != null && account.id != unsync.id) {
            if (account.updatedAt != unsync.updatedAt)
                Log.w("Account overwritten", unsync.getData())
            unsync.id = account.id
        }

        unsync.sync = true
        unsync.id = MyApplication.database.accountDao().saveAtDatabase(unsync)

        return result
    }
}