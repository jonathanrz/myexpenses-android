package br.com.jonathanzanella.myexpenses.source

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

import br.com.jonathanzanella.myexpenses.database.Fields
import br.com.jonathanzanella.myexpenses.database.SqlTypes
import br.com.jonathanzanella.myexpenses.database.Table

class SourceTable : Table<Source> {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTableSql())
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    override fun onDrop(db: SQLiteDatabase) {
        db.execSQL(dropTableSql())
    }

    override val name: String
        get() = "Source"

    private fun createTableSql(): String {
        return "CREATE TABLE " + name + " (" +
                Fields.ID + SqlTypes.PRIMARY_KEY + "," +
                Fields.NAME + SqlTypes.TEXT_NOT_NULL + "," +
                Fields.UUID + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
                Fields.SERVER_ID + SqlTypes.TEXT_UNIQUE + "," +
                Fields.CREATED_AT + SqlTypes.DATE + "," +
                Fields.UPDATED_AT + SqlTypes.DATE + "," +
                Fields.SYNC + SqlTypes.INT + " )"
    }

    private fun dropTableSql(): String {
        return "DROP TABLE IF EXISTS " + name
    }

    override fun fillContentValues(data: Source): ContentValues {
        val values = ContentValues()
        values.put(Fields.NAME.toString(), data.name)
        values.put(Fields.UUID.toString(), data.uuid)
        values.put(Fields.SERVER_ID.toString(), data.serverId)
        values.put(Fields.CREATED_AT.toString(), data.createdAt)
        values.put(Fields.UPDATED_AT.toString(), data.updatedAt)
        values.put(Fields.SYNC.toString(), data.sync)
        return values
    }

    override fun fill(c: Cursor): Source {
        val source = Source()
        source.id = c.getLong(c.getColumnIndexOrThrow(Fields.ID.toString()))
        source.name = c.getString(c.getColumnIndexOrThrow(Fields.NAME.toString()))
        source.uuid = c.getString(c.getColumnIndexOrThrow(Fields.UUID.toString()))
        source.serverId = c.getString(c.getColumnIndexOrThrow(Fields.SERVER_ID.toString()))
        source.createdAt = c.getLong(c.getColumnIndexOrThrow(Fields.CREATED_AT.toString()))
        source.updatedAt = c.getLong(c.getColumnIndexOrThrow(Fields.UPDATED_AT.toString()))
        source.sync = c.getLong(c.getColumnIndexOrThrow(Fields.SYNC.toString())) != 0L
        return source
    }

    override val projection: Array<String>
        get() = arrayOf(Fields.ID.toString(), Fields.NAME.toString(), Fields.UUID.toString(), Fields.SERVER_ID.toString(), Fields.CREATED_AT.toString(), Fields.UPDATED_AT.toString(), Fields.SYNC.toString())
}