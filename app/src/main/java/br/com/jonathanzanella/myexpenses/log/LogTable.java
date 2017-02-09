package br.com.jonathanzanella.myexpenses.log;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.SqlTypes;
import br.com.jonathanzanella.myexpenses.database.Table;

import static br.com.jonathanzanella.myexpenses.database.CursorHelper.getInt;
import static br.com.jonathanzanella.myexpenses.database.CursorHelper.getLong;
import static br.com.jonathanzanella.myexpenses.database.CursorHelper.getString;

public final class LogTable implements Table<Log> {
	public void onCreate(@NonNull SQLiteDatabase db) {
		db.execSQL(createTableSql());
	}

	public void onUpgrade(@NonNull SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
	}

	@Override
	public void onDrop(@NonNull SQLiteDatabase db) {
		db.execSQL(dropTableSql());
	}

	@Override
	public String getName() {
		return "Log";
	}

	private String createTableSql() {
		return "CREATE TABLE " + getName() + " (" +
				Fields.ID + SqlTypes.PRIMARY_KEY + "," +
				Fields.TITLE + SqlTypes.TEXT_NOT_NULL + "," +
				Fields.DESCRIPTION + SqlTypes.TEXT_NOT_NULL + "," +
				Fields.DATE + SqlTypes.TEXT_NOT_NULL + "," +
				Fields.TYPE + SqlTypes.TEXT_NOT_NULL + " )";
	}

	private String dropTableSql() {
		return "DROP TABLE IF EXISTS " + getName();
	}

	@NonNull
	@Override
	public ContentValues fillContentValues(@NonNull Log log) {
		ContentValues values = new ContentValues();
		values.put(Fields.TITLE.toString(), log.getTitle());
		values.put(Fields.DESCRIPTION.toString(), log.getDescription());
		values.put(Fields.DATE.toString(), log.getDateAsString());
		values.put(Fields.TYPE.toString(), log.getLogLevel().toString());
		return values;
	}

	@NonNull
	@Override
	public Log fill(@NonNull Cursor c) {
		Log log = new Log();
		log.setId(getLong(c, Fields.ID));
		log.setTitle(getString(c, Fields.TITLE));
		log.setDescription(getString(c, Fields.DESCRIPTION));
		log.setDate(new DateTime(getInt(c, Fields.DATE)));
		log.setType(Log.LOG_LEVEL.getLogLevel(getString(c, Fields.TYPE)));
		return log;
	}

	@NonNull
	@Override
	public String [] getProjection() {
		return new String[]{
				Fields.ID.toString(),
				Fields.TITLE.toString(),
				Fields.DESCRIPTION.toString(),
				Fields.DATE.toString(),
				Fields.TYPE.toString()
		};
	}
}