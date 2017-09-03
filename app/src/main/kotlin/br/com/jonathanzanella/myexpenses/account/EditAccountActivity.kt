package br.com.jonathanzanella.myexpenses.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.helpers.CurrencyTextWatch
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.apache.commons.lang3.StringUtils
import org.jetbrains.anko.*
import timber.log.Timber

class EditAccountActivity : AppCompatActivity(), AccountContract.EditView {
    override val context = this
    private val ui = EditAccountActivityUi()
    private val presenter = AccountPresenter(AccountRepository())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)

        storeBundle(savedInstanceState)
        storeBundle(intent.extras)

        ui.toolbar.setup(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        ui.editBalance.addTextChangedListener(CurrencyTextWatch(ui.editBalance))
        presenter.viewUpdated(false)
    }

    fun storeBundle(extras: Bundle?) {
        if (extras != null && extras.containsKey(KEY_ACCOUNT_UUID))
            presenter.loadAccount(extras.getString(KEY_ACCOUNT_UUID))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val uuid = presenter.uuid
        if (uuid != null)
            outState.putString(KEY_ACCOUNT_UUID, uuid)
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onStop() {
        presenter.detachView()
        super.onStop()
    }

    override fun setTitle(string: String) {
        ui.toolbar.title = string
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

    override fun fillAccount(account: Account): Account {
        account.apply {
            name = ui.editName.text.toString()
            balance = getBalanceValue()
            accountToPayCreditCard = ui.checkToPayCreditCard.isChecked
            accountToPayBills = ui.checkToPayBill.isChecked
            showInResume = ui.checkShowInResume.isChecked
        }

        return account
    }

    private fun getBalanceValue() : Int {
        val balanceText = ui.editBalance.text.toString().replace("[^\\d]".toRegex(), "")
        val balanceValue = if (StringUtils.isEmpty(balanceText)) 0 else Integer.parseInt(balanceText)
        if (ui.checkAccountBalanceNegative.isChecked)
            return balanceValue * -1
        return balanceValue
    }

    override fun showAccount(account: Account) {
        ui.apply {
            editName.setText(account.name)
            val balance = account.balance
            if (balance > 0) {
                editBalance.setText(balance.toCurrencyFormatted())
                checkAccountBalanceNegative.isChecked = false
            } else {
                editBalance.setText((balance * -1).toCurrencyFormatted())
                checkAccountBalanceNegative.isChecked = true
            }
            checkToPayCreditCard.isChecked = account.accountToPayCreditCard
            checkToPayBill.isChecked = account.accountToPayBills
            checkShowInResume.isChecked = account.showInResume
        }
    }

    override fun showError(error: ValidationError) {
        when (error) {
            ValidationError.NAME -> ui.editName.error = getString(error.message)
            else -> Timber.e("Validation unrecognized, field:" + error)
        }
    }

    override fun finishView() {
        val i = Intent()
        i.putExtra(KEY_ACCOUNT_UUID, presenter.uuid)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    companion object {
        val KEY_ACCOUNT_UUID = "KeyAccountUuid"
    }
}

private class EditAccountActivityUi : AnkoComponent<EditAccountActivity> {
    lateinit var toolbar : TemplateToolbar
    lateinit var editName : AppCompatEditText
    lateinit var editBalance: AppCompatEditText
    lateinit var checkAccountBalanceNegative: CheckBox
    lateinit var checkToPayCreditCard: CheckBox
    lateinit var checkToPayBill: CheckBox
    lateinit var checkShowInResume: CheckBox

    override fun createView(ui: AnkoContext<EditAccountActivity>) = with(ui) {
        verticalLayout {
            toolbar = toolbarTemplate {}

            scrollView {
                linearLayout {
                    tableViewFrame {
                        textInputLayout {
                            editName = appCompatEditText {
                                id = R.id.act_edit_account_name
                                hint = resources.getString(R.string.name)
                            }
                        }
                        textInputLayout {
                            editBalance = appCompatEditText {
                                id = R.id.act_edit_account_balance
                                hint = resources.getString(R.string.balance)
                            }
                        }
                        checkAccountBalanceNegative = checkBox {
                            id = R.id.act_edit_account_negative
                            hint = resources.getString(R.string.negative)
                        }.lparams { bottomMargin = dip(5) }
                        checkToPayCreditCard = checkBox {
                            id = R.id.act_edit_account_to_pay_credit_card
                            hint = resources.getString(R.string.account_to_pay_credit_card)
                        }.lparams { bottomMargin = dip(5) }
                        checkToPayBill = checkBox {
                            id = R.id.act_edit_account_to_pay_bills
                            hint = resources.getString(R.string.account_to_pay_bills)
                        }.lparams { bottomMargin = dip(5) }
                        checkShowInResume = checkBox {
                            id = R.id.act_edit_account_show_in_resume
                            hint = resources.getString(R.string.show_in_resume)
                        }.lparams { bottomMargin = dip(5) }
                    }
                }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}