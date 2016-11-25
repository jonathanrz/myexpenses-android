package br.com.jonathanzanella.myexpenses.database

/**
 * Created by jzanella on 11/1/16.
 */

object SqlTypes {
    const val PRIMARY_KEY = " INTEGER PRIMARY KEY AUTOINCREMENT"
    const val INT = " INTEGER"
    const val INT_NOT_NULL = " INTEGER NOT NULL"
    const val TEXT = " TEXT"
    const val TEXT_NOT_NULL = " TEXT NOT NULL"
    const val TEXT_UNIQUE = " TEXT UNIQUE"
    const val TEXT_UNIQUE_NOT_NULL = " TEXT UNIQUE NOT NULL"
    const val DATE = " DATE"
}