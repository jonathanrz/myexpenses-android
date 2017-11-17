package br.com.jonathanzanella.myexpenses.account

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.database.DatabaseObjectObservable
import br.com.jonathanzanella.myexpenses.database.DatabaseObservable
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import io.reactivex.Observable
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import javax.inject.Inject

class AccountNotFoundException : Exception()

interface AccountDataSource {
    fun all(): Observable<List<Account>>
    fun forResumeScreen(): Observable<List<Account>>
    fun unsync(): Observable<List<Account>>

    fun find(uuid: String): Observable<Account>
    fun greaterUpdatedAt(): Observable<Long>

    fun save(account: Account): Observable<ValidationResult>
    fun syncAndSave(unsync: Account): Observable<ValidationResult>
    fun deleteAll()
}

class AccountRepository @Inject constructor(val dao: AccountDao): AccountDataSource {
    private val allData: DatabaseObservable<List<Account>> = DatabaseObservable { dao.all() }
    private val resumeScreenData: DatabaseObservable<List<Account>> = DatabaseObservable  { dao.showInResume() }
    private val unsyncData: DatabaseObservable<List<Account>> = DatabaseObservable  { dao.unsync() }
    private val findData: DatabaseObjectObservable<String, Account> = DatabaseObjectObservable { id -> dao.find(id).firstOrNull() ?: throw AccountNotFoundException() }

    private fun refreshObservables(account: Account? = null) {
        allData.emit()
        resumeScreenData.emit()
        unsyncData.emit()
        account?.let { findData.emit(it.uuid!!) }
    }

    @WorkerThread
    override fun all(): Observable<List<Account>> = allData.cache()

    override fun forResumeScreen(): Observable<List<Account>> = resumeScreenData.cache()

    override fun unsync(): Observable<List<Account>> = unsyncData.cache()

    override fun find(uuid: String): Observable<Account> = findData.cache(uuid)

    override fun greaterUpdatedAt(): Observable<Long> =
            Observable.fromCallable { dao.greaterUpdatedAt().firstOrNull()?.updatedAt ?:0 }

    override fun save(account: Account): Observable<ValidationResult> {
        return Observable.fromCallable {
            val result = validate(account)
            if (result.isValid) {
                if (account.id == 0L && account.uuid == null)
                    account.uuid = java.util.UUID.randomUUID().toString()
                account.sync = false
                account.id = dao.saveAtDatabase(account)

                refreshObservables(account)
            }
            result
        }
    }

    private fun validate(account: Account): ValidationResult {
        val result = ValidationResult()
        if (StringUtils.isEmpty(account.name))
            result.addError(ValidationError.NAME)
        return result
    }

    override fun syncAndSave(unsync: Account): Observable<ValidationResult> {
        return Observable.fromCallable {
            val result = validate(unsync)
            if (!result.isValid) {
                Timber.tag("Account validation fail")
                        .w(unsync.getData() + "\nerrors: " + result.errorsAsString)
                result
            } else {
                try {
                    val account = find(unsync.uuid!!).blockingFirst()

                    if (account.id != unsync.id) {
                        if (account.updatedAt != unsync.updatedAt)
                            Timber.tag("Account overwritten")
                                    .w(unsync.getData())
                        unsync.id = account.id
                    }
                } catch (ignored: RuntimeException) {}

                unsync.sync = true
                unsync.id = dao.saveAtDatabase(unsync)

                refreshObservables(unsync)

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
