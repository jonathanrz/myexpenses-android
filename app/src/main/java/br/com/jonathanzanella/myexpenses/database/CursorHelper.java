package br.com.jonathanzanella.myexpenses.database;

import android.database.Cursor;

import org.joda.time.DateTime;

public final class CursorHelper {
	private CursorHelper() {}

	public static Long getLong(Cursor c, Fields field) {
		return c.getLong(c.getColumnIndexOrThrow(field.toString()));
	}

	public static Integer getInt(Cursor c, Fields field) {
		return c.getInt(c.getColumnIndexOrThrow(field.toString()));
	}

	public static DateTime getDate(Cursor c, Fields field) {
		return new DateTime(c.getLong(c.getColumnIndexOrThrow(field.toString())));
	}

	public static String getString(Cursor c, Fields field) {
		return c.getString(c.getColumnIndexOrThrow(field.toString()));
	}
}