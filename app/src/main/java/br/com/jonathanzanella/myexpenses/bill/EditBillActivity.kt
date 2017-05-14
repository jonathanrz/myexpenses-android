package br.com.jonathanzanella.myexpenses.bill

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper
import br.com.jonathanzanella.myexpenses.helpers.CurrencyTextWatch
import br.com.jonathanzanella.myexpenses.log.Log
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.apache.commons.lang3.StringUtils
import org.jetbrains.anko.*
import org.joda.time.DateTime

class EditBillActivity : AppCompatActivity(), BillContract.EditView {

    private val presenter: BillPresenter
    private val ui = EditBillActivityUi()

    init {
        val expenseRepository = ExpenseRepository(RepositoryImpl<Expense>(this))
        presenter = BillPresenter(BillRepository(RepositoryImpl<Bill>(this), expenseRepository))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)

        storeBundle(savedInstanceState)
        storeBundle(intent.extras)

        ui.toolbar.setup(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        ui.editAmount.addTextChangedListener(CurrencyTextWatch(ui.editAmount))
        ui.editInitDate.onClick { presenter.onInitDate(this) }
        ui.editEndDate.onClick { presenter.onEndDate(this) }
        presenter.onViewUpdated(false)
    }

    fun storeBundle(extras: Bundle?) {
        if (extras?.containsKey(KEY_BILL_UUID) ?: false) {
            object : AsyncTask<Void, Void, Void>() {

                override fun doInBackground(vararg voids: Void): Void? {
                    presenter.loadBill(extras!!.getString(KEY_BILL_UUID))
                    return null
                }

                override fun onPostExecute(aVoid: Void) {
                    super.onPostExecute(aVoid)
                    presenter.onViewUpdated(false)
                }
            }.execute()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val uuid = presenter.uuid
        if (uuid != null)
            outState.putString(KEY_BILL_UUID, uuid)
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

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
        menuInflater.inflate(R.menu.save, menu)
        return true
    }

    override fun onInitDateChanged(date: DateTime) {
        ui.editInitDate.setText(Bill.SIMPLE_DATE_FORMAT.format(date.toDate()))
    }

    override fun onEndDateChanged(date: DateTime) {
        ui.editEndDate.setText(Bill.SIMPLE_DATE_FORMAT.format(date.toDate()))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> presenter.save()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showBill(bill: Bill) {
        ui.editName.setText(bill.name)
        ui.editAmount.setText(CurrencyHelper.format(bill.amount))
        ui.editDueDate.setText(bill.dueDate.toString())
    }

    override fun fillBill(bill: Bill): Bill {
        val amountText = ui.editAmount.text.toString().replace("[^\\d]".toRegex(), "")
        val dueDateText = ui.editDueDate.text.toString().replace("[^\\d]".toRegex(), "")

        bill.name = ui.editName.text.toString()
        bill.amount = if (StringUtils.isEmpty(amountText)) 0 else Integer.parseInt(amountText)
        bill.dueDate = if (StringUtils.isEmpty(dueDateText)) 0 else Integer.parseInt(dueDateText)

        return bill
    }

    override fun finishView() {
        val i = Intent()
        i.putExtra(KEY_BILL_UUID, presenter.uuid)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    override fun showError(error: ValidationError) {
        when (error) {
            ValidationError.NAME -> ui.editName.error = getString(error.message)
            ValidationError.AMOUNT -> ui.editAmount.error = getString(error.message)
            ValidationError.DUE_DATE -> ui.editDueDate.error = getString(error.message)
            ValidationError.INIT_DATE, ValidationError.INIT_DATE_GREATER_THAN_END_DATE -> ui.editInitDate.error = getString(error.message)
            ValidationError.END_DATE -> ui.editEndDate.error = getString(error.message)
            else -> Log.error(this.javaClass.name, "Validation unrecognized, field:" + error)
        }
    }

    companion object {
        val KEY_BILL_UUID = "KeyBillUuid"
    }
}

class EditBillActivityUi : AnkoComponent<EditBillActivity> {
    lateinit var toolbar : TemplateToolbar
    lateinit var editName: EditText
    lateinit var editAmount: EditText
    lateinit var editDueDate: EditText
    lateinit var editInitDate: EditText
    lateinit var editEndDate: EditText

    override fun createView(ui: AnkoContext<EditBillActivity>) = with(ui) {
        verticalLayout {
            toolbar = toolbarTemplate {}

            linearViewFrame {
                textInputLayout {
                    editName = appCompatEditText {
                        id = R.id.act_edit_bill_name
                        hint = resources.getString(R.string.name)
                    }
                }
                textInputLayout {
                    editAmount = appCompatEditText {
                        id = R.id.act_edit_bill_amount
                        hint = resources.getString(R.string.amount)
                    }
                }
                textInputLayout {
                    editDueDate = appCompatEditText {
                        id = R.id.act_edit_bill_due_date
                        hint = resources.getString(R.string.due_date)
                    }
                }
                textInputLayout {
                    editInitDate = dateView {
                        id = R.id.act_edit_bill_init_date
                        hint = resources.getString(R.string.start)
                    }
                }
                textInputLayout {
                    editEndDate = dateView {
                        id = R.id.act_edit_bill_end_date
                        hint = resources.getString(R.string.end)
                    }
                }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}