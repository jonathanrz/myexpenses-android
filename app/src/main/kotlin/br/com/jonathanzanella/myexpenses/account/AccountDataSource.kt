package br.com.jonathanzanella.myexpenses.account

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.database.DatabaseObservable
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import io.reactivex.Observable
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import javax.inject.Inject

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

    private fun refreshObservables() {
        allData.emit()
        resumeScreenData.emit()
        unsyncData.emit()
    }

    @WorkerThread
    override fun all(): Observable<List<Account>> = allData.cache()

    override fun forResumeScreen(): Observable<List<Account>> = resumeScreenData.cache()

    override fun unsync(): Observable<List<Account>> = unsyncData.cache()

    override fun find(uuid: String): Observable<Account> = Observable.fromCallable {
        Log.i("teste", "will load account $uuid")
        val account = dao.find(uuid).first()
        Log.i("teste", "loaded account ${account.name}")
        account
    }

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

                refreshObservables()
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
                val account = find(unsync.uuid!!).blockingFirst()

                if (account != null && account.id != unsync.id) {
                    if (account.updatedAt != unsync.updatedAt)
                        Timber.tag("Account overwritten")
                                .w(unsync.getData())
                    unsync.id = account.id
                }

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
