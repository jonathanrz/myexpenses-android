package br.com.jonathanzanella.myexpenses.resume

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import br.com.jonathanzanella.myexpenses.views.RefreshableView
import org.joda.time.DateTime
import java.util.*

class MonthlyPagerAdapter(private val context: Context, private val builder: MonthlyPagerAdapterBuilder) : PagerAdapter() {

    private val helper = MonthlyPagerAdapterHelper()
    private val months = ArrayList<DateTime>()

    init {

        val initTime = DateTime.now().minusMonths(INIT_MONTH_VISIBLE).withTime(0, 0, 0, 0).withDayOfMonth(1)

        (0 until TOTAL_MONTHS_VISIBLE).mapTo(months) { initTime.plusMonths(it) }
    }

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val view = builder.buildView(context, months[position])
        collection.addView(view)
        if(view is RefreshableView)
            view.refreshData()
        return view
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return months.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getPageTitle(position: Int): CharSequence {
        return helper.formatMonthForView(months[position])
    }

    fun getDatePosition(date: DateTime): Int {
        for (i in months.indices) {
            val d = months[i]
            if (d.monthOfYear == date.monthOfYear && d.year == date.year)
                return i
        }

        return 0
    }

    fun getDate(position: Int): DateTime {
        return months[position]
    }

    companion object {
        val TOTAL_MONTHS_VISIBLE = 25
        val INIT_MONTH_VISIBLE = TOTAL_MONTHS_VISIBLE / 2
    }
}
