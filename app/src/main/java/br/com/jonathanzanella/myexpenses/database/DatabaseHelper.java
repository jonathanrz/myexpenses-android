package br.com.jonathanzanella.myexpenses.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.com.jonathanzanella.myexpenses.bill.BillTable;
import br.com.jonathanzanella.myexpenses.source.SourceTable;

/**
 * Created by jzanella on 11/1/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
	private Table[] tables = {
			new SourceTable()
	};

	public DatabaseHelper(Context context) {
		super(context, MyDatabase.NAME, null, MyDatabase.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (Table table : tables)
			table.onCreate(db);
		new BillTable().recreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (Table table : tables)
			table.onUpgrade(db, oldVersion, newVersion);
	}

	public void runMigrations() {
		getReadableDatabase(); //We just need to open a database to execute the migrations
	}
}