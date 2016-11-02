package br.com.jonathanzanella.myexpenses.source;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import br.com.jonathanzanella.myexpenses.database.Table;

import static br.com.jonathanzanella.myexpenses.database.SqlTypes.DATE;
import static br.com.jonathanzanella.myexpenses.database.SqlTypes.INT;
import static br.com.jonathanzanella.myexpenses.database.SqlTypes.PRIMARY_KEY;
import static br.com.jonathanzanella.myexpenses.database.SqlTypes.TEXT;

/**
 * Created by jzanella on 11/1/16.
 */

public final class SourceTable implements Table {
	private static final String TABLE_NAME = "Source";

	private static class Fields implements BaseColumns {
		static final String ID = "id";
		static final String NAME = "name";
		static final String UUID = "uuid";
		static final String USER_UUID = "userUuid";
		static final String SERVER_ID = "serverId";
		static final String CREATED_AT = "createdAt";
		static final String UPDATED_AT = "updatedAt";
		static final String SYNC = "sync";
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(dropTableSql()); //TODO: Remove after removing DBFLow from Source class
		db.execSQL(createTableSql());
	}

	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
	}

	private String createTableSql() {
		return "CREATE TABLE " + TABLE_NAME + " (" +
				Fields.ID + PRIMARY_KEY + "," +
				Fields.NAME + TEXT + "," +
				Fields.UUID + TEXT + "," +
				Fields.USER_UUID + TEXT + "," +
				Fields.SERVER_ID + TEXT + "," +
				Fields.CREATED_AT + DATE + "," +
				Fields.UPDATED_AT + DATE + "," +
				Fields.SYNC + INT + " )";
	}

	private String dropTableSql() {
		return "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
}