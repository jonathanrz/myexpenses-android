package br.com.jonathanzanella.myexpenses.expense

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.bill.Bill
import br.com.jonathanzanella.myexpenses.bill.BillRepository
import br.com.jonathanzanella.myexpenses.bill.ListBillActivity
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType
import br.com.jonathanzanella.myexpenses.chargeable.ListChargeableActivity
import br.com.jonathanzanella.myexpenses.helpers.CurrencyTextWatch
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.apache.commons.lang3.StringUtils
import org.jetbrains.anko.*
import org.joda.time.DateTime
import timber.log.Timber

class EditExpenseActivity : AppCompatActivity(), ExpenseContract.EditView {
    override val context = this
    private val ui = EditExpenseActivityUi()
    private val presenter: ExpensePresenter

    init {
        val expenseRepository = ExpenseRepository()
        presenter = ExpensePresenter(expenseRepository, BillRepository(expenseRepository))
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

        ui.value.addTextChangedListener(CurrencyTextWatch(ui.value))
        ui.valueToShowInOverview.addTextChangedListener(CurrencyTextWatch(ui.valueToShowInOverview))
        ui.value.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
            if (ui.valueToShowInOverview.text.toString().isEmpty() && !ui.value.text.toString().isEmpty()) {
                ui.valueToShowInOverview.text = ui.value.text
            }
        }
        presenter.attachView(this)
        presenter.onViewUpdated(false)
    }

    fun storeBundle(extras: Bundle?) {
        if (extras != null)
            presenter.storeBundle(extras)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.refreshExpense()
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun setTitle(string: String) {
        ui.toolbar.title = string
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.attachView(this)
        when (requestCode) {
            REQUEST_SELECT_CHARGEABLE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val uuid = data.getStringExtra(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_UUID)
                    val keyChargeableSelectedType = ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE
                    val type = data.getSerializableExtra(keyChargeableSelectedType) as ChargeableType
                    presenter.onChargeableSelected(type, uuid)
                }
            }
            REQUEST_SELECT_BILL -> {
                if (resultCode == Activity.RESULT_OK && data != null)
                    presenter.onBillSelected(data.getStringExtra(ListBillActivity.KEY_BILL_SELECTED_UUID))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> presenter.save()
        }
        return super.onOptionsItemSelected(item)
    }

    internal fun onDate() {
        presenter.onDate(this)
    }

    override fun onDateChanged(date: DateTime) {
        ui.date.setText(Transaction.SIMPLE_DATE_FORMAT.format(date.toDate()))
    }

    override fun onChargeableSelected(chargeable: Chargeable) {
        ui.chargeable.setText(chargeable.name)
        ui.payNextMonth.visibility = if (chargeable.canBePaidNextMonth()) View.VISIBLE else View.GONE
    }

    internal fun onChargeable() {
        doAsync {
            val hasChargeable = presenter.hasChargeable()

            uiThread {
                if (!hasChargeable) {
                    val intent = Intent(this@EditExpenseActivity, ListChargeableActivity::class.java)
                    startActivityForResult(intent, REQUEST_SELECT_CHARGEABLE)
                }
            }
        }
    }

    override fun onBillSelected(bill: Bill?) {
        if (bill != null) {
            if (ui.bill.text.toString().isEmpty())
                ui.bill.setText(bill.name)
            if (ui.name.text.toString().isEmpty())
                ui.name.setText(bill.name)
            if (ui.value.text.toString().isEmpty())
                ui.value.setText(bill.amount.toCurrencyFormatted())
            ui.showInOverview.isChecked = false
            ui.showInResume.isChecked = true
        } else {
            ui.bill.setText("")
        }
    }

    internal fun onBill() {
        startActivityForResult(Intent(this, ListBillActivity::class.java), REQUEST_SELECT_BILL)
    }

    override fun fillExpense(expense: Expense): Expense {
        expense.name = ui.name.text.toString()
        var value = 0
        val valueText = ui.value.text.toString().replace("[^\\d]".toRegex(), "")
        if (!StringUtils.isEmpty(valueText))
            value = Integer.parseInt(valueText)

        var valueToShowInOverview = 0
        val valueToShowInOverviewText = ui.valueToShowInOverview.text.toString().replace("[^\\d]".toRegex(), "")
        if (!StringUtils.isEmpty(valueToShowInOverviewText))
            valueToShowInOverview = Integer.parseInt(valueToShowInOverviewText)

        if (ui.repayment.isChecked) {
            value *= -1
            valueToShowInOverview *= -1
        }
        expense.value = value
        expense.valueToShowInOverview = valueToShowInOverview
        expense.chargedNextMonth = ui.payNextMonth.isChecked
        expense.showInOverview(ui.showInOverview.isChecked)
        expense.showInResume(ui.showInResume.isChecked)
        expense.installments = installment
        expense.repetition = repetition
        return expense
    }

    override fun finishView() {
        val i = Intent()
        i.putExtra(KEY_EXPENSE_UUID, presenter.uuid)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    override fun showError(error: ValidationError) {
        when (error) {
            ValidationError.NAME -> ui.name.error = getString(error.message)
            ValidationError.AMOUNT -> ui.value.error = getString(error.message)
            ValidationError.CHARGEABLE -> ui.chargeable.error = getString(error.message)
            else -> Timber.e("Validation unrecognized, field:" + error)
        }
    }

    private val installment: Int
        get() = Integer.parseInt(ui.installment.text.toString())

    val repetition: Int
        get() = Integer.parseInt(ui.repetition.text.toString())

    override fun showExpense(expense: Expense) {
        ui.name.setText(expense.name)
        ui.value.setText(Math.abs(expense.value).toCurrencyFormatted())
        ui.valueToShowInOverview.setText(Math.abs(expense.valueToShowInOverview).toCurrencyFormatted())
        if (expense.charged) {
            ui.value.setTextColor(ResourcesCompat.getColor(resources, R.color.value_unpaid, null))
            ui.repayment.isEnabled = false
        }
        if (expense.value < 0)
            ui.repayment.isChecked = true

        ui.payNextMonth.isChecked = expense.chargedNextMonth
        ui.showInOverview.isChecked = expense.isShowInOverview
        ui.showInResume.isChecked = expense.isShowInResume
    }

    companion object {
        val KEY_EXPENSE_UUID = ExpensePresenter.KEY_EXPENSE_UUID
        private val REQUEST_SELECT_CHARGEABLE = 1003
        private val REQUEST_SELECT_BILL = 1004
    }
}

private class EditExpenseActivityUi : AnkoComponent<EditExpenseActivity> {
    lateinit var toolbar : TemplateToolbar
    lateinit var name : AppCompatEditText
    lateinit var date : AppCompatEditText
    lateinit var value : AppCompatEditText
    lateinit var valueToShowInOverview : AppCompatEditText
    lateinit var repayment : CheckBox
    lateinit var chargeable : AppCompatEditText
    lateinit var bill : AppCompatEditText
    lateinit var payNextMonth : CheckBox
    lateinit var showInOverview : CheckBox
    lateinit var showInResume : CheckBox
    lateinit var repetition : AppCompatEditText
    lateinit var installment : AppCompatEditText

    override fun createView(ui: AnkoContext<EditExpenseActivity>) = with(ui) {
        verticalLayout {
            toolbar = toolbarTemplate {}

            scrollView {
                padding = resources.getDimensionPixelSize(R.dimen.default_spacing)
                linearLayout {
                    orientation = LinearLayout.VERTICAL
                    textInputLayout {
                        name = appCompatEditText {
                            id = R.id.act_edit_expense_name
                            hint = resources.getString(R.string.name)
                        }
                    }
                    textInputLayout {
                        date = clickableView {
                            id = R.id.act_edit_expense_date
                            hint = resources.getString(R.string.date)
                            onClick { ui.owner.onDate() }
                        }
                    }
                    textInputLayout {
                        value = appCompatEditText {
                            id = R.id.act_edit_expense_value
                            hint = resources.getString(R.string.value)
                            inputType = InputType.TYPE_CLASS_NUMBER
                        }
                    }
                    textInputLayout {
                        valueToShowInOverview = appCompatEditText {
                            id = R.id.act_edit_expense_value_to_show_in_overview
                            hint = resources.getString(R.string.income_to_show_in_overview)
                            inputType = InputType.TYPE_CLASS_NUMBER
                        }
                    }
                    repayment = checkBox {
                        id = R.id.act_edit_expense_repayment
                        text = resources.getString(R.string.repayment)
                    }.lparams {
                        bottomMargin = dip(5)
                    }
                    textInputLayout {
                        chargeable = clickableView {
                            id = R.id.act_edit_expense_chargeable
                            hint = resources.getString(R.string.paid_with)
                            onClick { ui.owner.onChargeable() }
                        }
                    }
                    textInputLayout {
                        bill = clickableView {
                            id = R.id.act_edit_expense_bill
                            hint = resources.getString(R.string.bill)
                            onClick { ui.owner.onBill() }
                        }
                    }
                    payNextMonth = checkBox {
                        id = R.id.act_edit_expense_pay_next_month
                        text = resources.getString(R.string.pay_next_month)
                        visibility = View.GONE
                    }.lparams {
                        bottomMargin = dip(5)
                    }
                    showInOverview = checkBox {
                        id = R.id.act_edit_expense_show_in_overview
                        text = resources.getString(R.string.show_in_overview)
                        isChecked = true
                    }.lparams {
                        bottomMargin = dip(5)
                    }
                    showInResume = checkBox {
                        id = R.id.act_edit_expense_show_in_resume
                        text = resources.getString(R.string.show_in_resume)
                        isChecked = true
                    }.lparams {
                        bottomMargin = dip(5)
                    }
                    textInputLayout {
                        repetition = appCompatEditText {
                            id = R.id.act_edit_expense_repetition
                            hint = resources.getString(R.string.repetition)
                            inputType = InputType.TYPE_CLASS_NUMBER
                            setText(R.string._1)
                        }
                    }
                    textInputLayout {
                        installment = appCompatEditText {
                            id = R.id.act_edit_expense_installment
                            hint = resources.getString(R.string.installments)
                            inputType = InputType.TYPE_CLASS_NUMBER
                            setText(R.string._1)
                        }
                    }
                }.lparams(height = wrapContent, width = matchParent)
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
