package br.com.jonathanzanella.myexpenses.log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.helpers.converter.DateTimeConverter;
import lombok.Getter;

/**
 * Created by jzanella on 7/11/16.
 */
@Table(database = MyDatabase.class)
public class Log extends BaseModel {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss:SSSS", Locale.getDefault());
	private static final String TAG = "Log";

	enum TYPE {
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
	}

	@Column
	@PrimaryKey(autoincrement = true)
	long id;

	@Column @NotNull @Getter
	String title;

	@Column @NotNull @Getter
	String description;

	@Column(typeConverter = DateTimeConverter.class) @NotNull @Getter
	DateTime date;

	@Column @NotNull @Getter
	TYPE type;

	Log() {
	}

	private static void log(String title, String description, TYPE type) {
		Log log = new Log();
		log.title = title;
		log.description = description;
		log.date = DateTime.now();
		log.type = type;
		log.save();
	}

	public static void debug(String title, String description) {
		log(title, description, TYPE.DEBUG);
		android.util.Log.d(title, description);
	}

	public static void info(String title, String description) {
		log(title, description, TYPE.INFO);
		android.util.Log.i(title, description);
	}

	public static void warning(String title, String description) {
		log(title, description, TYPE.WARNING);
		android.util.Log.w(title, description);
	}

	public static void error(String title, String description) {
		log(title, description, TYPE.ERROR);
		android.util.Log.e(title, description);
	}

	private static From<Log> initQuery() {
		return SQLite.select().from(Log.class);
	}

	public static List<Log> all() {
		return initQuery().queryList();
	}
}
