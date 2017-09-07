package br.com.jonathanzanella.myexpenses.card

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.card.CardType.CREDIT
import br.com.jonathanzanella.myexpenses.card.CardType.DEBIT
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.helpers.ResourcesHelper
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.jetbrains.anko.*
import timber.log.Timber
import javax.inject.Inject

class EditCardActivity : AppCompatActivity(), CardContract.EditView {
    @Inject
    lateinit var accountDataSource: AccountDataSource
    @Inject
    lateinit var cardDataSource: CardDataSource
    @Inject
    lateinit var expenseRepository: ExpenseRepository
    override val context = this
    private val ui = EditCardActivityUi()
    private val presenter: CardPresenter

    init {
        App.getAppComponent().inject(this)
        presenter = CardPresenter(cardDataSource, accountDataSource, expenseRepository, ResourcesHelper(this))
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
        presenter.viewUpdated(false)
    }

    fun storeBundle(extras: Bundle?) {
        doAsync {
            if (extras?.containsKey(KEY_CARD_UUID) == true)
                presenter.loadCard(extras.getString(KEY_CARD_UUID))

            uiThread { presenter.updateView() }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val uuidCard = presenter.uuid
        if (uuidCard != null)
            outState.putString(KEY_CARD_UUID, uuidCard)
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

    internal fun onAccount() {
        presenter.showSelectAccountActivity(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.attachView(this)
        presenter.onActivityResult(requestCode, resultCode, data!!)
    }

    override fun onAccountSelected(account: Account) {
        ui.editAccount.setText(account.name)
    }

    override fun showCard(card: Card) {
        ui.editName.setText(card.name)
        when (card.type) {
            CREDIT -> ui.radioType.check(R.id.act_edit_card_type_credit)
            DEBIT -> ui.radioType.check(R.id.act_edit_card_type_debit)
        }
    }

    override fun fillCard(card: Card): Card {
        card.name = ui.editName.text.toString()
        when (ui.radioType.checkedRadioButtonId) {
            R.id.act_edit_card_type_credit -> {
                card.type = CREDIT
            }
            R.id.act_edit_card_type_debit -> {
                card.type = DEBIT
            }
        }
        return card
    }

    override fun finishView() {
        val i = Intent()
        i.putExtra(KEY_CARD_UUID, presenter.uuid)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    override fun showError(error: ValidationError) {
        when (error) {
            ValidationError.NAME -> ui.editName.error = getString(error.message)
            ValidationError.CARD_TYPE -> Snackbar.make(contentView!!, getString(error.message), Snackbar.LENGTH_SHORT).show()
            ValidationError.ACCOUNT -> ui.editAccount.error = getString(error.message)
            else -> Timber.e("Validation unrecognized, field:" + error)
        }
    }

    companion object {
        val KEY_CARD_UUID = "KeyCardUuid"
    }
}

class EditCardActivityUi : AnkoComponent<EditCardActivity> {
    private lateinit var contentView: View
    lateinit var toolbar : TemplateToolbar
    lateinit var editName: EditText
    lateinit var editAccount: EditText
    lateinit var radioType: RadioGroup

    override fun createView(ui: AnkoContext<EditCardActivity>) = with(ui) {
        contentView = verticalLayout {
            toolbar = toolbarTemplate {}

            scrollView {
                verticalLayout {
                    textInputLayout {
                        editName = appCompatEditText {
                            id = R.id.act_edit_card_name
                            hint = resources.getString(R.string.name)
                        }
                    }
                    textInputLayout {
                        editAccount = clickableView {
                            id = R.id.act_edit_card_account
                            hint = resources.getString(R.string.account)
                            onClick { ui.owner.onAccount() }
                        }
                    }

                    textView { text = resources.getString(R.string.select_card_type) }
                    view {
                        backgroundColor = ResourcesCompat.getColor(resources, R.color.color_divider, null)
                    }.lparams(width = matchParent, height = dip(1)) {
                        bottomMargin = resources.getDimensionPixelSize(R.dimen.min_spacing)
                    }

                    radioType = radioGroup {
                        id = R.id.act_edit_card_type
                        orientation = RadioGroup.HORIZONTAL

                        radioButton {
                            id = R.id.act_edit_card_type_debit
                            hint = resources.getString(R.string.debit)
                        }.lparams {
                            marginEnd = resources.getDimensionPixelSize(R.dimen.default_spacing)
                        }
                        radioButton {
                            id = R.id.act_edit_card_type_credit
                            hint = resources.getString(R.string.credit)
                        }
                    }.lparams(width = matchParent) {
                        bottomMargin = resources.getDimensionPixelSize(R.dimen.default_spacing)
                    }
                }.lparams {
                    margin = resources.getDimensionPixelSize(R.dimen.default_spacing)
                }
            }
        }.applyRecursively(::applyTemplateViewStyles)

        contentView
    }
}
