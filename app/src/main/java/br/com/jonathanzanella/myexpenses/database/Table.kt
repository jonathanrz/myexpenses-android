package br.com.jonathanzanella.myexpenses.database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

import br.com.jonathanzanella.myexpenses.sync.UnsyncModel

/**
 * Created by jzanella on 11/1/16.
 */

interface Table<T : UnsyncModel> {
    fun onCreate(db: SQLiteDatabase)
    fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    fun onDrop(db: SQLiteDatabase)

    fun fillContentValues(data: T): ContentValues
    fun fill(c: Cursor): T

    val name: String
    val projection: Array<String>
}