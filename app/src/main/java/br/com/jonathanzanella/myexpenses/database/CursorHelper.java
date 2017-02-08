package br.com.jonathanzanella.myexpenses.database;

import android.database.Cursor;

/**
 * Created by jzanella on 07/02/17.
 */

public class CursorHelper {
	public static Long getLong(Cursor c, Fields field) {
		return c.getLong(c.getColumnIndexOrThrow(field.toString()));
	}

	public static String getString(Cursor c, Fields field) {
		return c.getString(c.getColumnIndexOrThrow(field.toString()));
	}
}
