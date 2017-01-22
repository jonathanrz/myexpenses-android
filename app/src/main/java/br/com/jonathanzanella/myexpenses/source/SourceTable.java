package br.com.jonathanzanella.myexpenses.source;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.SqlTypes;
import br.com.jonathanzanella.myexpenses.database.Table;

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
	public void onDrop(SQLiteDatabase db) {
		db.execSQL(dropTableSql());
	}

	@Override
	public String getName() {
		return "Source";
	}

	private String createTableSql() {
		return "CREATE TABLE " + getName() + " (" +
				Fields.ID + SqlTypes.PRIMARY_KEY + "," +
				Fields.NAME + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
				Fields.UUID + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
				Fields.USER_UUID + SqlTypes.TEXT_NOT_NULL + "," +
				Fields.SERVER_ID + SqlTypes.TEXT_UNIQUE + "," +
				Fields.CREATED_AT + SqlTypes.DATE + "," +
				Fields.UPDATED_AT + SqlTypes.DATE + "," +
				Fields.SYNC + SqlTypes.INT + " )";
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
		source.setSync(c.getLong(c.getColumnIndexOrThrow(Fields.SYNC.toString())) != 0);
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