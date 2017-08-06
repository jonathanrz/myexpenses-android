package br.com.jonathanzanella.myexpenses.overview

import android.content.Context
import android.support.design.widget.TabLayout
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapter
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapterBuilder
import br.com.jonathanzanella.myexpenses.views.FilterableView
import br.com.jonathanzanella.myexpenses.views.TabableView
import kotlinx.android.synthetic.main.view_overview_expenses.view.*
import org.joda.time.DateTime

class OverviewExpensesView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), TabableView, FilterableView {
    override var filter = ""

    init {
        super.onAttachedToWindow()
        View.inflate(context, R.layout.view_overview_expenses, this)

        val adapter = MonthlyPagerAdapter(context, object : MonthlyPagerAdapterBuilder {
            override fun buildView(ctx: Context, date: DateTime): View {
                return OverviewExpensesMonthlyView(ctx, date)
            }
        })
        pager.adapter = adapter
        pager.currentItem = MonthlyPagerAdapter.INIT_MONTH_VISIBLE
    }

    override fun setTabs(tabs: TabLayout) {
        tabs.setupWithViewPager(pager)
        tabs.visibility = View.VISIBLE
    }
}
