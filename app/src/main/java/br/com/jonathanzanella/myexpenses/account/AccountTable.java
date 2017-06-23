package br.com.jonathanzanella.myexpenses.account;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.SqlTypes;
import br.com.jonathanzanella.myexpenses.database.Table;

import static br.com.jonathanzanella.myexpenses.database.CursorHelper.getInt;
import static br.com.jonathanzanella.myexpenses.database.CursorHelper.getLong;
import static br.com.jonathanzanella.myexpenses.database.CursorHelper.getString;

public final class AccountTable implements Table<Account> {
	public void onCreate(@NonNull SQLiteDatabase db) {
		db.execSQL(createTableSql());
	}

	public void onUpgrade(@NonNull SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		if(oldVersion <= 5)
			sqLiteDatabase.execSQL("ALTER TABLE " + getName() +  " ADD COLUMN " + Fields.SHOW_IN_RESUME + SqlTypes.INT + " DEFAULT 1");
	}

	@Override
	public void onDrop(@NonNull SQLiteDatabase db) {
		db.execSQL(dropTableSql());
	}

	@Override
	public String getName() {
		return "Account";
	}

	private String createTableSql() {
		return "CREATE TABLE " + getName() + " (" +
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
		values.put(Fields.SHOW_IN_RESUME.toString(), account.getShowInResume());
		values.put(Fields.SERVER_ID.toString(), account.getServerId());
		values.put(Fields.CREATED_AT.toString(), account.getCreatedAt());
		values.put(Fields.UPDATED_AT.toString(), account.getUpdatedAt());
		values.put(Fields.SYNC.toString(), account.getSync());
		return values;
	}

	@NonNull
	@Override
	public Account fill(@NonNull Cursor c) {
		Account account = new Account();
		account.setId(getLong(c, Fields.ID));
		account.setName(getString(c, Fields.NAME));
		account.setUuid(getString(c, Fields.UUID));
		account.setBalance(getInt(c, Fields.BALANCE));
		account.setAccountToPayCreditCard(getInt(c, Fields.ACCOUNT_TO_PAY_CREDIT_CARD) != 0);
		account.setAccountToPayBills(getInt(c, Fields.ACCOUNT_TO_PAY_BILLS) != 0);
		account.setShowInResume(getInt(c, Fields.SHOW_IN_RESUME) != 0);
		account.setServerId(getString(c, Fields.SERVER_ID));
		account.setCreatedAt(getLong(c, Fields.CREATED_AT));
		account.setUpdatedAt(getLong(c, Fields.UPDATED_AT));
		account.setSync(getLong(c, Fields.SYNC) != 0);
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
				Fields.SHOW_IN_RESUME.toString(),
				Fields.SERVER_ID.toString(),
				Fields.CREATED_AT.toString(),
				Fields.UPDATED_AT.toString(),
				Fields.SYNC.toString()
		};
	}
}