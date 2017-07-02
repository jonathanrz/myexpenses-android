package br.com.jonathanzanella.myexpenses.database

import android.database.Cursor

import org.joda.time.DateTime

object CursorHelper {

    @JvmStatic fun getLong(c: Cursor, field: Fields): Long {
        return c.getLong(c.getColumnIndexOrThrow(field.toString()))
    }

    @JvmStatic fun getInt(c: Cursor, field: Fields): Int {
        return c.getInt(c.getColumnIndexOrThrow(field.toString()))
    }

    @JvmStatic fun getDate(c: Cursor, field: Fields): DateTime {
        return DateTime(c.getLong(c.getColumnIndexOrThrow(field.toString())))
    }

    @JvmStatic fun getString(c: Cursor, field: Fields): String {
        return c.getString(c.getColumnIndexOrThrow(field.toString()))
    }
}