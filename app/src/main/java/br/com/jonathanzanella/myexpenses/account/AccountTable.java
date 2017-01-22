package br.com.jonathanzanella.myexpenses.account;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.SqlTypes;
import br.com.jonathanzanella.myexpenses.database.Table;

/**
 * Created by jzanella on 11/1/16.
 */

final class AccountTable implements Table<Account> {
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
		return "Bill";
	}

	private String createTableSql() {
		return "CREATE TABLE " + getName() + " (" +
				Fields.ID + SqlTypes.PRIMARY_KEY + "," +
				Fields.NAME + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
				Fields.UUID + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
				Fields.BALANCE + SqlTypes.INT + "," +
				Fields.ACCOUNT_TO_PAY_CREDIT_CARD + SqlTypes.INT + "," +
				Fields.ACCOUNT_TO_PAY_BILLS + SqlTypes.INT + "," +
				Fields.USER_UUID + SqlTypes.TEXT_NOT_NULL + "," +
				Fields.SERVER_ID + SqlTypes.TEXT_UNIQUE + "," +
				Fields.CREATED_AT + SqlTypes.DATE + "," +
				Fields.UPDATED_AT + SqlTypes.DATE + "," +
				Fields.SYNC + SqlTypes.INT + " )";
	}

	private String dropTableSql() {
		return "DROP TABLE IF EXISTS " + getName();
	}

	@NonNull
	@Override
	public ContentValues fillContentValues(@NonNull Account account) {
		ContentValues values = new ContentValues();
		values.put(Fields.NAME.toString(), account.getName());
		values.put(Fields.UUID.toString(), account.getUuid());
		values.put(Fields.BALANCE.toString(), account.getBalance());
		values.put(Fields.ACCOUNT_TO_PAY_CREDIT_CARD.toString(), account.isAccountToPayCreditCard());
		values.put(Fields.ACCOUNT_TO_PAY_BILLS.toString(), account.isAccountToPayBills());
		values.put(Fields.USER_UUID.toString(), account.getUserUuid());
		values.put(Fields.SERVER_ID.toString(), account.getServerId());
		values.put(Fields.CREATED_AT.toString(), account.getCreatedAt());
		values.put(Fields.UPDATED_AT.toString(), account.getUpdatedAt());
		values.put(Fields.SYNC.toString(), account.isSync());
		return values;
	}

	@NonNull
	@Override
	public Account fill(@NonNull Cursor c) {
		Account account = new Account();
		account.setId(c.getLong(c.getColumnIndexOrThrow(Fields.ID.toString())));
		account.setName(c.getString(c.getColumnIndexOrThrow(Fields.NAME.toString())));
		account.setUuid(c.getString(c.getColumnIndexOrThrow(Fields.UUID.toString())));
		account.setBalance(c.getInt(c.getColumnIndexOrThrow(Fields.BALANCE.toString())));
		account.setAccountToPayCreditCard(c.getLong(c.getColumnIndexOrThrow(Fields.ACCOUNT_TO_PAY_CREDIT_CARD.toString())) != 0);
		account.setAccountToPayBills(c.getLong(c.getColumnIndexOrThrow(Fields.ACCOUNT_TO_PAY_BILLS.toString())) != 0);
		account.setUserUuid((c.getString(c.getColumnIndexOrThrow(Fields.USER_UUID.toString()))));
		account.setServerId(c.getString(c.getColumnIndexOrThrow(Fields.SERVER_ID.toString())));
		account.setCreatedAt(c.getLong(c.getColumnIndexOrThrow(Fields.CREATED_AT.toString())));
		account.setUpdatedAt(c.getLong(c.getColumnIndexOrThrow(Fields.UPDATED_AT.toString())));
		account.setSync(c.getLong(c.getColumnIndexOrThrow(Fields.SYNC.toString())) != 0);
		return account;
	}

	@NonNull
	@Override
	public String [] getProjection() {
		return new String[]{
				Fields.ID.toString(),
				Fields.NAME.toString(),
				Fields.UUID.toString(),
				Fields.BALANCE.toString(),
				Fields.ACCOUNT_TO_PAY_CREDIT_CARD.toString(),
				Fields.ACCOUNT_TO_PAY_BILLS.toString(),
				Fields.USER_UUID.toString(),
				Fields.SERVER_ID.toString(),
				Fields.CREATED_AT.toString(),
				Fields.UPDATED_AT.toString(),
				Fields.SYNC.toString()
		};
	}
}