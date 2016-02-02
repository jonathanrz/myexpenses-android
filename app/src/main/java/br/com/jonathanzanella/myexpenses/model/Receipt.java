package br.com.jonathanzanella.myexpenses.model;

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
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by jzanella on 2/1/16.
 */
@Table(database = MyDatabase.class)
public class Receipt extends BaseModel {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

	@Column
	@PrimaryKey(autoincrement = true) @Getter
	long id;

	@Column @Getter @Setter
	String name;

	@Column(typeConverter = DateTimeConverter.class) @Getter @Setter
	DateTime date;

	@Column @Getter @Setter
	int income;

	@Column
	long sourceId;

	public static List<Receipt> all() {
		return initQuery().queryList();
	}

	private static From<Receipt> initQuery() {
		return SQLite.select().from(Receipt.class);
	}

	public static Receipt find(long id) {
		return initQuery().where(Receipt_Table.id.eq(id)).querySingle();
	}

	public Source getSource() {
		return Source.find(sourceId);
	}

	public void setSource(@NonNull Source s) {
		sourceId = s.getId();
	}
}
