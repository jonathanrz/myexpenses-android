package br.com.jonathanzanella.myexpenses.overview

import android.content.Context
import android.support.design.widget.TabLayout
import android.util.AttributeSet
import android.view.View
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapter
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapterBuilder
import br.com.jonathanzanella.myexpenses.views.BaseView
import kotlinx.android.synthetic.main.view_overview_expenses.view.*

class OverviewExpensesView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseView(context, attrs, defStyleAttr) {
    override fun init() {
        View.inflate(context, R.layout.view_overview_expenses, this)

        val adapter = MonthlyPagerAdapter(context, MonthlyPagerAdapterBuilder { ctx, date -> OverviewExpensesMonthlyView(ctx, date) })
        pager.adapter = adapter
        pager.currentItem = MonthlyPagerAdapter.INIT_MONTH_VISIBLE
    }

    override fun setTabs(tabs: TabLayout) {
        tabs.setupWithViewPager(pager)
        tabs.visibility = View.VISIBLE
    }
}