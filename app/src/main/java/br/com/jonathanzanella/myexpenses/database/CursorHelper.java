package br.com.jonathanzanella.myexpenses.database;

import android.database.Cursor;

import org.joda.time.DateTime;

/**
 * Created by jzanella on 07/02/17.
 */

public class CursorHelper {
	public static Long getLong(Cursor c, Fields field) {
		return c.getLong(c.getColumnIndexOrThrow(field.toString()));
	}

	public static Integer getInt(Cursor c, Fields field) {
		return c.getInt(c.getColumnIndexOrThrow(field.toString()));
	}

	public static DateTime getDate(Cursor c, Fields field) {
		return DateTime.parse(c.getString(c.getColumnIndexOrThrow(field.toString())));
	}

	public static String getString(Cursor c, Fields field) {
		return c.getString(c.getColumnIndexOrThrow(field.toString()));
	}
}
