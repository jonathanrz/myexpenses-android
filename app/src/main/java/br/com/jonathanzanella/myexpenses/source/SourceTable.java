package br.com.jonathanzanella.myexpenses.source;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.Table;

import static br.com.jonathanzanella.myexpenses.database.SqlTypes.DATE;
import static br.com.jonathanzanella.myexpenses.database.SqlTypes.INT;
import static br.com.jonathanzanella.myexpenses.database.SqlTypes.PRIMARY_KEY;
import static br.com.jonathanzanella.myexpenses.database.SqlTypes.TEXT;

/**
 * Created by jzanella on 11/1/16.
 */

public final class SourceTable implements Table<Source> {
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createTableSql());
	}

	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
	}

	@Override
	public String getName() {
		return "Source";
	}

	void recreate(SQLiteDatabase db) {
		db.execSQL(dropTableSql());
		db.execSQL(createTableSql());
	}

	private String createTableSql() {
		return "CREATE TABLE " + getName() + " (" +
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
		return "DROP TABLE IF EXISTS " + getName();
	}

	@Override
	public ContentValues fillContentValues(Source source) {
		ContentValues values = new ContentValues();
		values.put(Fields.NAME.toString(), source.getName());
		values.put(Fields.UUID.toString(), source.getUuid());
		values.put(Fields.USER_UUID.toString(), source.getUserUuid());
		values.put(Fields.SERVER_ID.toString(), source.getServerId());
		values.put(Fields.CREATED_AT.toString(), source.getCreatedAt());
		values.put(Fields.UPDATED_AT.toString(), source.getUpdatedAt());
		values.put(Fields.SYNC.toString(), source.isSync());
		return values;
	}

	@Override
	public Source fill(Cursor c) {
		Source source = new Source();
		source.setId(c.getLong(c.getColumnIndexOrThrow(Fields.ID.toString())));
		source.setName(c.getString(c.getColumnIndexOrThrow(Fields.NAME.toString())));
		source.setUuid(c.getString(c.getColumnIndexOrThrow(Fields.UUID.toString())));
		source.setUserUuid(c.getString(c.getColumnIndexOrThrow(Fields.USER_UUID.toString())));
		source.setServerId(c.getString(c.getColumnIndexOrThrow(Fields.SERVER_ID.toString())));
		source.setCreatedAt(c.getLong(c.getColumnIndexOrThrow(Fields.CREATED_AT.toString())));
		source.setUpdatedAt(c.getLong(c.getColumnIndexOrThrow(Fields.UPDATED_AT.toString())));
		source.setSync(c.getLong(c.getColumnIndexOrThrow(Fields.UPDATED_AT.toString())) != 0);
		return source;
	}

	@Override
	public String [] getProjection() {
		return new String[]{
				Fields.ID.toString(),
				Fields.NAME.toString(),
				Fields.UUID.toString(),
				Fields.USER_UUID.toString(),
				Fields.SERVER_ID.toString(),
				Fields.CREATED_AT.toString(),
				Fields.UPDATED_AT.toString(),
				Fields.SYNC.toString()
		};
	}
}