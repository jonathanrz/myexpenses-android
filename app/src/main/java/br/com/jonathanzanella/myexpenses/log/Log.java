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
import lombok.Getter;
import lombok.Setter;

public class Log implements UnsyncModel {
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy HH:mm:ss:SSSS", Locale.getDefault());
	private static final String TAG = "Log";
	private static LogRepository logRepository;

	enum LOG_LEVEL {
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
					logLevels.add(LOG_LEVEL.DEBUG.name());
				case INFO:
					logLevels.add(LOG_LEVEL.INFO.name());
				case WARNING:
					logLevels.add(LOG_LEVEL.WARNING.name());
				case ERROR:
					logLevels.add(LOG_LEVEL.ERROR.name());
			}
			return logLevels;
		}

		public static LOG_LEVEL getLogLevel(String logLevelAsString) {
			for (LOG_LEVEL logLevel : values()) {
				if(logLevel.name().equals(logLevelAsString))
					return logLevel;
			}

			return null;
		}
	}

	@Setter
	private long id;

	@Getter @Setter
	private String title;

	@Getter @Setter
	private String description;

	@Getter @Setter
	private DateTime date;

	@Setter
	private LOG_LEVEL type;

	Log() {
	}

	LOG_LEVEL getLogLevel() {
		return type;
	}

	String getDateAsString() {
		return date.toString();
	}

	private static LogRepository getLogRepository() {
		if(logRepository == null)
			logRepository = new LogRepository(new RepositoryImpl<Log>(MyApplication.getContext()));
		return logRepository;
	}

	private static void log(String title, String description, LOG_LEVEL logLevel) {
		Log log = new Log();
		log.title = title;
		log.description = description;
		log.date = DateTime.now();
		log.type = logLevel;
		getLogRepository().save(log);
	}

	public static void debug(String title, String description) {
		log(title, description, LOG_LEVEL.DEBUG);
		android.util.Log.d(title, description);
	}

	public static void info(String title, String description) {
		log(title, description, LOG_LEVEL.INFO);
		android.util.Log.i(title, description);
	}

	public static void warning(String title, String description) {
		log(title, description, LOG_LEVEL.WARNING);
		android.util.Log.w(title, description);
	}

	public static void error(String title, String description) {
		log(title, description, LOG_LEVEL.ERROR);
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

}
