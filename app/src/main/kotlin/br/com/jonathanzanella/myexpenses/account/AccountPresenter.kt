package br.com.jonathanzanella.myexpenses.account

import android.support.annotation.UiThread
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import io.reactivex.Observable
import javax.inject.Inject

class AccountPresenter @Inject constructor(private val dataSource: AccountDataSource) {

    private var view: AccountContract.View? = null
    private var editView: AccountContract.EditView? = null
    private var account: Account? = null

    fun attachView(view: AccountContract.View) {
        this.view = view
    }

    fun attachView(view: AccountContract.EditView) {
        this.view = view
        this.editView = view
    }

    fun detachView() {
        this.view = null
        this.editView = null
    }

    @UiThread
    fun viewUpdated(invalidateCache: Boolean) {
        val acc = account
        if (acc == null) {
            editView?.setTitle(R.string.new_account_title)
        } else {
            if (invalidateCache)
                loadAccount(acc.uuid!!)
        }
    }

    @UiThread
    fun updateView() {
        val v = editView
        if (v != null) {
            v.setTitle(R.string.edit_account_title)
        } else {
            view!!.let {
                val title = it.context.getString(R.string.account)
                it.setTitle(title + " " + account!!.name)
            }
        }
        view!!.showAccount(account!!)
    }

    @UiThread
    fun reloadAccount() {
        loadAccount(account!!.uuid!!)
    }

    fun loadAccount(uuid: String): Observable<Account> {
        return dataSource.find(uuid)
                .doOnNext { account = it }
    }

    @UiThread
    fun save(): Observable<ValidationResult> {
        val v = editView ?: throw InvalidMethodCallException("save", javaClass.toString(), "View should be a Edit View")

        if (account == null)
            account = Account()

        account = v.fillAccount(account!!)

        return dataSource.save(account!!)
    }

    fun delete(): Observable<ValidationResult> {
        account!!.removed = true
        return dataSource.save(account!!)
    }

    val uuid: String?
        get() = account?.uuid
}
