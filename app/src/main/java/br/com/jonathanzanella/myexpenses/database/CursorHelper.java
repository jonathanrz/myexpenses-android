package br.com.jonathanzanella.myexpenses.database;

import android.database.Cursor;
import android.util.Log;

import org.joda.time.DateTime;

public class CursorHelper {
	public static Long getLong(Cursor c, Fields field) {
		return c.getLong(c.getColumnIndexOrThrow(field.toString()));
	}

	public static Integer getInt(Cursor c, Fields field) {
		return c.getInt(c.getColumnIndexOrThrow(field.toString()));
	}

	public static DateTime getDate(Cursor c, Fields field) {
		long anInt = c.getLong(c.getColumnIndexOrThrow(field.toString()));
		Log.i("teste", "date loaded=" + anInt);
		return new DateTime(anInt);
	}

	public static String getString(Cursor c, Fields field) {
		return c.getString(c.getColumnIndexOrThrow(field.toString()));
	}
}
