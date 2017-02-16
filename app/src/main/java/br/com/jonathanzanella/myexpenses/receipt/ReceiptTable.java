package br.com.jonathanzanella.myexpenses.receipt;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.SqlTypes;
import br.com.jonathanzanella.myexpenses.database.Table;

import static br.com.jonathanzanella.myexpenses.database.CursorHelper.getDate;
import static br.com.jonathanzanella.myexpenses.database.CursorHelper.getInt;
import static br.com.jonathanzanella.myexpenses.database.CursorHelper.getLong;
import static br.com.jonathanzanella.myexpenses.database.CursorHelper.getString;

public final class ReceiptTable implements Table<Receipt> {
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
		return "Receipt";
	}

	private String createTableSql() {
		return "CREATE TABLE " + getName() + " (" +
				Fields.ID + SqlTypes.PRIMARY_KEY + "," +
				Fields.NAME + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
				Fields.UUID + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
				Fields.DATE + SqlTypes.INT_NOT_NULL + "," +
				Fields.INCOME + SqlTypes.INT_NOT_NULL + "," +
				Fields.SOURCE_UUID + SqlTypes.TEXT + "," +
				Fields.ACCOUNT_UUID + SqlTypes.TEXT_NOT_NULL + "," +
				Fields.CREDITED + SqlTypes.INT_NOT_NULL + "," +
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
	public ContentValues fillContentValues(@NonNull Receipt receipt) {
		ContentValues values = new ContentValues();
		values.put(Fields.NAME.toString(), receipt.getName());
		values.put(Fields.UUID.toString(), receipt.getUuid());
		values.put(Fields.DATE.toString(), receipt.getDate().getMillis());
		values.put(Fields.INCOME.toString(), receipt.getIncome());
		values.put(Fields.SOURCE_UUID.toString(), receipt.getSourceUuid());
		values.put(Fields.ACCOUNT_UUID.toString(), receipt.getAccountUuid());
		values.put(Fields.CREDITED.toString(), receipt.isCredited() ? 1 : 0);
		values.put(Fields.IGNORE_IN_RESUME.toString(), receipt.isIgnoreInResume() ? 1 : 0);
		values.put(Fields.USER_UUID.toString(), receipt.getUserUuid());
		values.put(Fields.SERVER_ID.toString(), receipt.getServerId());
		values.put(Fields.CREATED_AT.toString(), receipt.getCreatedAt());
		values.put(Fields.UPDATED_AT.toString(), receipt.getUpdatedAt());
		values.put(Fields.REMOVED.toString(), receipt.isRemoved());
		values.put(Fields.SYNC.toString(), receipt.isSync());
		return values;
	}

	@NonNull
	@Override
	public Receipt fill(@NonNull Cursor c) {
		Receipt receipt = new Receipt();
		receipt.setId(getLong(c, Fields.ID));
		receipt.setName(getString(c, Fields.NAME));
		receipt.setUuid(getString(c, Fields.UUID));
		receipt.setDate(getDate(c, Fields.DATE));
		receipt.setIncome(getInt(c, Fields.INCOME));
		receipt.setSourceUuid(getString(c, Fields.SOURCE_UUID));
		receipt.setAccountUuid(getString(c, Fields.ACCOUNT_UUID));
		receipt.setCredited(getInt(c, Fields.CREDITED) != 0);
		receipt.setIgnoreInResume(getInt(c, Fields.IGNORE_IN_RESUME) != 0);
		receipt.setUserUuid(getString(c, Fields.USER_UUID));
		receipt.setServerId(getString(c, Fields.SERVER_ID));
		receipt.setCreatedAt(getLong(c, Fields.CREATED_AT));
		receipt.setUpdatedAt(getLong(c, Fields.UPDATED_AT));
		receipt.setRemoved(getInt(c, Fields.REMOVED) != 0);
		receipt.setSync(getLong(c, Fields.SYNC) != 0);
		return receipt;
	}

	@NonNull
	@Override
	public String [] getProjection() {
		return new String[]{
				Fields.ID.toString(),
				Fields.NAME.toString(),
				Fields.UUID.toString(),
				Fields.DATE.toString(),
				Fields.INCOME.toString(),
				Fields.SOURCE_UUID.toString(),
				Fields.ACCOUNT_UUID.toString(),
				Fields.CREDITED.toString(),
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