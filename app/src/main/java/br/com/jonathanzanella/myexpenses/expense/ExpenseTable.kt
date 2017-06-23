package br.com.jonathanzanella.myexpenses.expense

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType
import br.com.jonathanzanella.myexpenses.database.CursorHelper.*
import br.com.jonathanzanella.myexpenses.database.Fields
import br.com.jonathanzanella.myexpenses.database.SqlTypes
import br.com.jonathanzanella.myexpenses.database.Table

class ExpenseTable : Table<Expense> {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTableSql())
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    override fun onDrop(db: SQLiteDatabase) {
        db.execSQL(dropTableSql())
    }

    override val name: String
        get() = "Expense"

    private fun createTableSql(): String {
        return StringBuilder("CREATE TABLE ").append(name).append(" (")
                .append(Fields.ID).append(SqlTypes.PRIMARY_KEY).append(",")
                .append(Fields.UUID).append(SqlTypes.TEXT_UNIQUE_NOT_NULL).append(",")
                .append(Fields.NAME).append(SqlTypes.TEXT_NOT_NULL).append(",")
                .append(Fields.DATE).append(SqlTypes.DATE_NOT_NULL).append(",")
                .append(Fields.VALUE).append(SqlTypes.INT_NOT_NULL).append(",")
                .append(Fields.VALUE_TO_SHOW_IN_OVERVIEW).append(SqlTypes.INT_NOT_NULL).append(",")
                .append(Fields.CHARGEABLE_UUID).append(SqlTypes.TEXT_NOT_NULL).append(",")
                .append(Fields.CHARGEABLE_TYPE).append(SqlTypes.TEXT_NOT_NULL).append(",")
                .append(Fields.BILL_UUID).append(SqlTypes.TEXT).append(",")
                .append(Fields.CHARGED).append(SqlTypes.INT_NOT_NULL).append(",")
                .append(Fields.CHARGE_NEXT_MONTH).append(SqlTypes.INT_NOT_NULL).append(",")
                .append(Fields.IGNORE_IN_OVERVIEW).append(SqlTypes.INT_NOT_NULL).append(",")
                .append(Fields.IGNORE_IN_RESUME).append(SqlTypes.INT_NOT_NULL).append(",")
                .append(Fields.SERVER_ID).append(SqlTypes.TEXT_UNIQUE).append(",")
                .append(Fields.CREATED_AT).append(SqlTypes.DATE).append(",")
                .append(Fields.UPDATED_AT).append(SqlTypes.DATE).append(",")
                .append(Fields.REMOVED).append(SqlTypes.INT_NOT_NULL).append(",")
                .append(Fields.SYNC).append(SqlTypes.INT_NOT_NULL).append(" )")
                .toString()
    }

    private fun dropTableSql(): String {
        return "DROP TABLE IF EXISTS " + name
    }

    override fun fillContentValues(data: Expense): ContentValues {
        val values = ContentValues()
        values.put(Fields.UUID.toString(), data.uuid)
        values.put(Fields.NAME.toString(), data.name)
        values.put(Fields.DATE.toString(), data.date.millis)
        values.put(Fields.VALUE.toString(), data.value)
        values.put(Fields.VALUE_TO_SHOW_IN_OVERVIEW.toString(), data.valueToShowInOverview)
        values.put(Fields.CHARGEABLE_UUID.toString(), data.chargeableFromCache.uuid)
        values.put(Fields.CHARGEABLE_TYPE.toString(), data.chargeableFromCache.chargeableType.toString())
        values.put(Fields.BILL_UUID.toString(), data.billUuid)
        values.put(Fields.CHARGED.toString(), if (data.isCharged) 1 else 0)
        values.put(Fields.CHARGE_NEXT_MONTH.toString(), if (data.isChargedNextMonth) 1 else 0)
        values.put(Fields.IGNORE_IN_OVERVIEW.toString(), if (data.isIgnoreInOverview) 1 else 0)
        values.put(Fields.IGNORE_IN_RESUME.toString(), if (data.isIgnoreInResume) 1 else 0)
        values.put(Fields.SERVER_ID.toString(), data.serverId)
        values.put(Fields.CREATED_AT.toString(), data.createdAt)
        values.put(Fields.UPDATED_AT.toString(), data.updatedAt)
        values.put(Fields.REMOVED.toString(), if (data.isRemoved) 1 else 0)
        values.put(Fields.SYNC.toString(), if (data.sync) 1 else 0)
        return values
    }

    override fun fill(c: Cursor): Expense {
        val expense = Expense()
        expense.id = getLong(c, Fields.ID)
        expense.uuid = getString(c, Fields.UUID)
        expense.name = getString(c, Fields.NAME)
        expense.date = getDate(c, Fields.DATE)
        expense.value = getInt(c, Fields.VALUE)
        expense.valueToShowInOverview = getInt(c, Fields.VALUE_TO_SHOW_IN_OVERVIEW)
        expense.setChargeable(getString(c, Fields.CHARGEABLE_UUID),
                ChargeableType.getType(getString(c, Fields.CHARGEABLE_TYPE)))
        expense.billUuid = getString(c, Fields.BILL_UUID)
        expense.isCharged = getInt(c, Fields.CHARGED) != 0
        expense.isChargedNextMonth = getInt(c, Fields.CHARGE_NEXT_MONTH) != 0
        expense.isIgnoreInOverview = getInt(c, Fields.IGNORE_IN_OVERVIEW) != 0
        expense.isIgnoreInResume = getInt(c, Fields.IGNORE_IN_RESUME) != 0
        expense.serverId = getString(c, Fields.SERVER_ID)
        expense.createdAt = getLong(c, Fields.CREATED_AT)
        expense.updatedAt = getLong(c, Fields.UPDATED_AT)
        expense.isRemoved = getInt(c, Fields.REMOVED) != 0
        expense.sync = getLong(c, Fields.SYNC) != 0L
        return expense
    }

    override val projection: Array<String>
        get() = arrayOf(Fields.ID.toString(),
                Fields.UUID.toString(),
                Fields.NAME.toString(),
                Fields.DATE.toString(),
                Fields.VALUE.toString(),
                Fields.VALUE_TO_SHOW_IN_OVERVIEW.toString(),
                Fields.CHARGEABLE_UUID.toString(),
                Fields.CHARGEABLE_TYPE.toString(),
                Fields.BILL_UUID.toString(),
                Fields.CHARGED.toString(),
                Fields.CHARGE_NEXT_MONTH.toString(),
                Fields.IGNORE_IN_OVERVIEW.toString(),
                Fields.IGNORE_IN_RESUME.toString(),
                Fields.SERVER_ID.toString(),
                Fields.CREATED_AT.toString(),
                Fields.UPDATED_AT.toString(),
                Fields.REMOVED.toString(),
                Fields.SYNC.toString())
}