package br.com.jonathanzanella.myexpenses.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jzanella on 1/31/16.
 */
@Table(database = MyDatabase.class)
public class CreditCard extends BaseModel {
	@Column
	@PrimaryKey(autoincrement = true) @Getter
	long id;

	@Column @Getter @Setter
	String name;

	public static List<CreditCard> all() {
		return initQuery().queryList();
	}

	private static From<CreditCard> initQuery() {
		return SQLite.select().from(CreditCard.class);
	}

	public static CreditCard find(long id) {
		return initQuery().where(Source_Table.id.eq(id)).querySingle();
	}
}
