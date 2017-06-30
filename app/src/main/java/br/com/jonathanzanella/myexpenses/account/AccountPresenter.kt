package br.com.jonathanzanella.myexpenses.account

import android.os.AsyncTask
import android.support.annotation.UiThread
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException
import br.com.jonathanzanella.myexpenses.validations.ValidationResult

class AccountPresenter(private val repository: AccountRepository) {

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
        if (account == null) {
            if (editView != null)
                editView!!.setTitle(R.string.new_account_title)
        } else {
            if (invalidateCache)
                loadAccount(account!!.uuid!!)
        }
    }

    @UiThread
    private fun updateView() {
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

    @UiThread
    fun loadAccount(uuid: String) {
        object : AsyncTask<Void, Void, Account>() {

            override fun doInBackground(vararg voids: Void): Account? {
                account = repository.find(uuid)
                return account
            }

            override fun onPostExecute(account: Account?) {
                super.onPostExecute(account)
                if (account != null)
                    updateView()
            }
        }.execute()
    }

    @UiThread
    fun save() {
        val v = editView ?: throw InvalidMethodCallException("save", javaClass.toString(), "View should be a Edit View")

        if (account == null)
            account = Account()

        account = v.fillAccount(account!!)
        object : AsyncTask<Void, Void, ValidationResult>() {

            override fun doInBackground(vararg voids: Void): ValidationResult {
                return repository.save(account!!)
            }

            override fun onPostExecute(result: ValidationResult) {
                super.onPostExecute(result)
                if (result.isValid) {
                    v.finishView()
                } else {
                    for (validationError in result.errors)
                        v.showError(validationError)
                }
            }
        }.execute()
    }

    val uuid: String?
        get() = account?.uuid
}
