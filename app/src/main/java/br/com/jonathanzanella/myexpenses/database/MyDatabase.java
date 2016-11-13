package br.com.jonathanzanella.myexpenses.database;

import com.raizlabs.android.dbflow.annotation.Database;

import br.com.jonathanzanella.myexpenses.Environment;

/**
 * Created by jonathan on 01/11/15.
 * Copyright (c) 2015. All rights reserved.
 */
@Database(name = MyDatabase.NAME, version = MyDatabase.VERSION)
public class MyDatabase {
	public static final String NAME = Environment.DB_NAME;

	static final int VERSION = Environment.DB_VERSION;
}