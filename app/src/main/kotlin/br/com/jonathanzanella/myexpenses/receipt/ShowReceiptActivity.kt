package br.com.jonathanzanella.myexpenses.receipt

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.UiThread
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

class ShowReceiptActivity : AppCompatActivity(), ReceiptContract.View {
    @Inject
    lateinit var presenter: ReceiptPresenter
    override val context = this
    private val ui = ShowReceiptActivityUi()

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
        presenter.refreshReceipt()
    }

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
                val i = Intent(this, EditReceiptActivity::class.java)
                i.putExtra(EditReceiptActivity.KEY_RECEIPT_UUID, presenter.uuid)
                startActivityForResult(i, EDIT_RECEIPT)
            }
            R.id.action_delete -> {
                presenter.delete(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_RECEIPT && resultCode == Activity.RESULT_OK) {
            presenter.refreshReceipt()
        }
    }

    @UiThread
    override fun showReceipt(receipt: Receipt) {
        ui.receiptName.text = receipt.name
        ui.receiptDate.text = Transaction.SIMPLE_DATE_FORMAT.format(receipt.getDate().toDate())
        ui.receiptIncome.text = receipt.income.toCurrencyFormatted()

        doAsync {
            val source = receipt.source
            val account = receipt.accountFromCache

            uiThread {
                ui.receiptSource.text = source?.name
                ui.receiptAccount.text = account?.name
            }
        }

        ui.receiptShowInResume.setText(if (receipt.isShowInResume) R.string.yes else R.string.no)
    }

    companion object {
        val KEY_RECEIPT_UUID = ReceiptPresenter.KEY_RECEIPT_UUID
        private val EDIT_RECEIPT = 1001
    }
}

private class ShowReceiptActivityUi : AnkoComponent<ShowReceiptActivity> {
    lateinit var toolbar : TemplateToolbar
    lateinit var receiptName : TextView
    lateinit var receiptDate : TextView
    lateinit var receiptIncome : TextView
    lateinit var receiptSource : TextView
    lateinit var receiptAccount : TextView
    lateinit var receiptShowInResume : TextView

    override fun createView(ui: AnkoContext<ShowReceiptActivity>) = with(ui) {
        verticalLayout {
            toolbar = toolbarTemplate {}

            tableViewFrame {
                tableRow {
                    static { text = resources.getString(R.string.name) }
                    receiptName = staticWithData { id = R.id.act_show_receipt_name }
                }
                tableRow {
                    static { text = resources.getString(R.string.date) }
                    receiptDate = staticWithData { id = R.id.act_show_receipt_date }
                }
                tableRow {
                    static { text = resources.getString(R.string.income) }
                    receiptIncome = staticWithData { id = R.id.act_show_receipt_income }
                }
                tableRow {
                    static { text = resources.getString(R.string.source) }
                    receiptSource = staticWithData { id = R.id.act_show_receipt_source }
                }
                tableRow {
                    static { text = resources.getString(R.string.account) }
                    receiptAccount = staticWithData { id = R.id.act_show_receipt_account }
                }
                tableRow {
                    static { text = resources.getString(R.string.show_in_resume) }
                    receiptShowInResume = staticWithData { id = R.id.act_show_receipt_show_in_resume }
                }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
