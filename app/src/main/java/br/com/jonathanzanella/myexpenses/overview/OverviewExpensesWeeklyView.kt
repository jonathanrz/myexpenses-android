package br.com.jonathanzanella.myexpenses.overview

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.view.View
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.expense.ExpenseWeeklyOverviewAdapter
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper
import br.com.jonathanzanella.myexpenses.views.BaseView
import kotlinx.android.synthetic.main.view_overview_expenses_weekly.view.*

@SuppressLint("ViewConstructor")
internal class OverviewExpensesWeeklyView(context: Context, private val period: WeeklyPagerAdapter.Period) : BaseView(context) {
    private val expenseRepository: ExpenseRepository = ExpenseRepository(RepositoryImpl<Expense>(context))
    private var adapter = ExpenseWeeklyOverviewAdapter()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        View.inflate(context, R.layout.view_overview_expenses_weekly, this)

        list.adapter = adapter
        list.setHasFixedSize(true)
        list.layoutManager = GridLayoutManager(context, 1)
    }

    override fun init() {
    }

    override fun refreshData() {
        super.refreshData()

        adapter.setExpenses(expenseRepository.expenses(period))
        adapter.notifyDataSetChanged()

        total.text = CurrencyHelper.format(adapter.totalValue)
    }
}