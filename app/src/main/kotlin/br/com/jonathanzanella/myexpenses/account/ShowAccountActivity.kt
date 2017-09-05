package br.com.jonathanzanella.myexpenses.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.transactions.TransactionsView
import br.com.jonathanzanella.myexpenses.helpers.firstDayOfMonth
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.jetbrains.anko.*
import org.joda.time.DateTime

class ShowAccountActivity : AppCompatActivity(), AccountContract.View {
    override val context = this
    private var monthToShow: DateTime? = null
    private val ui = ShowAccountActivityUi()
    private val presenter = AccountPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)

        storeBundle(savedInstanceState)
        storeBundle(intent.extras)

        if (monthToShow == null)
            monthToShow = DateTime.now().firstDayOfMonth()

        ui.toolbar.setup(this)
    }

    fun storeBundle(extras: Bundle?) {
        if (extras == null)
            return
        presenter.loadAccount(extras.getString(KEY_ACCOUNT_UUID))
        if (extras.containsKey(KEY_ACCOUNT_MONTH_TO_SHOW))
            monthToShow = DateTime(extras.getLong(KEY_ACCOUNT_MONTH_TO_SHOW)).firstDayOfMonth()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        presenter.viewUpdated(false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_ACCOUNT_UUID, presenter.uuid)
        outState.putLong(KEY_ACCOUNT_MONTH_TO_SHOW, monthToShow!!.millis)
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                val i = Intent(this, EditAccountActivity::class.java)
                i.putExtra(EditAccountActivity.KEY_ACCOUNT_UUID, presenter.uuid)
                startActivityForResult(i, EDIT_ACCOUNT)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showAccount(account: Account) {
        ui.accountName.text = account.name
        ui.accountBalance.text = account.balance.toCurrencyFormatted()
        ui.accountToPayCreditCard.setText(if (account.accountToPayCreditCard) R.string.yes else R.string.no)
        ui.accountToPayBills.setText(if (account.accountToPayBills) R.string.yes else R.string.no)

        ui.transactionsView.showTransactions(account, monthToShow!!)
    }

    override fun setTitle(string: String) {
        ui.toolbar.title = string
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_ACCOUNT && resultCode == Activity.RESULT_OK)
            presenter.reloadAccount()
    }

    companion object {
        val KEY_ACCOUNT_UUID = "KeyAccountUuid"
        val KEY_ACCOUNT_MONTH_TO_SHOW = "KeyAccountMonthToShow"
        private val EDIT_ACCOUNT = 1001
    }
}

private class ShowAccountActivityUi : AnkoComponent<ShowAccountActivity> {
    lateinit var toolbar : TemplateToolbar
    lateinit var accountName : TextView
    lateinit var accountBalance : TextView
    lateinit var accountToPayCreditCard : TextView
    lateinit var accountToPayBills : TextView
    lateinit var transactionsView : TransactionsView

    override fun createView(ui: AnkoContext<ShowAccountActivity>) = with(ui) {
        verticalLayout {
            toolbar = toolbarTemplate {}

            tableLayout {
                tableRow {
                    static { text = resources.getString(R.string.name) }
                    accountName = staticWithData { id = R.id.act_show_account_name }
                }
                tableRow {
                    static { text = resources.getString(R.string.balance) }
                    accountBalance = staticWithData { id = R.id.act_show_account_balance }
                }
                tableRow {
                    static { text = resources.getString(R.string.account_to_pay_credit_card) }
                    accountToPayCreditCard = staticWithData { id = R.id.act_show_account_to_pay_credit_card }
                }
                tableRow {
                    static { text = resources.getString(R.string.account_to_pay_bills) }
                    accountToPayBills = staticWithData { id = R.id.act_show_account_to_pay_bills }
                }
            }.lparams { margin = resources.getDimensionPixelSize(R.dimen.default_spacing) }

            transactionsView = transactionsView {
                id = R.id.act_show_account_transactions
            }.lparams(width = FrameLayout.LayoutParams.MATCH_PARENT) {
                leftMargin = resources.getDimensionPixelSize(R.dimen.default_spacing)
                rightMargin = resources.getDimensionPixelSize(R.dimen.default_spacing)
                bottomMargin = resources.getDimensionPixelSize(R.dimen.default_spacing)
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
