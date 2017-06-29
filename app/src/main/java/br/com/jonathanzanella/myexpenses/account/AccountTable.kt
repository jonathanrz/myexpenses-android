package br.com.jonathanzanella.myexpenses.account

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import br.com.jonathanzanella.myexpenses.database.CursorHelper.*
import br.com.jonathanzanella.myexpenses.database.Fields
import br.com.jonathanzanella.myexpenses.database.SqlTypes
import br.com.jonathanzanella.myexpenses.database.Table

class AccountTable : Table<Account> {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTableSql())
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion <= 5)
            sqLiteDatabase.execSQL("ALTER TABLE " + name + " ADD COLUMN " + Fields.SHOW_IN_RESUME + SqlTypes.INT + " DEFAULT 1")
    }

    override fun onDrop(db: SQLiteDatabase) {
        db.execSQL(dropTableSql())
    }

    override val name: String
        get() = "Account"

    private fun createTableSql(): String {
        return "CREATE TABLE " + name + " (" +
                Fields.ID + SqlTypes.PRIMARY_KEY + "," +
                Fields.NAME + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
                Fields.UUID + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
                Fields.BALANCE + SqlTypes.INT + "," +
                Fields.ACCOUNT_TO_PAY_CREDIT_CARD + SqlTypes.INT + "," +
                Fields.ACCOUNT_TO_PAY_BILLS + SqlTypes.INT + "," +
                Fields.SHOW_IN_RESUME + SqlTypes.INT + "," +
                Fields.SERVER_ID + SqlTypes.TEXT_UNIQUE + "," +
                Fields.CREATED_AT + SqlTypes.DATE + "," +
                Fields.UPDATED_AT + SqlTypes.DATE + "," +
                Fields.SYNC + SqlTypes.INT + " )"
    }

    private fun dropTableSql(): String {
        return "DROP TABLE IF EXISTS " + name
    }

    override fun fillContentValues(data: Account): ContentValues {
        val values = ContentValues()
        values.put(Fields.NAME.toString(), data.name)
        values.put(Fields.UUID.toString(), data.uuid)
        values.put(Fields.BALANCE.toString(), data.balance)
        values.put(Fields.ACCOUNT_TO_PAY_CREDIT_CARD.toString(), data.isAccountToPayCreditCard)
        values.put(Fields.ACCOUNT_TO_PAY_BILLS.toString(), data.isAccountToPayBills)
        values.put(Fields.SHOW_IN_RESUME.toString(), data.showInResume)
        values.put(Fields.SERVER_ID.toString(), data.serverId)
        values.put(Fields.CREATED_AT.toString(), data.createdAt)
        values.put(Fields.UPDATED_AT.toString(), data.updatedAt)
        values.put(Fields.SYNC.toString(), data.sync)
        return values
    }

    override fun fill(c: Cursor): Account {
        val account = Account()
        account.id = getLong(c, Fields.ID)
        account.name = getString(c, Fields.NAME)
        account.uuid = getString(c, Fields.UUID)
        account.balance = getInt(c, Fields.BALANCE)
        account.isAccountToPayCreditCard = getInt(c, Fields.ACCOUNT_TO_PAY_CREDIT_CARD) != 0
        account.isAccountToPayBills = getInt(c, Fields.ACCOUNT_TO_PAY_BILLS) != 0
        account.showInResume = getInt(c, Fields.SHOW_IN_RESUME) != 0
        account.serverId = getString(c, Fields.SERVER_ID)
        account.createdAt = getLong(c, Fields.CREATED_AT)
        account.updatedAt = getLong(c, Fields.UPDATED_AT)
        account.sync = getLong(c, Fields.SYNC) != 0L
        return account
    }

    override val projection: Array<String>
        get() = arrayOf(Fields.ID.toString(), Fields.NAME.toString(), Fields.UUID.toString(), Fields.BALANCE.toString(), Fields.ACCOUNT_TO_PAY_CREDIT_CARD.toString(), Fields.ACCOUNT_TO_PAY_BILLS.toString(), Fields.SHOW_IN_RESUME.toString(), Fields.SERVER_ID.toString(), Fields.CREATED_AT.toString(), Fields.UPDATED_AT.toString(), Fields.SYNC.toString())
}