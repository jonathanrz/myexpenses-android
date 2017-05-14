package br.com.jonathanzanella.myexpenses.card

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountRepository
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.expense.EditExpenseActivity
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.helpers.ResourcesHelper
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.jetbrains.anko.*
import org.joda.time.DateTime

class ShowCardActivity : AppCompatActivity(), CardContract.View {

    private var presenter: CardPresenter
    private val ui = ShowCardActivityUi()

    init {
        val expenseRepository = ExpenseRepository(RepositoryImpl<Expense>(this))
        val accountRepository = AccountRepository(RepositoryImpl<Account>(this))
        val cardRepository = CardRepository(RepositoryImpl<Card>(this), expenseRepository)
        presenter = CardPresenter(cardRepository, accountRepository, expenseRepository, ResourcesHelper(this))
    }

    @UiThread
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)

        storeBundle(savedInstanceState)
        storeBundle(intent.extras)

        ui.toolbar.setup(this)
    }

    @UiThread
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        presenter.viewUpdated(false)
    }

    @UiThread
    fun storeBundle(extras: Bundle?) {
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                if (extras?.containsKey(KEY_CREDIT_CARD_UUID) ?: false)
                    presenter.loadCard(extras!!.getString(KEY_CREDIT_CARD_UUID))
                return null
            }

            override fun onPostExecute(aVoid: Void) {
                super.onPostExecute(aVoid)
                presenter.updateView()
            }
        }.execute()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CREDIT_CARD_UUID, presenter.uuid)
    }

    @UiThread
    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    @UiThread
    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun setTitle(string: String?) {
        ui.toolbar.title = string
    }

    override fun getContext(): Context {
        return this
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit, menu)
        return true
    }

    @UiThread
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                val i = Intent(this, EditCardActivity::class.java)
                i.putExtra(EditCardActivity.KEY_CARD_UUID, presenter.uuid)
                startActivityForResult(i, EDIT_CARD)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_CARD && resultCode == Activity.RESULT_OK)
            presenter.reloadCard()
    }



    @UiThread
    override fun showCard(card: Card) {
        object : AsyncTask<Void, Void, Account>() {

            override fun doInBackground(vararg voids: Void): Account {
                return card.account
            }

            override fun onPostExecute(account: Account) {
                super.onPostExecute(account)
                ui.cardAccount.text = account.name
            }
        }.execute()

        ui.cardName.text = card.name

        when (card.type) {
            CardType.CREDIT -> ui.cardType.setText(R.string.credit)
            CardType.DEBIT -> ui.cardType.setText(R.string.debit)
        }
    }

    @UiThread
    fun payCreditCardBill() {
        object : AsyncTask<Void, Void, Expense>() {
            override fun doInBackground(vararg voids: Void): Expense {
                return presenter.generateCreditCardBill(DateTime.now().minusMonths(1))
            }

            override fun onPostExecute(expense: Expense?) {
                super.onPostExecute(expense)
                if (expense != null) {
                    val i = Intent(this@ShowCardActivity, EditExpenseActivity::class.java)
                    i.putExtra(EditExpenseActivity.KEY_EXPENSE_UUID, expense.uuid)
                    startActivity(i)
                } else {
                    val ctx = this@ShowCardActivity
                    Toast.makeText(ctx, ctx.getString(R.string.empty_invoice), Toast.LENGTH_SHORT).show()
                }
            }
        }.execute()

    }

    companion object {
        val KEY_CREDIT_CARD_UUID = "KeyCreateCardUuid"
        private val EDIT_CARD = 1001
    }
}

class ShowCardActivityUi : AnkoComponent<ShowCardActivity> {
    lateinit var toolbar : TemplateToolbar
    lateinit var cardName: TextView
    lateinit var cardAccount: TextView
    lateinit var cardType: TextView

    override fun createView(ui: AnkoContext<ShowCardActivity>) = with(ui) {
        verticalLayout {
            toolbar = toolbarTemplate {}

            tableViewFrame {
                tableRow {
                    static { text = resources.getString(R.string.name) }
                    cardName = staticWithData { id = R.id.act_show_card_name }
                }
                tableRow {
                    static { text = resources.getString(R.string.account) }
                    cardAccount = staticWithData { id = R.id.act_show_card_account }
                }
                tableRow {
                    static { text = resources.getString(R.string.type) }
                    cardType = staticWithData { id = R.id.act_show_card_type }
                }
            }.lparams(height = 0, weight = 1f)

            button {
                id = R.id.act_show_card_pay_credit_card_bill
                text = resources.getString(R.string.pay_credit_card_bill)
                onClick { ui.owner.payCreditCardBill() }
            }.lparams(width = matchParent) {
                margin = resources.getDimensionPixelSize(R.dimen.default_spacing)
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}