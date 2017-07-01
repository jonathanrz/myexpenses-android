package br.com.jonathanzanella.myexpenses.card

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import br.com.jonathanzanella.myexpenses.database.CursorHelper.getLong
import br.com.jonathanzanella.myexpenses.database.CursorHelper.getString
import br.com.jonathanzanella.myexpenses.database.Fields
import br.com.jonathanzanella.myexpenses.database.SqlTypes
import br.com.jonathanzanella.myexpenses.database.Table

class CardTable : Table<Card> {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTableSql())
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    override fun onDrop(db: SQLiteDatabase) {
        db.execSQL(dropTableSql())
    }

    override val name: String
        get() = "Card"

    private fun createTableSql(): String {
        return "CREATE TABLE " + name + " (" +
                Fields.ID + SqlTypes.PRIMARY_KEY + "," +
                Fields.NAME + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
                Fields.UUID + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
                Fields.TYPE + SqlTypes.TEXT_NOT_NULL + "," +
                Fields.ACCOUNT_UUID + SqlTypes.TEXT_NOT_NULL + "," +
                Fields.SERVER_ID + SqlTypes.TEXT_UNIQUE + "," +
                Fields.CREATED_AT + SqlTypes.DATE + "," +
                Fields.UPDATED_AT + SqlTypes.DATE + "," +
                Fields.SYNC + SqlTypes.INT + " )"
    }

    private fun dropTableSql(): String {
        return "DROP TABLE IF EXISTS " + name
    }

    override fun fillContentValues(data: Card): ContentValues {
        val values = ContentValues()
        values.put(Fields.NAME.toString(), data.name)
        values.put(Fields.UUID.toString(), data.uuid)
        values.put(Fields.TYPE.toString(), data.type!!.value)
        values.put(Fields.ACCOUNT_UUID.toString(), data.accountUuid)
        values.put(Fields.SERVER_ID.toString(), data.serverId)
        values.put(Fields.CREATED_AT.toString(), data.createdAt)
        values.put(Fields.UPDATED_AT.toString(), data.updatedAt)
        values.put(Fields.SYNC.toString(), data.sync)
        return values
    }

    override fun fill(c: Cursor): Card {
        val card = Card()
        card.id = getLong(c, Fields.ID)
        card.name = getString(c, Fields.NAME)
        card.uuid = getString(c, Fields.UUID)
        card.type = CardType.fromValue(getString(c, Fields.TYPE))
        card.accountUuid = getString(c, Fields.ACCOUNT_UUID)
        card.serverId = getString(c, Fields.SERVER_ID)
        card.createdAt = getLong(c, Fields.CREATED_AT)
        card.updatedAt = getLong(c, Fields.UPDATED_AT)
        card.sync = getLong(c, Fields.SYNC) != 0L
        return card
    }

    override val projection: Array<String>
        get() = arrayOf(Fields.ID.toString(), Fields.NAME.toString(), Fields.UUID.toString(), Fields.TYPE.toString(), Fields.ACCOUNT_UUID.toString(), Fields.SERVER_ID.toString(), Fields.CREATED_AT.toString(), Fields.UPDATED_AT.toString(), Fields.SYNC.toString())
}