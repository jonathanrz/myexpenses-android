package br.com.jonathanzanella.myexpenses.receipt

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import br.com.jonathanzanella.myexpenses.database.CursorHelper.getDate
import br.com.jonathanzanella.myexpenses.database.CursorHelper.getInt
import br.com.jonathanzanella.myexpenses.database.CursorHelper.getLong
import br.com.jonathanzanella.myexpenses.database.CursorHelper.getString
import br.com.jonathanzanella.myexpenses.database.Fields
import br.com.jonathanzanella.myexpenses.database.SqlTypes
import br.com.jonathanzanella.myexpenses.database.Table

class ReceiptTable : Table<Receipt> {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTableSql())
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    override fun onDrop(db: SQLiteDatabase) {
        db.execSQL(dropTableSql())
    }

    override val name: String
        get() = "Receipt"

    private fun createTableSql(): String {
        return "CREATE TABLE " + name + " (" +
                Fields.ID + SqlTypes.PRIMARY_KEY + "," +
                Fields.NAME + SqlTypes.TEXT_NOT_NULL + "," +
                Fields.UUID + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
                Fields.DATE + SqlTypes.DATE_NOT_NULL + "," +
                Fields.INCOME + SqlTypes.INT_NOT_NULL + "," +
                Fields.SOURCE_UUID + SqlTypes.TEXT + "," +
                Fields.ACCOUNT_UUID + SqlTypes.TEXT_NOT_NULL + "," +
                Fields.CREDITED + SqlTypes.INT_NOT_NULL + "," +
                Fields.IGNORE_IN_RESUME + SqlTypes.INT_NOT_NULL + "," +
                Fields.SERVER_ID + SqlTypes.TEXT_UNIQUE + "," +
                Fields.CREATED_AT + SqlTypes.DATE + "," +
                Fields.UPDATED_AT + SqlTypes.DATE + "," +
                Fields.REMOVED + SqlTypes.INT_NOT_NULL + "," +
                Fields.SYNC + SqlTypes.INT_NOT_NULL + " )"
    }

    private fun dropTableSql(): String {
        return "DROP TABLE IF EXISTS " + name
    }

    override fun fillContentValues(data: Receipt): ContentValues {
        val values = ContentValues()
        values.put(Fields.NAME.toString(), data.name)
        values.put(Fields.UUID.toString(), data.uuid)
        values.put(Fields.DATE.toString(), data.getDate().millis)
        values.put(Fields.INCOME.toString(), data.income)
        values.put(Fields.SOURCE_UUID.toString(), data.sourceUuid)
        values.put(Fields.ACCOUNT_UUID.toString(), data.accountUuid)
        values.put(Fields.CREDITED.toString(), if (data.isCredited) 1 else 0)
        values.put(Fields.IGNORE_IN_RESUME.toString(), if (data.isIgnoreInResume) 1 else 0)
        values.put(Fields.SERVER_ID.toString(), data.serverId)
        values.put(Fields.CREATED_AT.toString(), data.createdAt)
        values.put(Fields.UPDATED_AT.toString(), data.updatedAt)
        values.put(Fields.REMOVED.toString(), data.isRemoved)
        values.put(Fields.SYNC.toString(), data.sync)
        return values
    }

    override fun fill(c: Cursor): Receipt {
        val receipt = Receipt()
        receipt.id = getLong(c, Fields.ID)
        receipt.name = getString(c, Fields.NAME)
        receipt.uuid = getString(c, Fields.UUID)
        receipt.setDate(getDate(c, Fields.DATE))
        receipt.income = getInt(c, Fields.INCOME)
        receipt.sourceUuid = getString(c, Fields.SOURCE_UUID)
        receipt.accountUuid = getString(c, Fields.ACCOUNT_UUID)
        receipt.isCredited = getInt(c, Fields.CREDITED) != 0
        receipt.isIgnoreInResume = getInt(c, Fields.IGNORE_IN_RESUME) != 0
        receipt.serverId = getString(c, Fields.SERVER_ID)
        receipt.createdAt = getLong(c, Fields.CREATED_AT)
        receipt.updatedAt = getLong(c, Fields.UPDATED_AT)
        receipt.isRemoved = getInt(c, Fields.REMOVED) != 0
        receipt.sync = getLong(c, Fields.SYNC) != 0L
        return receipt
    }

    override val projection: Array<String>
        get() = arrayOf(Fields.ID.toString(), Fields.NAME.toString(), Fields.UUID.toString(), Fields.DATE.toString(), Fields.INCOME.toString(), Fields.SOURCE_UUID.toString(), Fields.ACCOUNT_UUID.toString(), Fields.CREDITED.toString(), Fields.IGNORE_IN_RESUME.toString(), Fields.SERVER_ID.toString(), Fields.CREATED_AT.toString(), Fields.UPDATED_AT.toString(), Fields.REMOVED.toString(), Fields.SYNC.toString())
}