package br.com.jonathanzanella.myexpenses.views

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.R
import kotlinx.android.synthetic.main.view_datetime.view.*
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

class DateTimeView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private var listener: Listener? = null

    var currentTime: DateTime? = null
        private set

    interface Listener {
        fun onDateTimeChanged(currentTime: DateTime)
    }

    init {
        View.inflate(context, R.layout.view_datetime, this)
        date.setOnClickListener { onDate() }
        hour.setOnClickListener { onHour() }
    }

    fun setDate(currentTime: DateTime) {
        this.currentTime = currentTime
        onDateChanged()
    }

    private fun onDateChanged() {
        val time = currentTime!!
        date.setText(dateFormat.format(time.toDate()))
        hour.setText(hourFormat.format(time.toDate()))
        listener?.onDateTimeChanged(time)
    }

    internal fun onDate() {
        val time = currentTime!!
        DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            currentTime = currentTime?.withYear(year)?.withMonthOfYear(monthOfYear + 1)?.withDayOfMonth(dayOfMonth)
            onDateChanged()
        }, time.year, time.monthOfYear - 1, time.dayOfMonth).show()
    }

    internal fun onHour() {
        val time = currentTime!!
        TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            currentTime = currentTime?.withHourOfDay(hourOfDay)?.withMinuteOfHour(minute)
            onDateChanged()
        }, time.hourOfDay, time.minuteOfHour, true).show()
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }
}