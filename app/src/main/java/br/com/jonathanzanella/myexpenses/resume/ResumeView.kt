package br.com.jonathanzanella.myexpenses.resume

import android.content.Context
import android.support.design.widget.TabLayout
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.views.BaseView
import br.com.jonathanzanella.myexpenses.views.TabableView
import kotlinx.android.synthetic.main.view_resume.view.*
import org.joda.time.DateTime

class ResumeView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), BaseView, TabableView {
    override var filter = ""

    init {
        View.inflate(context, R.layout.view_resume, this)

        val adapter = MonthlyPagerAdapter(context, object : MonthlyPagerAdapterBuilder {
            override fun buildView(ctx: Context, date: DateTime): View {
                return ResumeMonthlyView(ctx, date)
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
