package br.com.jonathanzanella.myexpenses.bill

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.jetbrains.anko.*
import javax.inject.Inject

class ShowBillActivity : AppCompatActivity(), BillContract.View {
    @Inject
    lateinit var presenter: BillPresenter
    override val context = this
    private val ui = ShowBillActivityUi()

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

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        presenter.onViewUpdated(false)
    }

    fun storeBundle(extras: Bundle?) {
        if (extras?.containsKey(KEY_BILL_UUID) == true) {
            doAsync {
                presenter.loadBill(extras.getString(KEY_BILL_UUID))

                uiThread { presenter.updateView() }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_BILL_UUID, presenter.uuid)
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                val i = Intent(this, EditBillActivity::class.java)
                i.putExtra(EditBillActivity.KEY_BILL_UUID, presenter.uuid)
                startActivity(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showBill(bill: Bill) {
        ui.apply {
            billName.text = bill.name
            billAmount.text = bill.amount.toCurrencyFormatted()
            billDueDate.text = bill.dueDate.toString()
            billInitDate.text = Transaction.SIMPLE_DATE_FORMAT.format(bill.initDate?.toDate())
            billEndDate.text = Transaction.SIMPLE_DATE_FORMAT.format(bill.endDate?.toDate())
        }
    }

    companion object {
        val KEY_BILL_UUID = "KeyBillUuid"
    }
}

class ShowBillActivityUi : AnkoComponent<ShowBillActivity> {
    lateinit var toolbar : TemplateToolbar
    lateinit var billName: TextView
    lateinit var billAmount: TextView
    lateinit var billDueDate: TextView
    lateinit var billInitDate: TextView
    lateinit var billEndDate: TextView

    override fun createView(ui: AnkoContext<ShowBillActivity>) = with(ui) {
        verticalLayout {
            toolbar = toolbarTemplate {}

            tableViewFrame {
                tableRow {
                    static { text = resources.getString(R.string.name) }
                    billName = staticWithData { id = R.id.act_show_bill_name }
                }
                tableRow {
                    static { text = resources.getString(R.string.amount) }
                    billAmount = staticWithData { id = R.id.act_show_bill_amount }
                }
                tableRow {
                    static { text = resources.getString(R.string.due_date) }
                    billDueDate = staticWithData { id = R.id.act_show_bill_due_date }
                }
                tableRow {
                    static { text = resources.getString(R.string.start) }
                    billInitDate = staticWithData { id = R.id.act_show_bill_init_date }
                }
                tableRow {
                    static { text = resources.getString(R.string.end) }
                    billEndDate = staticWithData { id = R.id.act_show_bill_end_date }
                }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
