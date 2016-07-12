package br.com.jonathanzanella.myexpenses.log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.helpers.converter.DateTimeConverter;

/**
 * Created by jzanella on 7/11/16.
 */
@Table(database = MyDatabase.class)
public class Log extends BaseModel {
	enum TYPE {
		DEBUG,
		INFO,
		WARNING,
		ERROR
	}

	@Column
	@PrimaryKey(autoincrement = true)
	long id;

	@Column @NotNull
	String title;

	@Column @NotNull
	String description;

	@Column(typeConverter = DateTimeConverter.class) @NotNull
	DateTime date;

	@Column @NotNull
	TYPE type;

	private Log() {
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
	}

	public static void info(String title, String description) {
		log(title, description, TYPE.INFO);
	}

	public static void warning(String title, String description) {
		log(title, description, TYPE.WARNING);
	}

	public static void error(String title, String description) {
		log(title, description, TYPE.ERROR);
	}
}
