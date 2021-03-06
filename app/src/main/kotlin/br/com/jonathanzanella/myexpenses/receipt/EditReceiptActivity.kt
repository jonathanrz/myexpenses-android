package br.com.jonathanzanella.myexpenses.receipt

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.ListAccountActivity
import br.com.jonathanzanella.myexpenses.helpers.CurrencyTextWatch
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.source.ListSourceActivity
import br.com.jonathanzanella.myexpenses.source.Source
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.apache.commons.lang3.StringUtils
import org.jetbrains.anko.*
import org.joda.time.DateTime
import timber.log.Timber
import javax.inject.Inject

class EditReceiptActivity : AppCompatActivity(), ReceiptContract.EditView {
    @Inject
    lateinit var presenter: ReceiptPresenter
    override val context = this
    private val ui = EditReceiptActivityUi()

    override val installment: Int
        get() = Integer.parseInt(ui.editInstallment.text.toString())
    override val repetition: Int
        get() = Integer.parseInt(ui.editRepetition.text.toString())

    init {
        App.getAppComponent().inject(this)
    }

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

    override fun setTitle(string: String) {
        ui.toolbar.title = string
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.attachView(this)
        when (requestCode) {
            REQUEST_SELECT_SOURCE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val sourceUUid = data!!.getStringExtra(ListSourceActivity.KEY_SOURCE_SELECTED_UUID)
                    if (sourceUUid != null)
                        presenter.onSourceSelected(sourceUUid)
                }
            }
            REQUEST_SELECT_ACCOUNT -> {
                if (resultCode == Activity.RESULT_OK) {
                    val accountUuid = data!!.getStringExtra(ListAccountActivity.KEY_ACCOUNT_SELECTED_UUID)
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

    override fun onDateChanged(balanceDate: DateTime) {
        ui.editDate.setText(Transaction.SIMPLE_DATE_FORMAT.format(balanceDate.toDate()))
    }

    override fun onSourceSelected(source: Source) {
        ui.editSource.setText(source.name)
    }

    override fun onAccountSelected(account: Account) {
        ui.editAccount.setText(account.name)
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

    override fun showConfirmDialog(receipt: Receipt) {
        AlertDialog.Builder(ctx)
                .setMessage("${getString(R.string.message_confirm_receipt)} ${receipt.name} - ${receipt.incomeFormatted}?")
                .setPositiveButton(R.string.yes) { _, _ ->
                    doAsync {
                        receipt.credit()

                        uiThread { finishView() }
                    }
                }
                .setNegativeButton(R.string.no) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    finishView()
                }
                .show()
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
            else -> Timber.e("Validation unrecognized, field:" + error)
        }
    }

    @UiThread
    override fun showReceipt(receipt: Receipt) {
        ui.editName.setText(receipt.name)
        ui.editIncome.setText(receipt.income.toCurrencyFormatted())
        if (receipt.credited)
            ui.editIncome.setTextColor(ResourcesCompat.getColor(resources, R.color.value_unpaid, null))

        doAsync {
            val source = receipt.source

            uiThread { ui.editSource.setText(source?.name) }
        }

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
        verticalLayout {
            toolbar = toolbarTemplate {}
            scrollView {
                verticalLayout {
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
                                inputType = InputType.TYPE_CLASS_NUMBER
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
                                inputType = InputType.TYPE_CLASS_NUMBER
                                setText(R.string._1)
                            }
                        }
                        textInputLayout {
                            editInstallment = appCompatEditText {
                                id = R.id.act_edit_receipt_installment
                                hint = resources.getString(R.string.installments)
                                inputType = InputType.TYPE_CLASS_NUMBER
                                setText(R.string._1)
                            }
                        }
                    }
                }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
