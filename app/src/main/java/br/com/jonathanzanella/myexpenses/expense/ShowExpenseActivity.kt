package br.com.jonathanzanella.myexpenses.expense

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.bill.Bill
import br.com.jonathanzanella.myexpenses.bill.BillRepository
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper
import br.com.jonathanzanella.myexpenses.receipt.Receipt
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.jetbrains.anko.*

class ShowExpenseActivity : AppCompatActivity(), ExpenseContract.View {

    private val ui = ShowExpenseActivityUi()
    private var presenter: ExpensePresenter

    init {
        val expenseRepository = ExpenseRepository(RepositoryImpl<Expense>(this))
        val billRepository = BillRepository(RepositoryImpl<Bill>(this), expenseRepository)
        presenter = ExpensePresenter(expenseRepository, billRepository)
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

    override fun setTitle(string: String?) {
        ui.toolbar.title = string
    }

    override fun getContext(): Context {
        return this
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
        ui.name.text = expense.name
        ui.date.text = Receipt.SIMPLE_DATE_FORMAT.format(expense.date.toDate())
        ui.value.text = CurrencyHelper.format(expense.value)
        ui.showInOverview.text = CurrencyHelper.format(expense.valueToShowInOverview)
        object : AsyncTask<Void, Void, Chargeable>() {

            override fun doInBackground(vararg voids: Void): Chargeable {
                return expense.chargeableFromCache
            }

            override fun onPostExecute(chargeable: Chargeable) {
                super.onPostExecute(chargeable)
                ui.chargeable.text = chargeable.name
            }
        }.execute()
        ui.chargeNextMonth.visibility = if (expense.isChargedNextMonth) View.VISIBLE else View.GONE
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
