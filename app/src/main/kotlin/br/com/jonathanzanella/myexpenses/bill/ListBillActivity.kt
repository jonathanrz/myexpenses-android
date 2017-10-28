package br.com.jonathanzanella.myexpenses.bill

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.views.anko.TemplateToolbar
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.recyclerView
import br.com.jonathanzanella.myexpenses.views.anko.toolbarTemplate
import org.jetbrains.anko.*
import javax.inject.Inject

class ListBillActivity : AppCompatActivity(), BillAdapterCallback {

    @Inject
    lateinit var adapter: BillAdapter
    private val ui = ListBillActivityUi()

    init {
        App.getAppComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)

        ui.toolbar.title = getString(R.string.select_bill_title)
        ui.toolbar.setup(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        adapter.setCallback(this)

        ui.bills.adapter = adapter
        ui.bills.setHasFixedSize(true)
        ui.bills.layoutManager = GridLayoutManager(this, 2)
        ui.bills.itemAnimator = DefaultItemAnimator()
    }

    override fun onDestroy() {
        adapter.onDestroy()
        super.onDestroy()
    }

    override fun onBillSelected(bill: Bill) {
        val i = Intent()
        i.putExtra(KEY_BILL_SELECTED_UUID, bill.uuid)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    companion object {
        val KEY_BILL_SELECTED_UUID = "KeyBillSelectUuid"
    }
}

class ListBillActivityUi : AnkoComponent<ListBillActivity> {
    lateinit var toolbar : TemplateToolbar
    lateinit var bills: RecyclerView

    override fun createView(ui: AnkoContext<ListBillActivity>) = with(ui) {
        verticalLayout {
            toolbar = toolbarTemplate {}
            bills = recyclerView { id = R.id.act_bill_list}
        }
    }.applyRecursively(::applyTemplateViewStyles)
}
