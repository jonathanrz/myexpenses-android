package br.com.jonathanzanella.myexpenses.bill

import android.content.Context
import android.content.Intent
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.views.FilterableView
import br.com.jonathanzanella.myexpenses.views.TabableView
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.recyclerView
import org.jetbrains.anko.*
import org.jetbrains.anko.design.floatingActionButton
import javax.inject.Inject

class BillView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), FilterableView, TabableView {
    override var filter = ""
    private val ui = BillViewUI()
    @Inject
    lateinit var adapter: BillAdapter

    init {
        App.getAppComponent().inject(this)

        addView(ui.createView(AnkoContext.Companion.create(context, this)))

        ui.bills.adapter = adapter
        ui.bills.layoutManager = GridLayoutManager(context, 2)
        ui.bills.itemAnimator = DefaultItemAnimator()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        adapter.onDestroy()
    }

    override fun filter(s: String) {
        super.filter(s)
        adapter.filter(s)
    }
}

class BillViewUI: AnkoComponent<BillView> {
    lateinit var bills : RecyclerView

    override fun createView(ui: AnkoContext<BillView>) = with(ui) {
        frameLayout {
            bills = recyclerView { id = R.id.view_bills_list}
            floatingActionButton {
                id = R.id.view_bills_fab
                onClick { context.startActivity(Intent(context, EditBillActivity::class.java)) }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
