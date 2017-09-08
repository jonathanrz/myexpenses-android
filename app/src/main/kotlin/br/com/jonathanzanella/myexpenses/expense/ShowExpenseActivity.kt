package br.com.jonathanzanella.myexpenses.expense

import android.content.Intent
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.jetbrains.anko.*
import javax.inject.Inject

class ShowExpenseActivity : AppCompatActivity(), ExpenseContract.View {
    override val context = this
    private val ui = ShowExpenseActivityUi()
    @Inject
    lateinit var presenter: ExpensePresenter

    init {
        App.getAppComponent().inject(this)
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
        presenter.onViewUpdated(false)
    }

    @UiThread
    fun storeBundle(extras: Bundle?) {
        if (extras != null)
            presenter.storeBundle(extras)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.onSaveInstanceState(outState)
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

    override fun setTitle(string: String) {
        ui.toolbar.title = string
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_delete, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                presenter.edit(this)
            }
            R.id.action_delete -> {
                presenter.delete(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @UiThread
    override fun showExpense(expense: Expense) {
        ui.apply {
            name.text = expense.name
            date.text = Transaction.SIMPLE_DATE_FORMAT.format(expense.getDate().toDate())
            value.text = expense.value.toCurrencyFormatted()
            showInOverview.text = expense.valueToShowInOverview.toCurrencyFormatted()
            chargeNextMonth.visibility = if (expense.chargedNextMonth) View.VISIBLE else View.GONE
        }

        doAsync {
            val chargeable = expense.chargeableFromCache

            uiThread { ui.chargeable.text = chargeable?.name }
        }
    }

    @UiThread
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.attachView(this)
        presenter.onActivityResult(requestCode, resultCode)
    }

    companion object {
        val KEY_EXPENSE_UUID = ExpensePresenter.KEY_EXPENSE_UUID
    }
}

private class ShowExpenseActivityUi : AnkoComponent<ShowExpenseActivity> {
    lateinit var toolbar : TemplateToolbar
    lateinit var name : TextView
    lateinit var date : TextView
    lateinit var value : TextView
    lateinit var showInOverview : TextView
    lateinit var chargeable : TextView
    lateinit var bill : TextView
    lateinit var chargeNextMonth : TableRow

    override fun createView(ui: AnkoContext<ShowExpenseActivity>) = with(ui) {
        verticalLayout {
            toolbar = toolbarTemplate {}

            tableViewFrame {
                tableRow {
                    static { text = resources.getString(R.string.name) }
                    name = staticWithData { id = R.id.act_show_expense_name }
                }
                tableRow {
                    static { text = resources.getString(R.string.date) }
                    date = staticWithData { id = R.id.act_show_expense_date }
                }
                tableRow {
                    static { text = resources.getString(R.string.income) }
                    value = staticWithData { id = R.id.act_show_expense_value }
                }
                tableRow {
                    static { text = resources.getString(R.string.income_to_show_in_overview) }
                    showInOverview = staticWithData { id = R.id.act_show_expense_value_to_show_in_overview }
                }
                tableRow {
                    static { text = resources.getString(R.string.paid_with) }
                    chargeable = staticWithData { id = R.id.act_show_expense_chargeable }
                }
                tableRow {
                    static { text = resources.getString(R.string.bill) }
                    bill = staticWithData { id = R.id.act_show_expense_bill }
                }
                chargeNextMonth = tableRow {
                    id = R.id.act_show_expense_charge_next_month
                    static { text = resources.getString(R.string.pay_next_month) }
                }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
