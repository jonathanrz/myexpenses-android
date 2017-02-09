package br.com.jonathanzanella.myexpenses.database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

/**
 * Created by jzanella on 11/1/16.
 */

interface Table<T> {
    fun onCreate(db: SQLiteDatabase)
    fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    fun onDrop(db: SQLiteDatabase)

    fun fillContentValues(data: T): ContentValues
    fun fill(c: Cursor): T

    val name: String
    val projection: Array<String>
}