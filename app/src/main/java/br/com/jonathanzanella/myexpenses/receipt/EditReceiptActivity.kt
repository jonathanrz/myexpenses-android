package br.com.jonathanzanella.myexpenses.receipt

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountRepository
import br.com.jonathanzanella.myexpenses.account.ListAccountActivity
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper
import br.com.jonathanzanella.myexpenses.helpers.CurrencyTextWatch
import br.com.jonathanzanella.myexpenses.log.Log
import br.com.jonathanzanella.myexpenses.source.ListSourceActivity
import br.com.jonathanzanella.myexpenses.source.Source
import br.com.jonathanzanella.myexpenses.source.SourceRepository
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.apache.commons.lang3.StringUtils
import org.jetbrains.anko.*
import org.joda.time.DateTime

class EditReceiptActivity : AppCompatActivity(), ReceiptContract.EditView {

    private val ui = EditReceiptActivityUi()
    private val presenter: ReceiptPresenter = ReceiptPresenter(ReceiptRepository(RepositoryImpl<Receipt>(this)),
            SourceRepository(RepositoryImpl<Source>(this)),
            AccountRepository(RepositoryImpl<Account>(this)))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)

        storeBundle(savedInstanceState)
        storeBundle(intent.extras)

        ui.toolbar.setup(this)
        ui.editIncome.addTextChangedListener(CurrencyTextWatch(ui.editIncome))

        presenter.attachView(this)
        presenter.viewUpdated(false)
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
        presenter.updateView()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.attachView(this)
        when (requestCode) {
            REQUEST_SELECT_SOURCE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val sourceUUid = data.getStringExtra(ListSourceActivity.KEY_SOURCE_SELECTED_UUID)
                    if (sourceUUid != null)
                        presenter.onSourceSelected(sourceUUid)
                }
            }
            REQUEST_SELECT_ACCOUNT -> {
                if (resultCode == Activity.RESULT_OK) {
                    val accountUuid = data.getStringExtra(ListAccountActivity.KEY_ACCOUNT_SELECTED_UUID)
                    if (accountUuid != null)
                        presenter.onAccountSelected(accountUuid)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> save()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDateChanged(date: DateTime) {
        ui.editDate.setText(Receipt.SIMPLE_DATE_FORMAT.format(date.toDate()))
    }

    override fun onSourceSelected(source: Source) {
        ui.editSource.setText(source.name)
    }

    override fun onAccountSelected(account: Account) {
        ui.editAccount.setText(account.name)
    }

    override fun getInstallment(): Int {
        return Integer.parseInt(ui.editInstallment.text.toString())
    }

    override fun getRepetition(): Int {
        return Integer.parseInt(ui.editRepetition.text.toString())
    }

    internal fun onDate() {
        presenter.onDate(this)
    }

    internal fun onSource() {
        startActivityForResult(Intent(this, ListSourceActivity::class.java), REQUEST_SELECT_SOURCE)
    }

    internal fun onAccount() {
        if (!presenter.hasReceipt())
            startActivityForResult(Intent(this, ListAccountActivity::class.java), REQUEST_SELECT_ACCOUNT)
    }

    private fun save() {
        presenter.save()
    }

    override fun fillReceipt(receipt: Receipt): Receipt {
        receipt.name = ui.editName.text.toString()
        val income = ui.editIncome.text.toString().replace("[^\\d]".toRegex(), "")
        if (!StringUtils.isEmpty(income))
            receipt.income = Integer.parseInt(income)
        receipt.isShowInResume = ui.checkShowInResume.isChecked
        receipt.installments = installment
        receipt.repetition = repetition
        return receipt
    }

    override fun finishView() {
        val i = Intent()
        i.putExtra(KEY_RECEIPT_UUID, presenter.uuid)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    override fun showError(error: ValidationError) {
        when (error) {
            ValidationError.NAME -> ui.editName.error = getString(error.message)
            ValidationError.AMOUNT -> ui.editIncome.error = getString(error.message)
            ValidationError.SOURCE -> ui.editSource.error = getString(error.message)
            ValidationError.ACCOUNT -> ui.editAccount.error = getString(error.message)
            else -> Log.error(this.javaClass.name, "Validation unrecognized, field:" + error)
        }
    }

    @UiThread
    override fun showReceipt(receipt: Receipt) {
        ui.editName.setText(receipt.name)
        ui.editIncome.setText(CurrencyHelper.format(receipt.income))
        if (receipt.isCredited)
            ui.editIncome.setTextColor(ResourcesCompat.getColor(resources, R.color.value_unpaid, null))

        object : AsyncTask<Void, Void, Source>() {

            override fun doInBackground(vararg voids: Void): Source {
                return receipt.source
            }

            override fun onPostExecute(source: Source) {
                super.onPostExecute(source)
                ui.editSource.setText(source.name)
            }
        }.execute()

        ui.checkShowInResume.isChecked = receipt.isShowInResume
    }

    companion object {
        val KEY_RECEIPT_UUID = "KeyReceiptUuid"
        private val REQUEST_SELECT_SOURCE = 1001
        private val REQUEST_SELECT_ACCOUNT = 1002
    }
}

private class EditReceiptActivityUi : AnkoComponent<EditReceiptActivity> {
    lateinit var toolbar : TemplateToolbar
    lateinit var editName : AppCompatEditText
    lateinit var editDate: AppCompatEditText
    lateinit var editIncome: AppCompatEditText
    lateinit var editSource: AppCompatEditText
    lateinit var editAccount: AppCompatEditText
    lateinit var checkShowInResume: CheckBox
    lateinit var editRepetition: AppCompatEditText
    lateinit var editInstallment: AppCompatEditText

    override fun createView(ui: AnkoContext<EditReceiptActivity>) = with(ui) {
        scrollView {
            verticalLayout {
                toolbar = toolbarTemplate {}

                tableViewFrame {
                    textInputLayout {
                        editName = appCompatEditText {
                            id = R.id.act_edit_receipt_name
                            hint = resources.getString(R.string.name)
                        }
                    }
                    textInputLayout {
                        editDate = clickableView {
                            id = R.id.act_edit_receipt_date
                            hint = resources.getString(R.string.date)
                            onClick { ui.owner.onDate() }
                        }
                    }
                    textInputLayout {
                        editIncome = appCompatEditText {
                            id = R.id.act_edit_receipt_income
                            hint = resources.getString(R.string.income)
                            inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                        }
                    }
                    textInputLayout {
                        editSource = clickableView {
                            id = R.id.act_edit_receipt_source
                            hint = resources.getString(R.string.source)
                            onClick { ui.owner.onSource() }
                        }
                    }
                    textInputLayout {
                        editAccount = clickableView {
                            id = R.id.act_edit_receipt_account
                            hint = resources.getString(R.string.account)
                            onClick { ui.owner.onAccount() }
                        }
                    }
                    checkShowInResume = checkBox {
                        id = R.id.act_edit_receipt_show_in_resume
                        hint = resources.getString(R.string.show_in_resume)
                        isChecked = true
                    }.lparams {
                        bottomMargin = dip(5)
                    }
                    textInputLayout {
                        editRepetition = appCompatEditText {
                            id = R.id.act_edit_receipt_repetition
                            hint = resources.getString(R.string.repetition)
                            inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                            setText(R.string._1)
                        }
                    }
                    textInputLayout {
                        editInstallment = appCompatEditText {
                            id = R.id.act_edit_receipt_installment
                            hint = resources.getString(R.string.installments)
                            inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                            setText(R.string._1)
                        }
                    }
                }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}