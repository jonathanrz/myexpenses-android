package br.com.jonathanzanella.myexpenses.card

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.account.ListAccountActivity
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException
import br.com.jonathanzanella.myexpenses.exceptions.ValidationException
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseDataSource
import br.com.jonathanzanella.myexpenses.extensions.fromComputationToMainThread
import br.com.jonathanzanella.myexpenses.helpers.ResourcesHelper
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime
import javax.inject.Inject

class CardPresenter @Inject constructor(private val accountDataSource: AccountDataSource,
                                        private val dataSource: CardDataSource,
                                        private val expenseDataSource: ExpenseDataSource,
                                        private val resourcesHelper: ResourcesHelper) {
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
                doAsync {
                    card = dataSource.find(card!!.uuid!!)

                    uiThread { updateView() }
                }
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
        doAsync {
            loadCard(card!!.uuid!!)

            uiThread { updateView() }
        }
    }

    @WorkerThread
    fun loadCard(uuid: String) {
        card = dataSource.find(uuid)
        if (card == null)
            throw CardNotFoundException(uuid)
    }

    @UiThread
    fun save() {
        val v = editView ?: throw InvalidMethodCallException("save", javaClass.toString(), "View should be a Edit View")
        if (card == null)
            card = Card(accountDataSource)
        card = v.fillCard(card!!)
        if (account != null)
            card!!.account = account

        doAsync {
            val result = dataSource.save(card!!)

            uiThread {
                if (result.isValid) {
                    v.finishView()
                } else {
                    for (validationError in result.errors)
                        v.showError(validationError)
                }
            }
        }
    }

    val uuid: String?
        get() = card?.uuid

    fun showSelectAccountActivity(act: Activity) {
        if (card == null)
            act.startActivityForResult(Intent(act, ListAccountActivity::class.java), REQUEST_SELECT_ACCOUNT)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_SELECT_ACCOUNT -> {
                if (resultCode == RESULT_OK)
                    loadAccount(data!!.getStringExtra(ListAccountActivity.KEY_ACCOUNT_SELECTED_UUID))
            }
        }
    }

    @UiThread
    private fun loadAccount(uuid: String) {
        accountDataSource.find(uuid)
                .fromComputationToMainThread()
                .subscribe {
                    account = it

                    account?.let { editView?.onAccountSelected(it) }
                }
    }

    fun generateCreditCardBill(month: DateTime): Expense? {
        val c = card!!
        val expenses = expenseDataSource.creditCardBills(c, month)
        var totalExpense = 0
        for (expense in expenses) {
            totalExpense += expense.value
            expense.charged = true
            expenseDataSource.save(expense)
        }

        if (totalExpense == 0)
            return null

        val e = Expense()
        e.name = resourcesHelper.getString(R.string.invoice) + " " + c.name
        e.setDate(DateTime.now())
        e.value = totalExpense
        e.setChargeable(c.account!!)
        val validationResult = expenseDataSource.save(e)
        if (!validationResult.isValid)
            throw ValidationException(validationResult)

        return e
    }

    companion object {
        private val REQUEST_SELECT_ACCOUNT = 1006
    }
}
