package br.com.jonathanzanella.myexpenses.overview

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapter
import br.com.jonathanzanella.myexpenses.views.BaseView
import kotlinx.android.synthetic.main.view_overview_expenses_monthly.view.*
import org.joda.time.DateTime

@SuppressLint("ViewConstructor")
internal class OverviewExpensesMonthlyView(context: Context, month: DateTime) : BaseView(context) {
    init {
        val expenseRepository = ExpenseRepository(RepositoryImpl<Expense>(context))

        val adapter = WeeklyPagerAdapter(getContext(), month, WeeklyPagerAdapterBuilder { ctx, period -> OverviewExpensesWeeklyView(ctx, period) })

        pager!!.adapter = adapter
        pager!!.currentItem = MonthlyPagerAdapter.INIT_MONTH_VISIBLE
        tabs!!.setupWithViewPager(pager)

        val now = DateTime.now()
        if (month.monthOfYear == now.monthOfYear && month.year == now.year)
            pager!!.currentItem = adapter.getPositionOfDay(now.dayOfMonth)

        val period = WeeklyPagerAdapter.Period()
        period.init = month.dayOfMonth().withMinimumValue()
        period.end = month.dayOfMonth().withMaximumValue()
        total.text = CurrencyHelper.format(expenseRepository.expenses(period).sumBy { it.valueToShowInOverview })
    }

    override fun init() {
        View.inflate(context, R.layout.view_overview_expenses_monthly, this)
    }
}