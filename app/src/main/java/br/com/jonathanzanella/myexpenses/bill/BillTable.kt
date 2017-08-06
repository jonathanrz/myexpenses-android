package br.com.jonathanzanella.myexpenses.bill

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import br.com.jonathanzanella.myexpenses.database.Fields
import br.com.jonathanzanella.myexpenses.database.SqlTypes
import br.com.jonathanzanella.myexpenses.database.Table
import org.joda.time.DateTime

class BillTable : Table<Bill> {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTableSql())
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    override fun onDrop(db: SQLiteDatabase) {
        db.execSQL(dropTableSql())
    }

    override val name: String
        get() = "Bill"

    private fun createTableSql(): String {
        return "CREATE TABLE " + name + " (" +
                Fields.ID + SqlTypes.PRIMARY_KEY + "," +
                Fields.NAME + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
                Fields.UUID + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
                Fields.AMOUNT + SqlTypes.INT + "," +
                Fields.DUE_DATE + SqlTypes.INT + "," +
                Fields.INIT_DATE + SqlTypes.DATE_NOT_NULL + "," +
                Fields.END_DATE + SqlTypes.DATE_NOT_NULL + "," +
                Fields.SERVER_ID + SqlTypes.TEXT_UNIQUE + "," +
                Fields.CREATED_AT + SqlTypes.DATE + "," +
                Fields.UPDATED_AT + SqlTypes.DATE + "," +
                Fields.SYNC + SqlTypes.INT + " )"
    }

    private fun dropTableSql(): String {
        return "DROP TABLE IF EXISTS " + name
    }

    override fun fillContentValues(data: Bill): ContentValues {
        val values = ContentValues()
        values.put(Fields.NAME.toString(), data.name)
        values.put(Fields.UUID.toString(), data.uuid)
        values.put(Fields.AMOUNT.toString(), data.amount)
        values.put(Fields.DUE_DATE.toString(), data.dueDate)
        values.put(Fields.INIT_DATE.toString(), data.initDate!!.millis)
        values.put(Fields.END_DATE.toString(), data.endDate!!.millis)
        values.put(Fields.SERVER_ID.toString(), data.serverId)
        values.put(Fields.CREATED_AT.toString(), data.createdAt)
        values.put(Fields.UPDATED_AT.toString(), data.updatedAt)
        values.put(Fields.SYNC.toString(), data.sync)
        return values
    }

    override fun fill(c: Cursor): Bill {
        val bill = Bill()
        bill.id = c.getLong(c.getColumnIndexOrThrow(Fields.ID.toString()))
        bill.name = c.getString(c.getColumnIndexOrThrow(Fields.NAME.toString()))
        bill.uuid = c.getString(c.getColumnIndexOrThrow(Fields.UUID.toString()))
        bill.amount = c.getInt(c.getColumnIndexOrThrow(Fields.AMOUNT.toString()))
        bill.dueDate = c.getInt(c.getColumnIndexOrThrow(Fields.DUE_DATE.toString()))
        bill.initDate = DateTime(c.getLong(c.getColumnIndexOrThrow(Fields.INIT_DATE.toString())))
        bill.endDate = DateTime(c.getLong(c.getColumnIndexOrThrow(Fields.END_DATE.toString())))
        bill.serverId = c.getString(c.getColumnIndexOrThrow(Fields.SERVER_ID.toString()))
        bill.createdAt = c.getLong(c.getColumnIndexOrThrow(Fields.CREATED_AT.toString()))
        bill.updatedAt = c.getLong(c.getColumnIndexOrThrow(Fields.UPDATED_AT.toString()))
        bill.sync = c.getLong(c.getColumnIndexOrThrow(Fields.SYNC.toString())) != 0L
        return bill
    }

    override val projection: Array<String>
        get() = arrayOf(Fields.ID.toString(), Fields.NAME.toString(), Fields.UUID.toString(), Fields.AMOUNT.toString(), Fields.DUE_DATE.toString(), Fields.INIT_DATE.toString(), Fields.END_DATE.toString(), Fields.SERVER_ID.toString(), Fields.CREATED_AT.toString(), Fields.UPDATED_AT.toString(), Fields.SYNC.toString())
}