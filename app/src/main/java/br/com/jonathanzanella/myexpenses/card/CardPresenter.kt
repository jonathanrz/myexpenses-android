package br.com.jonathanzanella.myexpenses.card

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.AsyncTask
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountRepository
import br.com.jonathanzanella.myexpenses.account.ListAccountActivity
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException
import br.com.jonathanzanella.myexpenses.exceptions.ValidationException
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.helpers.ResourcesHelper
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.joda.time.DateTime

class CardPresenter(private val repository: CardRepository, private val accountRepository: AccountRepository,
                     private val expenseRepository: ExpenseRepository, private val resourcesHelper: ResourcesHelper) {

    private var view: CardContract.View? = null
    private var editView: CardContract.EditView? = null
    private var card: Card? = null
    private var account: Account? = null

    fun attachView(view: CardContract.View) {
        this.view = view
    }

    fun attachView(view: CardContract.EditView) {
        this.view = view
        this.editView = view
    }

    fun detachView() {
        this.view = null
        this.editView = null
    }

    @UiThread
    fun viewUpdated(invalidateCache: Boolean) {
        if (card != null) {
            if (invalidateCache) {
                object : AsyncTask<Void, Void, Void>() {

                    override fun doInBackground(vararg voids: Void): Void? {
                        card = repository.find(card!!.uuid!!)
                        return null
                    }

                    override fun onPostExecute(aVoid: Void?) {
                        super.onPostExecute(aVoid)
                        updateView()
                    }
                }.execute()
            } else {
                updateView()
            }
        } else {
            updateView()
        }
    }

    @UiThread
    fun updateView() {
        val c = card
        val v = editView
        if (c != null) {
            if (v != null) {
                v.setTitle(R.string.edit_card_title)
            } else {
                view!!.let {
                    val title = it.context.getString(R.string.card)
                    it.setTitle(title + " " + c.name)
                }
            }
            view!!.showCard(c)
            val a = account
            if (a == null)
                loadAccount(c.accountUuid!!)
            else
                v?.onAccountSelected(a)
        } else {
            v?.setTitle(R.string.new_card_title)
        }
    }

    @UiThread
    fun reloadCard() {
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                loadCard(card!!.uuid!!)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                updateView()
            }
        }.execute()
    }

    @WorkerThread
    fun loadCard(uuid: String) {
        card = repository.find(uuid)
        if (card == null)
            throw CardNotFoundException(uuid)
    }

    @UiThread
    fun save() {
        val v = editView ?: throw InvalidMethodCallException("save", javaClass.toString(), "View should be a Edit View")
        if (card == null)
            card = Card(accountRepository)
        card = v.fillCard(card!!)
        if (account != null)
            card!!.account = account

        object : AsyncTask<Void, Void, ValidationResult>() {

            override fun doInBackground(vararg voids: Void): ValidationResult {
                return repository.save(card!!)
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
        get() = card?.uuid

    fun showSelectAccountActivity(act: Activity) {
        if (card == null)
            act.startActivityForResult(Intent(act, ListAccountActivity::class.java), REQUEST_SELECT_ACCOUNT)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            REQUEST_SELECT_ACCOUNT -> {
                if (resultCode == RESULT_OK)
                    loadAccount(data.getStringExtra(ListAccountActivity.KEY_ACCOUNT_SELECTED_UUID))
            }
        }
    }

    @UiThread
    private fun loadAccount(uuid: String) {
        object : AsyncTask<Void, Void, Account>() {

            override fun doInBackground(vararg voids: Void): Account? {
                return accountRepository.find(uuid)
            }

            override fun onPostExecute(account: Account?) {
                super.onPostExecute(account)
                this@CardPresenter.account = account
                if (account != null) {
                    editView?.onAccountSelected(account)
                }
            }
        }.execute()
    }

    fun generateCreditCardBill(month: DateTime): Expense? {
        val c = card!!
        val expenses = repository.creditCardBills(c, month)
        var totalExpense = 0
        for (expense in expenses) {
            totalExpense += expense.value
            expense.charged = true
            expenseRepository.save(expense)
        }

        if (totalExpense == 0)
            return null

        val e = Expense()
        e.name = resourcesHelper.getString(R.string.invoice) + " " + c.name
        e.setDate(DateTime.now())
        e.value = totalExpense
        e.setChargeable(c.account!!)
        val validationResult = expenseRepository.save(e)
        if (!validationResult.isValid)
            throw ValidationException(validationResult)

        return e
    }

    companion object {
        private val REQUEST_SELECT_ACCOUNT = 1006
    }
}
