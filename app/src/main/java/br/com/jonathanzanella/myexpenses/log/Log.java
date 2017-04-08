package br.com.jonathanzanella.myexpenses.log;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;

public class Log implements UnsyncModel {
	private static final String TAG = "Log";
	private static LogRepository logRepository;
	final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss:SSSS", Locale.getDefault());

	enum LogLevel {
		DEBUG,
		INFO,
		WARNING,
		ERROR;

		public int getColor() {
			switch (this) {
				case DEBUG:
					return R.color.log_debug;
				case INFO:
					return R.color.log_info;
				case WARNING:
					return R.color.log_warning;
				case ERROR:
					return R.color.log_error;
			}

			android.util.Log.e(TAG, "new log type?");
			return R.color.log_debug;
		}

		public List<String> getLogLevelsAsString() {
			List<String> logLevels = new ArrayList<>();
			switch (this) {
				case DEBUG:
					logLevels.add(LogLevel.DEBUG.name());
				case INFO:
					logLevels.add(LogLevel.INFO.name());
				case WARNING:
					logLevels.add(LogLevel.WARNING.name());
				case ERROR:
					logLevels.add(LogLevel.ERROR.name());
			}
			return logLevels;
		}

		public static LogLevel getLogLevel(String logLevelAsString) {
			for (LogLevel logLevel : values()) {
				if(logLevel.name().equals(logLevelAsString))
					return logLevel;
			}

			return null;
		}
	}

	private long id;
	private String title;
	private String description;
	private DateTime date;
	private LogLevel type;

	Log() {
	}

	LogLevel getLogLevel() {
		return type;
	}

	private static LogRepository getLogRepository() {
		if(logRepository == null)
			logRepository = new LogRepository(new RepositoryImpl<Log>(MyApplication.getContext()));
		return logRepository;
	}

	private static void log(String title, String description, LogLevel logLevel) {
		Log log = new Log();
		log.title = title;
		log.description = description;
		log.date = DateTime.now();
		log.type = logLevel;
		getLogRepository().save(log);
	}

	public static void debug(String title, String description) {
		log(title, description, LogLevel.DEBUG);
		android.util.Log.d(title, description);
	}

	public static void info(String title, String description) {
		log(title, description, LogLevel.INFO);
		android.util.Log.i(title, description);
	}

	public static void warning(String title, String description) {
		log(title, description, LogLevel.WARNING);
		android.util.Log.w(title, description);
	}

	public static void error(String title, String description) {
		log(title, description, LogLevel.ERROR);
		android.util.Log.e(title, description);
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getServerId() {
		throw new UnsupportedOperationException("Log isn't an unsync model");
	}

	@Override
	public String getUuid() {
		throw new UnsupportedOperationException("Log isn't an unsync model");
	}

	@Override
	public void setServerId(String serverId) {
		throw new UnsupportedOperationException("Log isn't an unsync model");
	}

	@Override
	public long getCreatedAt() {
		throw new UnsupportedOperationException("Log isn't an unsync model");
	}

	@Override
	public void setCreatedAt(long createdAt) {
		throw new UnsupportedOperationException("Log isn't an unsync model");
	}

	@Override
	public long getUpdatedAt() {
		throw new UnsupportedOperationException("Log isn't an unsync model");
	}

	@Override
	public void setUpdatedAt(long updatedAt) {
		throw new UnsupportedOperationException("Log isn't an unsync model");
	}

	@Override
	public String getData() {
		throw new UnsupportedOperationException("Log isn't an unsync model");
	}

	@Override
	public void setSync(boolean b) {
		throw new UnsupportedOperationException("Log isn't an unsync model");
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public LogLevel getType() {
		return type;
	}

	public void setType(LogLevel type) {
		this.type = type;
	}
}
