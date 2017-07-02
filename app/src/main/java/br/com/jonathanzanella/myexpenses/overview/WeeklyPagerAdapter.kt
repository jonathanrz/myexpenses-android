package br.com.jonathanzanella.myexpenses.overview

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import br.com.jonathanzanella.myexpenses.Environment
import br.com.jonathanzanella.myexpenses.helpers.DateHelper
import org.joda.time.DateTime
import java.util.*

class WeeklyPagerAdapter internal constructor(private val context: Context, month: DateTime, private val builder: WeeklyPagerAdapterBuilder) : PagerAdapter() {
    private val periods = ArrayList<Period>()

    class Period {
        var init: DateTime? = null
        var end: DateTime? = null

        internal fun titleize(): String {
            return String.format(Environment.PTBR_LOCALE, "%02d - %02d", init!!.dayOfMonth, end!!.dayOfMonth)
        }
    }

    init {

        var init = DateHelper.firstMillisOfDay(month.withDayOfMonth(1))

        while (init.monthOfYear == month.monthOfYear) {
            val period = Period()
            period.init = init
            period.end = init.plusDays(LAST_DAY_OF_WEEK)
            if (period.end!!.monthOfYear > month.monthOfYear) {
                period.end!!.minusMonths(1)
                period.end = DateHelper.lastMillisOfDay(init.dayOfMonth().withMaximumValue())
            }
            init = init.plusDays(TOTAL_DAYS_OF_WEEK)
            periods.add(period)
        }
    }

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val view = builder.buildView(context, periods[position])
        collection.addView(view)
        view.refreshData()
        return view
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return periods.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getPageTitle(position: Int): CharSequence {
        return periods[position].titleize()
    }

    internal fun getPositionOfDay(day: Int): Int {
        for (i in periods.indices) {
            val period = periods[i]
            if (day >= period.init!!.dayOfMonth && day <= period.end!!.dayOfMonth)
                return i
        }

        return 0
    }

    companion object {
        private val TOTAL_DAYS_OF_WEEK = 7
        private val LAST_DAY_OF_WEEK = 6
    }
}
