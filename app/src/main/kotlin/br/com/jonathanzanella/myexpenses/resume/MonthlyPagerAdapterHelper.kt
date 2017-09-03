package br.com.jonathanzanella.myexpenses.resume

import org.joda.time.DateTime

import java.text.SimpleDateFormat
import java.util.Locale

class MonthlyPagerAdapterHelper {

    fun formatMonthForView(month: DateTime): String {
        synchronized(this) {
            return SIMPLE_DATE_FORMAT.format(month.toDate())
        }
    }

    companion object {
        private val SIMPLE_DATE_FORMAT = SimpleDateFormat("MMM/yy", Locale.getDefault())
    }
}