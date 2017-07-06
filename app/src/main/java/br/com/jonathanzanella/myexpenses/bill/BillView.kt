package br.com.jonathanzanella.myexpenses.bill

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.views.BaseView
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.recyclerView
import org.jetbrains.anko.*
import org.jetbrains.anko.design.floatingActionButton

class BillView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), BaseView {
    override var filter = ""
    private val ui = BillViewUI()
    private var adapter: BillAdapter = BillAdapter()

    init {
        addView(ui.createView(AnkoContext.Companion.create(context, this)))

        ui.bills.adapter = adapter
        ui.bills.layoutManager = GridLayoutManager(context, 2)
        ui.bills.itemAnimator = DefaultItemAnimator()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_ADD_BILL -> if (resultCode == Activity.RESULT_OK)
                refreshData()
        }
    }

    override fun refreshData() {
        super.refreshData()

        adapter.refreshData()
        adapter.notifyDataSetChanged()
    }

    override fun filter(s: String) {
        super.filter(s)
        adapter.filter(s)
        adapter.notifyDataSetChanged()
    }

    companion object {
        val REQUEST_ADD_BILL = 1003
    }
}

class BillViewUI: AnkoComponent<BillView> {
    lateinit var bills : RecyclerView

    override fun createView(ui: AnkoContext<BillView>) = with(ui) {
        frameLayout {
            bills = recyclerView { id = R.id.view_bills_list}
            floatingActionButton {
                id = R.id.view_bills_fab
                onClick {
                    val ctx = context
                    val i = Intent(context, EditBillActivity::class.java)
                    if (ctx is Activity) {
                        ctx.startActivityForResult(i, BillView.REQUEST_ADD_BILL)
                    } else {
                        ctx.startActivity(i)
                    }
                }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
