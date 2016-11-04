package br.com.jonathanzanella.myexpenses.source;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Pair;

import br.com.jonathanzanella.myexpenses.database.Table;

import static br.com.jonathanzanella.myexpenses.database.SqlTypes.DATE;
import static br.com.jonathanzanella.myexpenses.database.SqlTypes.INT;
import static br.com.jonathanzanella.myexpenses.database.SqlTypes.PRIMARY_KEY;
import static br.com.jonathanzanella.myexpenses.database.SqlTypes.TEXT;

/**
 * Created by jzanella on 11/1/16.
 */

public final class SourceTable implements Table {
	static final String TABLE_NAME = "Source";

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
		db.execSQL(createTableSql());
	}

	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
	}

	public void recreate(SQLiteDatabase db) {
		db.execSQL(dropTableSql());
		db.execSQL(createTableSql());
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

	ContentValues fillContentValues(Source source) {
		ContentValues values = new ContentValues();
		values.put(Fields.NAME, source.getName());
		values.put(Fields.UUID, source.getUuid());
		values.put(Fields.USER_UUID, source.getUserUuid());
		values.put(Fields.SERVER_ID, source.getServerId());
		values.put(Fields.CREATED_AT, source.getCreatedAt());
		values.put(Fields.UPDATED_AT, source.getUpdatedAt());
		values.put(Fields.SYNC, source.isSync());
		return values;
	}

	Source fillSource(Cursor c) {
		Source source = new Source();
		source.setId(c.getLong(c.getColumnIndexOrThrow(Fields.ID)));
		source.setName(c.getString(c.getColumnIndexOrThrow(Fields.NAME)));
		source.setUuid(c.getString(c.getColumnIndexOrThrow(Fields.UUID)));
		source.setUserUuid(c.getString(c.getColumnIndexOrThrow(Fields.USER_UUID)));
		source.setServerId(c.getString(c.getColumnIndexOrThrow(Fields.SERVER_ID)));
		source.setCreatedAt(c.getLong(c.getColumnIndexOrThrow(Fields.CREATED_AT)));
		source.setUpdatedAt(c.getLong(c.getColumnIndexOrThrow(Fields.UPDATED_AT)));
		source.setSync(c.getLong(c.getColumnIndexOrThrow(Fields.UPDATED_AT)) != 0);
		return source;
	}

	String [] getProjection() {
		return new String[]{
				Fields.ID,
				Fields.NAME,
				Fields.UUID,
				Fields.USER_UUID,
				Fields.SERVER_ID,
				Fields.CREATED_AT,
				Fields.UPDATED_AT,
				Fields.SYNC
		};
	}

	Pair<String, String[]> getSelectionById(Long id) {
		return new Pair<>(Fields.ID + " = ?", new String[] { String.valueOf(id) });
	}

	Pair<String, String[]> getSelectionByUuid(String uuid) {
		return new Pair<>(Fields.UUID + " = ?", new String[] { uuid });
	}

	Pair<String, String[]> getSelectionByUserUuid(String userUuid) {
		return new Pair<>(Fields.USER_UUID + " = ?", new String[] { userUuid });
	}

	Pair<String, String[]> getSelectionBySync(boolean sync) {
		return new Pair<>(Fields.SYNC + " = ?", new String[] { String.valueOf(sync ? 1 : 0) });
	}

	String getOrderByName() {
		return Fields.NAME;
	}

	String getOrderByUpdatedAt() {
		return Fields.UPDATED_AT;
	}
}