package br.com.jonathanzanella.myexpenses.overview

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.expense.ExpenseWeeklyOverviewAdapter
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.views.FilterableView
import br.com.jonathanzanella.myexpenses.views.RefreshableView
import kotlinx.android.synthetic.main.view_overview_expenses_weekly.view.*

@SuppressLint("ViewConstructor")
internal class OverviewExpensesWeeklyView(context: Context, private val period: WeeklyPagerAdapter.Period) : FrameLayout(context), FilterableView, RefreshableView {
    override var filter = ""
    private val expenseRepository: ExpenseRepository = ExpenseRepository()
    private var adapter = ExpenseWeeklyOverviewAdapter()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        View.inflate(context, R.layout.view_overview_expenses_weekly, this)

        list.adapter = adapter
        list.setHasFixedSize(true)
        list.layoutManager = GridLayoutManager(context, 1)
    }

    override fun refreshData() {
        adapter.setExpenses(expenseRepository.expenses(period))
        adapter.notifyDataSetChanged()

        total.text = adapter.totalValue.toCurrencyFormatted()
    }
}
