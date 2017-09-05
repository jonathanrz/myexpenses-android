package br.com.jonathanzanella.myexpenses.card

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
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
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
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
                doAsync {
                    card = repository.find(card!!.uuid!!)

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

        doAsync {
            val result = repository.save(card!!)

            if (result.isValid) {
                v.finishView()
            } else {
                for (validationError in result.errors)
                    v.showError(validationError)
            }
        }
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
        doAsync {
            account = accountRepository.find(uuid)

            uiThread { account?.let { editView?.onAccountSelected(it) }}
        }
    }

    fun generateCreditCardBill(month: DateTime): Expense? {
        val c = card!!
        val expenses = expenseRepository.creditCardBills(c, month)
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
