package br.com.jonathanzanella.myexpenses.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import br.com.jonathanzanella.myexpenses.MyApplication

private const val DB_NAME = "MyExpenses"
private const val DB_VERSION = 6

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
//    private val tables = emptyList<Table<?>>()

    override fun onCreate(db: SQLiteDatabase) {
//        for (table in tables)
//            table.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        for (table in tables)
//            table.onUpgrade(db, oldVersion, newVersion)
    }

    fun runMigrations() {
        readableDatabase.close() //We just need to open a database to execute the migrations
    }

    fun recreateTables() {
        val db = writableDatabase
//        for (table in tables) {
//            table.onDrop(db)
//            table.onCreate(db)
//        }
        db.close()

        MyApplication.resetDatabase()
    }
}