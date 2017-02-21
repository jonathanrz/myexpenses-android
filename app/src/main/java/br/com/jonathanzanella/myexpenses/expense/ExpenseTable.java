package br.com.jonathanzanella.myexpenses.expense;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.SqlTypes;
import br.com.jonathanzanella.myexpenses.database.Table;

import static br.com.jonathanzanella.myexpenses.database.CursorHelper.getDate;
import static br.com.jonathanzanella.myexpenses.database.CursorHelper.getInt;
import static br.com.jonathanzanella.myexpenses.database.CursorHelper.getLong;
import static br.com.jonathanzanella.myexpenses.database.CursorHelper.getString;

public final class ExpenseTable implements Table<Expense> {
	public void onCreate(@NonNull SQLiteDatabase db) {
		db.execSQL(createTableSql());
	}

	public void onUpgrade(@NonNull SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
	}

	@Override
	public void onDrop(@NonNull SQLiteDatabase db) {
		db.execSQL(dropTableSql());
	}

	@Override
	public String getName() {
		return "Expense";
	}

	private String createTableSql() {
		return "CREATE TABLE " + getName() + " (" +
				Fields.ID + SqlTypes.PRIMARY_KEY + "," +
				Fields.UUID + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
				Fields.NAME + SqlTypes.TEXT_NOT_NULL + "," +
				Fields.DATE + SqlTypes.DATE_NOT_NULL + "," +
				Fields.VALUE + SqlTypes.INT_NOT_NULL + "," +
				Fields.VALUE_TO_SHOW_IN_OVERVIEW + SqlTypes.INT_NOT_NULL + "," +
				Fields.CHARGEABLE_UUID + SqlTypes.TEXT_NOT_NULL + "," +
				Fields.CHARGEABLE_TYPE + SqlTypes.TEXT_NOT_NULL + "," +
				Fields.BILL_UUID + SqlTypes.TEXT + "," +
				Fields.CHARGED + SqlTypes.INT_NOT_NULL + "," +
				Fields.CHARGE_NEXT_MONTH + SqlTypes.INT_NOT_NULL + "," +
				Fields.IGNORE_IN_OVERVIEW + SqlTypes.INT_NOT_NULL + "," +
				Fields.IGNORE_IN_RESUME + SqlTypes.INT_NOT_NULL + "," +
				Fields.USER_UUID + SqlTypes.TEXT_NOT_NULL + "," +
				Fields.SERVER_ID + SqlTypes.TEXT_UNIQUE + "," +
				Fields.CREATED_AT + SqlTypes.DATE + "," +
				Fields.UPDATED_AT + SqlTypes.DATE + "," +
				Fields.REMOVED + SqlTypes.INT_NOT_NULL + "," +
				Fields.SYNC + SqlTypes.INT_NOT_NULL + " )";
	}

	private String dropTableSql() {
		return "DROP TABLE IF EXISTS " + getName();
	}

	@NonNull
	@Override
	public ContentValues fillContentValues(@NonNull Expense expense) {
		ContentValues values = new ContentValues();
		values.put(Fields.UUID.toString(), expense.getUuid());
		values.put(Fields.NAME.toString(), expense.getName());
		values.put(Fields.DATE.toString(), expense.getDate().getMillis());
		values.put(Fields.VALUE.toString(), expense.getValue());
		values.put(Fields.VALUE_TO_SHOW_IN_OVERVIEW.toString(), expense.getValueToShowInOverview());
		values.put(Fields.CHARGEABLE_UUID.toString(), expense.getChargeable().getUuid());
		values.put(Fields.CHARGEABLE_TYPE.toString(), expense.getChargeable().getChargeableType().toString());
		values.put(Fields.BILL_UUID.toString(), expense.getBillUuid());
		values.put(Fields.CHARGED.toString(), expense.isCharged() ? 1 : 0);
		values.put(Fields.CHARGE_NEXT_MONTH.toString(), expense.isChargedNextMonth() ? 1 : 0);
		values.put(Fields.IGNORE_IN_OVERVIEW.toString(), expense.isIgnoreInOverview() ? 1 : 0);
		values.put(Fields.IGNORE_IN_RESUME.toString(), expense.isIgnoreInResume() ? 1 : 0);
		values.put(Fields.USER_UUID.toString(), expense.getUserUuid());
		values.put(Fields.SERVER_ID.toString(), expense.getServerId());
		values.put(Fields.CREATED_AT.toString(), expense.getCreatedAt());
		values.put(Fields.UPDATED_AT.toString(), expense.getUpdatedAt());
		values.put(Fields.REMOVED.toString(), expense.isRemoved() ? 1 : 0);
		values.put(Fields.SYNC.toString(), expense.isSync() ? 1 : 0);
		return values;
	}

	@NonNull
	@Override
	public Expense fill(@NonNull Cursor c) {
		Expense expense = new Expense();
		expense.setId(getLong(c, Fields.ID));
		expense.setUuid(getString(c, Fields.UUID));
		expense.setName(getString(c, Fields.NAME));
		expense.setDate(getDate(c, Fields.DATE));
		expense.setValue(getInt(c, Fields.VALUE));
		expense.setValueToShowInOverview(getInt(c, Fields.VALUE_TO_SHOW_IN_OVERVIEW));
		expense.setChargeable(getString(c, Fields.CHARGEABLE_UUID),
				ChargeableType.getType(getString(c, Fields.CHARGEABLE_TYPE)));
		expense.setBillUuid(getString(c, Fields.BILL_UUID));
		expense.setCharged(getInt(c, Fields.CHARGED) != 0);
		expense.setChargedNextMonth(getInt(c, Fields.CHARGE_NEXT_MONTH) != 0);
		expense.setIgnoreInOverview(getInt(c, Fields.IGNORE_IN_OVERVIEW) != 0);
		expense.setIgnoreInResume(getInt(c, Fields.IGNORE_IN_RESUME) != 0);
		expense.setUserUuid(getString(c, Fields.USER_UUID));
		expense.setServerId(getString(c, Fields.SERVER_ID));
		expense.setCreatedAt(getLong(c, Fields.CREATED_AT));
		expense.setUpdatedAt(getLong(c, Fields.UPDATED_AT));
		expense.setRemoved(getInt(c, Fields.REMOVED) != 0);
		expense.setSync(getLong(c, Fields.SYNC) != 0);
		return expense;
	}

	@NonNull
	@Override
	public String [] getProjection() {
		return new String[]{
				Fields.ID.toString(),
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
				Fields.USER_UUID.toString(),
				Fields.SERVER_ID.toString(),
				Fields.CREATED_AT.toString(),
				Fields.UPDATED_AT.toString(),
				Fields.REMOVED.toString(),
				Fields.SYNC.toString()
		};
	}
}