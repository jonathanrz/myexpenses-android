package br.com.jonathanzanella.myexpenses.overview

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapter
import kotlinx.android.synthetic.main.view_overview_expenses_monthly.view.*
import org.joda.time.DateTime

@SuppressLint("ViewConstructor")
internal class OverviewExpensesMonthlyView(context: Context, month: DateTime) : FrameLayout(context) {
    init {
        View.inflate(context, R.layout.view_overview_expenses_monthly, this)

        val expenseRepository = ExpenseRepository()

        val adapter = WeeklyPagerAdapter(getContext(), month, object : WeeklyPagerAdapterBuilder {
            override fun buildView(ctx: Context, period: WeeklyPagerAdapter.Period): View {
                return OverviewExpensesWeeklyView(ctx, period)
            }
        })

        pager.adapter = adapter
        pager.currentItem = MonthlyPagerAdapter.INIT_MONTH_VISIBLE
        tabs.setupWithViewPager(pager)

        val now = DateTime.now()
        if (month.monthOfYear == now.monthOfYear && month.year == now.year)
            pager.currentItem = adapter.getPositionOfDay(now.dayOfMonth)

        val period = WeeklyPagerAdapter.Period()
        period.init = month.dayOfMonth().withMinimumValue()
        period.end = month.dayOfMonth().withMaximumValue()
        total.text = expenseRepository.expenses(period).sumBy { it.valueToShowInOverview }.toCurrencyFormatted()
    }
}
