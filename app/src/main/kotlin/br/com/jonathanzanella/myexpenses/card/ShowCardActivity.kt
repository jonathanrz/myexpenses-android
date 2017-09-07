package br.com.jonathanzanella.myexpenses.card

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.expense.EditExpenseActivity
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.helpers.ResourcesHelper
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.jetbrains.anko.*
import org.joda.time.DateTime
import javax.inject.Inject

class ShowCardActivity : AppCompatActivity(), CardContract.View {
    @Inject
    lateinit var accountDataSource: AccountDataSource
    @Inject
    lateinit var cardRepository: CardRepository
    @Inject
    lateinit var expenseRepository: ExpenseRepository
    override val context = this
    private var presenter: CardPresenter
    private val ui = ShowCardActivityUi()

    init {
        App.getAppComponent().inject(this)
        presenter = CardPresenter(cardRepository, accountDataSource, expenseRepository, ResourcesHelper(this))
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
        presenter.viewUpdated(false)
    }

    @UiThread
    fun storeBundle(extras: Bundle?) {
        doAsync {
            if (extras?.containsKey(KEY_CREDIT_CARD_UUID) == true)
                presenter.loadCard(extras.getString(KEY_CREDIT_CARD_UUID))

            uiThread { presenter.updateView() }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CREDIT_CARD_UUID, presenter.uuid)
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
        menuInflater.inflate(R.menu.edit, menu)
        return true
    }

    @UiThread
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                val i = Intent(this, EditCardActivity::class.java)
                i.putExtra(EditCardActivity.KEY_CARD_UUID, presenter.uuid)
                startActivityForResult(i, EDIT_CARD)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_CARD && resultCode == Activity.RESULT_OK)
            presenter.reloadCard()
    }



    @UiThread
    override fun showCard(card: Card) {
        doAsync {
            val account = card.account!!

            uiThread { ui.cardAccount.text = account.name }
        }

        ui.cardName.text = card.name

        when (card.type) {
            CardType.CREDIT -> ui.cardType.setText(R.string.credit)
            CardType.DEBIT -> ui.cardType.setText(R.string.debit)
        }
    }

    @UiThread
    fun payCreditCardBill() {
        doAsync {
            val expense = presenter.generateCreditCardBill(DateTime.now().minusMonths(1))
            val ctx = this@ShowCardActivity

            uiThread {
                if (expense != null) {
                    val i = Intent(ctx, EditExpenseActivity::class.java)
                    i.putExtra(EditExpenseActivity.KEY_EXPENSE_UUID, expense.uuid)
                    startActivity(i)
                } else {
                    Toast.makeText(ctx, ctx.getString(R.string.empty_invoice), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        val KEY_CREDIT_CARD_UUID = "KeyCreateCardUuid"
        private val EDIT_CARD = 1001
    }
}

class ShowCardActivityUi : AnkoComponent<ShowCardActivity> {
    lateinit var toolbar : TemplateToolbar
    lateinit var cardName: TextView
    lateinit var cardAccount: TextView
    lateinit var cardType: TextView

    override fun createView(ui: AnkoContext<ShowCardActivity>) = with(ui) {
        verticalLayout {
            toolbar = toolbarTemplate {}

            tableViewFrame {
                tableRow {
                    static { text = resources.getString(R.string.name) }
                    cardName = staticWithData { id = R.id.act_show_card_name }
                }
                tableRow {
                    static { text = resources.getString(R.string.account) }
                    cardAccount = staticWithData { id = R.id.act_show_card_account }
                }
                tableRow {
                    static { text = resources.getString(R.string.type) }
                    cardType = staticWithData { id = R.id.act_show_card_type }
                }
            }.lparams(height = 0, weight = 1f)

            button {
                id = R.id.act_show_card_pay_credit_card_bill
                text = resources.getString(R.string.pay_credit_card_bill)
                onClick { ui.owner.payCreditCardBill() }
            }.lparams(width = matchParent) {
                margin = resources.getDimensionPixelSize(R.dimen.default_spacing)
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
