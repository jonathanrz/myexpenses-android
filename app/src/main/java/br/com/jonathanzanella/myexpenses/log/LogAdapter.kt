package br.com.jonathanzanella.myexpenses.log

import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.jonathanzanella.myexpenses.R
import kotlinx.android.synthetic.main.row_log.view.*
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import java.util.*

internal class LogAdapter(private val logRepository: LogRepository) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {
    private var filteredLogs: MutableList<Log>? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(log: Log) {
            itemView.indicator.setBackgroundColor(ResourcesCompat.getColor(itemView.indicator.context.resources, log.logLevel.color, null))
            itemView.title.text = log.title
            itemView.date.text = log.simpleDateFormat.format(log.date.toDate())
            itemView.description.text = log.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_log, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(filteredLogs!![position])
    }

    override fun getItemCount(): Int {
        return if (filteredLogs != null) filteredLogs!!.size else 0
    }

    fun loadData(initDate: DateTime, endDate: DateTime, logLevel: Log.LogLevel, filter: String) {
        val logs = logRepository.filter(initDate, endDate, logLevel)

        filteredLogs = ArrayList<Log>()
        if (StringUtils.isNotEmpty(filter)) {
            logs
                .filter { StringUtils.containsIgnoreCase(it.title, filter) || StringUtils.containsIgnoreCase(it.description, filter) }
                .forEach { filteredLogs!!.add(it) }
        } else {
            filteredLogs = logs
        }
    }
}
