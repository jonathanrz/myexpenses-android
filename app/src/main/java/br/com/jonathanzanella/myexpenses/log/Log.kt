package br.com.jonathanzanella.myexpenses.log

import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

class Log internal constructor() : UnsyncModel {
    internal val simpleDateFormat = SimpleDateFormat("dd/MM/yy HH:mm:ss:SSSS", Locale.getDefault())

    enum class LogLevel {
        DEBUG,
        INFO,
        WARNING,
        ERROR;

        val color: Int
            get() {
                when (this) {
                    DEBUG -> return R.color.log_debug
                    INFO -> return R.color.log_info
                    WARNING -> return R.color.log_warning
                    ERROR -> return R.color.log_error
                }
            }

        val logLevelsAsString: List<String>
            get() {
                val logLevels = ArrayList<String>()
                when (this) {
                    DEBUG -> {
                        logLevels.add(LogLevel.DEBUG.name)
                        logLevels.add(LogLevel.INFO.name)
                        logLevels.add(LogLevel.WARNING.name)
                        logLevels.add(LogLevel.ERROR.name)
                    }
                    INFO -> {
                        logLevels.add(LogLevel.INFO.name)
                        logLevels.add(LogLevel.WARNING.name)
                        logLevels.add(LogLevel.ERROR.name)
                    }
                    WARNING -> {
                        logLevels.add(LogLevel.WARNING.name)
                        logLevels.add(LogLevel.ERROR.name)
                    }
                    ERROR -> logLevels.add(LogLevel.ERROR.name)
                }
                return logLevels
            }

        companion object {
            fun getLogLevel(logLevelAsString: String): LogLevel? {
                return values().firstOrNull { it.name == logLevelAsString }
            }
        }
    }

    override var id: Long = 0
    var title: String? = null
    var description: String? = null
    var date: DateTime? = null
    var logLevel = LogLevel.INFO
        private set

    override var serverId: String?
        get() = throw UnsupportedOperationException("Log isn't an unsync model")
        set(_) = throw UnsupportedOperationException("Log isn't an unsync model")

    override var uuid: String?
        get() = throw UnsupportedOperationException("Log isn't an unsync model")
        set(_) = throw UnsupportedOperationException("Log isn't an unsync model")

    override var createdAt: Long
        get() = throw UnsupportedOperationException("Log isn't an unsync model")
        set(_) = throw UnsupportedOperationException("Log isn't an unsync model")

    override var updatedAt: Long
        get() = throw UnsupportedOperationException("Log isn't an unsync model")
        set(_) = throw UnsupportedOperationException("Log isn't an unsync model")

    override fun getData(): String {
        throw UnsupportedOperationException("Log isn't an unsync model")
    }

    override var sync: Boolean
        get() = throw UnsupportedOperationException("Log isn't an unsync model")
        set(b) = throw UnsupportedOperationException("Log isn't an unsync model")

    fun getType(): LogLevel {
        return logLevel
    }

    fun setType(type: LogLevel) {
        this.logLevel = type
    }

    companion object {
        private val TAG = "Log"
        private val logRepository: LogRepository by lazy {
            LogRepository(RepositoryImpl<Log>(MyApplication.getContext()))
        }

        private fun log(title: String, description: String, logLevel: LogLevel) {
            val log = Log()
            log.title = title
            log.description = description
            log.date = DateTime.now()
            log.logLevel = logLevel
            logRepository.save(log)
        }

        fun debug(title: String, description: String) {
            log(title, description, LogLevel.DEBUG)
            android.util.Log.d(title, description)
        }

        fun info(title: String, description: String) {
            log(title, description, LogLevel.INFO)
            android.util.Log.i(title, description)
        }

        fun warning(title: String, description: String) {
            log(title, description, LogLevel.WARNING)
            android.util.Log.w(title, description)
        }

        fun error(title: String, description: String) {
            log(title, description, LogLevel.ERROR)
            android.util.Log.e(title, description)
        }
    }
}
