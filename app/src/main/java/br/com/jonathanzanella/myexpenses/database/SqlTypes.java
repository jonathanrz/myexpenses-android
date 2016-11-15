package br.com.jonathanzanella.myexpenses.database;

/**
 * Created by jzanella on 11/1/16.
 */

public final class SqlTypes {
	private SqlTypes() {}

	public static final String PRIMARY_KEY = " INTEGER PRIMARY KEY AUTOINCREMENT";
	public static final String INT = " INTEGER";
	public static final String INT_NOT_NULL = " INTEGER NOT NULL";
	public static final String TEXT = " TEXT";
	public static final String TEXT_NOT_NULL = " TEXT NOT NULL";
	public static final String TEXT_UNIQUE = " TEXT UNIQUE";
	public static final String TEXT_UNIQUE_NOT_NULL = " TEXT UNIQUE NOT NULL";
	public static final String DATE = " DATE";
}