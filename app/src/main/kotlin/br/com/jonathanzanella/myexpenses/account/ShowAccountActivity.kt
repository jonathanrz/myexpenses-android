package br.com.jonathanzanella.myexpenses.account

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_LONG
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.transactions.TransactionsView
import br.com.jonathanzanella.myexpenses.extensions.fromIOToMainThread
import br.com.jonathanzanella.myexpenses.helpers.firstDayOfMonth
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.views.anko.*
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.*
import org.joda.time.DateTime
import timber.log.Timber
import javax.inject.Inject

class ShowAccountActivity : AppCompatActivity(), AccountContract.View {
    override val context = this
    @Inject
    lateinit var presenter: AccountPresenter
    private val ui = ShowAccountActivityUi()
    private val compositeDisposable = CompositeDisposable()

    init {
        App.getAppComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)

        storeBundle(savedInstanceState)
        storeBundle(intent.extras)

        ui.toolbar.setup(this)
    }

    fun storeBundle(extras: Bundle?) {
        if (extras == null)
            return
        compositeDisposable.add(presenter.loadAccount(extras.getString(KEY_ACCOUNT_UUID))
                .fromIOToMainThread()
                .doOnError { Timber.e(it) }
                .subscribe { presenter.updateView() })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        presenter.viewUpdated(false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_ACCOUNT_UUID, presenter.uuid)
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onStop() {
        presenter.detachView()
        compositeDisposable.dispose()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_delete, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                val i = Intent(this, EditAccountActivity::class.java)
                i.putExtra(EditAccountActivity.KEY_ACCOUNT_UUID, presenter.uuid)
                startActivityForResult(i, EDIT_ACCOUNT)
            }
            R.id.action_delete -> {
                delete()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun delete() {
        AlertDialog.Builder(act)
                .setTitle(android.R.string.dialog_alert_title)
                .setMessage(R.string.message_confirm_deletion)
                .setPositiveButton(android.R.string.yes) { dialog, _ ->
                    dialog.dismiss()

                    compositeDisposable.add(presenter.delete()
                            .fromIOToMainThread()
                            .subscribe {
                                if(it.isValid)
                                    finish()
                                else
                                    Snackbar.make(window.decorView, R.string.error_message_deletion, LENGTH_LONG).show()
                    })
                }
                .setNegativeButton(android.R.string.no) { dialog, _ -> dialog.dismiss() }
                .show()
    }

    override fun showAccount(account: Account) {
        ui.accountName.text = account.name
        ui.accountBalance.text = account.balance.toCurrencyFormatted()
        ui.accountToPayCreditCard.setText(if (account.accountToPayCreditCard) R.string.yes else R.string.no)
        ui.accountToPayBills.setText(if (account.accountToPayBills) R.string.yes else R.string.no)

        ui.transactionsView.showTransactions(account, DateTime.now().firstDayOfMonth())
    }

    override fun setTitle(string: String) {
        ui.toolbar.title = string
    }

    companion object {
        val KEY_ACCOUNT_UUID = "KeyAccountUuid"
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
