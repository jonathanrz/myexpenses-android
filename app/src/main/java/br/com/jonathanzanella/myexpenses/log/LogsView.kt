package br.com.jonathanzanella.myexpenses.log

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.views.BaseView
import br.com.jonathanzanella.myexpenses.views.DateTimeView
import br.com.jonathanzanella.myexpenses.views.FilterableView
import kotlinx.android.synthetic.main.view_logs.view.*
import org.joda.time.DateTime

class LogsView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), BaseView, FilterableView, DateTimeView.Listener {

    override var filter = ""
    private val adapter = LogAdapter(LogRepository(RepositoryImpl<Log>(context)))

    init {
        View.inflate(context, R.layout.view_logs, this)

        logs.adapter = adapter
        logs.setHasFixedSize(true)
        logs.layoutManager = LinearLayoutManager(context)
        logs.isNestedScrollingEnabled = false

        initTime.setDate(DateTime.now().minusHours(1))
        initTime.setListener(this)
        endTime.setDate(DateTime.now())
        endTime.setListener(this)

        logLevelError.setOnClickListener { onLogLevel() }
        logLevelWarning.setOnClickListener { onLogLevel() }
        logLevelInfo.setOnClickListener { onLogLevel() }
        logLevelDebug.setOnClickListener { onLogLevel() }

        refreshAdapter()
    }

    override fun onDateTimeChanged(currentTime: DateTime) {
        refreshAdapter()
    }

    fun onLogLevel() {
        refreshAdapter()
    }

    private fun refreshAdapter() {
        adapter.loadData(initTime.currentTime!!, endTime.currentTime!!, getLogLevel(), filter)
        adapter.notifyDataSetChanged()
    }

    private fun getLogLevel(): Log.LogLevel {
        when (logLevel.checkedRadioButtonId) {
            R.id.logLevelError -> return Log.LogLevel.ERROR
            R.id.logLevelWarning -> return Log.LogLevel.WARNING
            R.id.logLevelInfo -> return Log.LogLevel.INFO
            R.id.logLevelDebug -> return Log.LogLevel.DEBUG
        }

        Log.error(LOG_TAG, "new log level?")
        return Log.LogLevel.DEBUG
    }

    override fun filter(s: String) {
        super.filter(s)
        this.filter = s
        refreshAdapter()
    }

    companion object {
        private val LOG_TAG = LogsView::class.java.simpleName
    }
}