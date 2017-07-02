package br.com.jonathanzanella.myexpenses.log

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import br.com.jonathanzanella.myexpenses.database.CursorHelper.getLong
import br.com.jonathanzanella.myexpenses.database.CursorHelper.getString
import br.com.jonathanzanella.myexpenses.database.Fields
import br.com.jonathanzanella.myexpenses.database.SqlTypes
import br.com.jonathanzanella.myexpenses.database.Table
import org.joda.time.DateTime

class LogTable : Table<Log> {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTableSql())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion == 1) {
            onDrop(db)
            onCreate(db)
        }
    }

    override fun onDrop(db: SQLiteDatabase) {
        db.execSQL(dropTableSql())
    }

    override val name: String
        get() = "Log"

    private fun createTableSql(): String {
        return "CREATE TABLE " + name + " (" +
                Fields.ID + SqlTypes.PRIMARY_KEY + "," +
                Fields.TITLE + SqlTypes.TEXT_NOT_NULL + "," +
                Fields.DESCRIPTION + SqlTypes.TEXT_NOT_NULL + "," +
                Fields.DATE + SqlTypes.DATE_NOT_NULL + "," +
                Fields.TYPE + SqlTypes.TEXT_NOT_NULL + " )"
    }

    private fun dropTableSql(): String {
        return "DROP TABLE IF EXISTS " + name
    }

    override fun fillContentValues(log: Log): ContentValues {
        val values = ContentValues()
        values.put(Fields.TITLE.toString(), log.title)
        values.put(Fields.DESCRIPTION.toString(), log.description)
        values.put(Fields.DATE.toString(), log.date!!.millis)
        values.put(Fields.TYPE.toString(), log.logLevel.toString())
        return values
    }

    override fun fill(c: Cursor): Log {
        val log = Log()
        log.id = getLong(c, Fields.ID)
        log.title = getString(c, Fields.TITLE)
        log.description = getString(c, Fields.DESCRIPTION)
        log.date = DateTime(getLong(c, Fields.DATE))
        log.setType(Log.LogLevel.getLogLevel(getString(c, Fields.TYPE)!!)!!)
        return log
    }

    override val projection: Array<String>
        get() = arrayOf(Fields.ID.toString(), Fields.TITLE.toString(), Fields.DESCRIPTION.toString(), Fields.DATE.toString(), Fields.TYPE.toString())
}