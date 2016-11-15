package br.com.jonathanzanella.myexpenses.bill;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.Table;

import static br.com.jonathanzanella.myexpenses.database.SqlTypes.DATE;
import static br.com.jonathanzanella.myexpenses.database.SqlTypes.INT;
import static br.com.jonathanzanella.myexpenses.database.SqlTypes.INT_NOT_NULL;
import static br.com.jonathanzanella.myexpenses.database.SqlTypes.PRIMARY_KEY;
import static br.com.jonathanzanella.myexpenses.database.SqlTypes.TEXT_NOT_NULL;
import static br.com.jonathanzanella.myexpenses.database.SqlTypes.TEXT_UNIQUE;
import static br.com.jonathanzanella.myexpenses.database.SqlTypes.TEXT_UNIQUE_NOT_NULL;

/**
 * Created by jzanella on 11/1/16.
 */

public final class BillTable implements Table<Bill> {
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createTableSql());
	}

	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
	}

	@Override
	public void onDrop(SQLiteDatabase db) {
		db.execSQL(dropTableSql());
	}

	@Override
	public String getName() {
		return "Bill";
	}

	private String createTableSql() {
		return "CREATE TABLE " + getName() + " (" +
				Fields.ID + PRIMARY_KEY + "," +
				Fields.NAME + TEXT_UNIQUE_NOT_NULL + "," +
				Fields.UUID + TEXT_UNIQUE_NOT_NULL + "," +
				Fields.AMOUNT + INT+ "," +
				Fields.DUE_DATE + INT + "," +
				Fields.INIT_DATE + INT_NOT_NULL + "," +
				Fields.END_DATE + INT_NOT_NULL + "," +
				Fields.USER_UUID + TEXT_NOT_NULL + "," +
				Fields.SERVER_ID + TEXT_UNIQUE + "," +
				Fields.CREATED_AT + DATE + "," +
				Fields.UPDATED_AT + DATE + "," +
				Fields.SYNC + INT + " )";
	}

	private String dropTableSql() {
		return "DROP TABLE IF EXISTS " + getName();
	}

	@Override
	public ContentValues fillContentValues(Bill bill) {
		ContentValues values = new ContentValues();
		values.put(Fields.NAME.toString(), bill.getName());
		values.put(Fields.UUID.toString(), bill.getUuid());
		values.put(Fields.AMOUNT.toString(), bill.getAmount());
		values.put(Fields.DUE_DATE.toString(), bill.getDueDate());
		values.put(Fields.INIT_DATE.toString(), bill.getInitDate().getMillis());
		values.put(Fields.END_DATE.toString(), bill.getEndDate().getMillis());
		values.put(Fields.USER_UUID.toString(), bill.getUserUuid());
		values.put(Fields.SERVER_ID.toString(), bill.getServerId());
		values.put(Fields.CREATED_AT.toString(), bill.getCreatedAt());
		values.put(Fields.UPDATED_AT.toString(), bill.getUpdatedAt());
		values.put(Fields.SYNC.toString(), bill.isSync());
		return values;
	}

	@Override
	public Bill fill(Cursor c) {
		Bill bill = new Bill();
		bill.setId(c.getLong(c.getColumnIndexOrThrow(Fields.ID.toString())));
		bill.setName(c.getString(c.getColumnIndexOrThrow(Fields.NAME.toString())));
		bill.setUuid(c.getString(c.getColumnIndexOrThrow(Fields.UUID.toString())));
		bill.setAmount(c.getInt(c.getColumnIndexOrThrow(Fields.AMOUNT.toString())));
		bill.setDueDate(c.getInt(c.getColumnIndexOrThrow(Fields.DUE_DATE.toString())));
		bill.setInitDate(new DateTime(c.getLong(c.getColumnIndexOrThrow(Fields.INIT_DATE.toString()))));
		bill.setEndDate((new DateTime(c.getLong(c.getColumnIndexOrThrow(Fields.END_DATE.toString())))));
		bill.setUserUuid((c.getString(c.getColumnIndexOrThrow(Fields.USER_UUID.toString()))));
		bill.setServerId(c.getString(c.getColumnIndexOrThrow(Fields.SERVER_ID.toString())));
		bill.setCreatedAt(c.getLong(c.getColumnIndexOrThrow(Fields.CREATED_AT.toString())));
		bill.setUpdatedAt(c.getLong(c.getColumnIndexOrThrow(Fields.UPDATED_AT.toString())));
		bill.setSync(c.getLong(c.getColumnIndexOrThrow(Fields.UPDATED_AT.toString())) != 0);
		return bill;
	}

	@Override
	public String [] getProjection() {
		return new String[]{
				Fields.ID.toString(),
				Fields.NAME.toString(),
				Fields.UUID.toString(),
				Fields.AMOUNT.toString(),
				Fields.DUE_DATE.toString(),
				Fields.INIT_DATE.toString(),
				Fields.END_DATE.toString(),
				Fields.USER_UUID.toString(),
				Fields.SERVER_ID.toString(),
				Fields.CREATED_AT.toString(),
				Fields.UPDATED_AT.toString(),
				Fields.SYNC.toString()
		};
	}
}