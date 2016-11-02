package br.com.jonathanzanella.myexpenses.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by jzanella on 11/1/16.
 */

public interface Table {
	void onCreate(SQLiteDatabase db);
	void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion);
}