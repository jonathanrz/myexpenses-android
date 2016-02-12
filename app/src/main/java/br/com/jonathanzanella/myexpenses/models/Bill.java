package br.com.jonathanzanella.myexpenses.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.converter.DateTimeConverter;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Jonathan Zanella on 07/02/16.
 */
@Table(database = MyDatabase.class)
public class Bill extends BaseModel {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

	@Column
	@PrimaryKey(autoincrement = true) @Getter
	long id;

	@Column @Getter @Setter
	String name;

	@Column @Getter @Setter
	int amount;

	@Column @Getter @Setter
	int dueDate;

	@Column(typeConverter = DateTimeConverter.class) @Getter
	DateTime initDate;

	@Column(typeConverter = DateTimeConverter.class) @Getter
	DateTime endDate;

	public static List<Bill> all() {
		return initQuery().queryList();
	}

	private static From<Bill> initQuery() {
		return SQLite.select().from(Bill.class);
	}

	public static Bill find(long id) {
		return initQuery().where(Bill_Table.id.eq(id)).querySingle();
	}

	public static List<Bill> monthly(DateTime month) {
		return initQuery()
				.where(Bill_Table.initDate.lessThanOrEq(month))
				.and(Bill_Table.endDate.greaterThanOrEq(month))
				.queryList();
	}

	public void setInitDate(DateTime initDate) {
		this.initDate = initDate.withMillisOfDay(0);
	}

	public void setEndDate(DateTime endDate) {
		this.endDate = endDate.withMillisOfDay(0);
	}
}
