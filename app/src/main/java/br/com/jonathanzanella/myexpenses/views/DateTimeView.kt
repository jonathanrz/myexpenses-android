package br.com.jonathanzanella.myexpenses.views

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.View
import br.com.jonathanzanella.myexpenses.R
import kotlinx.android.synthetic.main.view_datetime.view.*
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

class DateTimeView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseView(context, attrs, defStyleAttr) {
    val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private var listener: Listener? = null

    var currentTime: DateTime? = null
        private set

    interface Listener {
        fun onDateTimeChanged(currentTime: DateTime)
    }

    override fun init() {
        View.inflate(context, R.layout.view_datetime, this)
        date.setOnClickListener { onDate() }
        hour.setOnClickListener { onHour() }
    }

    fun setDate(currentTime: DateTime) {
        this.currentTime = currentTime
        onDateChanged()
    }

    private fun onDateChanged() {
        date.setText(dateFormat.format(currentTime!!.toDate()))
        hour.setText(hourFormat.format(currentTime!!.toDate()))
        if (listener != null)
            listener!!.onDateTimeChanged(currentTime!!)
    }

    internal fun onDate() {
        DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            currentTime = currentTime!!.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth)
            onDateChanged()
        }, currentTime!!.year, currentTime!!.monthOfYear - 1, currentTime!!.dayOfMonth).show()
    }

    internal fun onHour() {
        TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            currentTime = currentTime!!.withHourOfDay(hourOfDay).withMinuteOfHour(minute)
            onDateChanged()
        }, currentTime!!.hourOfDay, currentTime!!.minuteOfHour, true).show()
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }
}