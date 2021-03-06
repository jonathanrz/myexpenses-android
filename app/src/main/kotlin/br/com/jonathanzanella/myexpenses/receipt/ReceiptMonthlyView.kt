package br.com.jonathanzanella.myexpenses.receipt

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.views.FilterableView
import br.com.jonathanzanella.myexpenses.views.RefreshableView
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.recyclerView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.applyRecursively
import org.jetbrains.anko.frameLayout
import org.joda.time.DateTime

@SuppressLint("ViewConstructor")
internal class ReceiptMonthlyView(context: Context, private val dateTime: DateTime) : FrameLayout(context), RefreshableView, FilterableView {
    override var filter = ""
    private val ui = ReceiptMonthlyViewUI()
    private var adapter = ReceiptAdapter()

    init {
        addView(ui.createView(AnkoContext.Companion.create(context, this)))

        adapter.loadData(dateTime)
        adapter.notifyDataSetChanged()

        ui.receipts.adapter = adapter
        ui.receipts.layoutManager = GridLayoutManager(context, 2)
        ui.receipts.itemAnimator = DefaultItemAnimator()
    }

    override fun refreshData() {
        adapter.loadData(dateTime)
        adapter.notifyDataSetChanged()
    }

    override fun filter(s: String) {
        super.filter(s)
        adapter.filter(s)
        adapter.notifyDataSetChanged()
    }
}

private class ReceiptMonthlyViewUI: AnkoComponent<ReceiptMonthlyView> {
    lateinit var receipts : RecyclerView

    override fun createView(ui: AnkoContext<ReceiptMonthlyView>) = with(ui) {
        frameLayout {
            receipts = recyclerView { id = R.id.view_receipts_monthly_list }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
