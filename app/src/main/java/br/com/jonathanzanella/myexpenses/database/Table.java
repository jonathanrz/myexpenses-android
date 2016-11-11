package br.com.jonathanzanella.myexpenses.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;

/**
 * Created by jzanella on 11/1/16.
 */

public interface Table <T extends UnsyncModel> {
	void onCreate(SQLiteDatabase db);
	void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion);

	String getName();
	ContentValues fillContentValues(T data);
	T fill(Cursor c);
	String [] getProjection();
}