package br.com.jonathanzanella.myexpenses.expense

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
import org.jetbrains.anko.*
import org.joda.time.DateTime

@SuppressLint("ViewConstructor")
internal class ExpenseMonthlyView(context: Context, private val dateTime: DateTime) : FrameLayout(context), RefreshableView, FilterableView {

    override var filter = ""
    private val ui = ExpenseMonthlyViewUI()
    private var adapter: ExpenseAdapter = ExpenseAdapter()

    init {
        addView(ui.createView(AnkoContext.Companion.create(context, this)))

        ui.expenses.adapter = adapter
        ui.expenses.layoutManager = GridLayoutManager(context, 2)
        ui.expenses.itemAnimator = DefaultItemAnimator()
    }

    override fun refreshData() {
        doAsync {
            adapter.loadData(dateTime)

            uiThread { adapter.notifyDataSetChanged() }
        }
    }

    override fun filter(s: String) {
        super.filter(s)
        adapter.filter(s)
        adapter.notifyDataSetChanged()
    }
}

private class ExpenseMonthlyViewUI: AnkoComponent<ExpenseMonthlyView> {
    lateinit var expenses : RecyclerView

    override fun createView(ui: AnkoContext<ExpenseMonthlyView>) = with(ui) {
        frameLayout {
            expenses = recyclerView { id = R.id.view_expenses_monthly_list }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
