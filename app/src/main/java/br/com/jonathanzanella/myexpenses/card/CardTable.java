package br.com.jonathanzanella.myexpenses.card;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.SqlTypes;
import br.com.jonathanzanella.myexpenses.database.Table;

import static br.com.jonathanzanella.myexpenses.database.CursorHelper.getLong;
import static br.com.jonathanzanella.myexpenses.database.CursorHelper.getString;

public final class CardTable implements Table<Card> {
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
		return "Card";
	}

	private String createTableSql() {
		return "CREATE TABLE " + getName() + " (" +
				Fields.ID + SqlTypes.PRIMARY_KEY + "," +
				Fields.NAME + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
				Fields.UUID + SqlTypes.TEXT_UNIQUE_NOT_NULL + "," +
				Fields.TYPE + SqlTypes.TEXT_NOT_NULL + "," +
				Fields.ACCOUNT_UUID + SqlTypes.TEXT_NOT_NULL + "," +
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
	public ContentValues fillContentValues(@NonNull Card card) {
		ContentValues values = new ContentValues();
		values.put(Fields.NAME.toString(), card.getName());
		values.put(Fields.UUID.toString(), card.getUuid());
		values.put(Fields.TYPE.toString(), card.getType().getValue());
		values.put(Fields.ACCOUNT_UUID.toString(), card.getAccountUuid());
		values.put(Fields.SERVER_ID.toString(), card.getServerId());
		values.put(Fields.CREATED_AT.toString(), card.getCreatedAt());
		values.put(Fields.UPDATED_AT.toString(), card.getUpdatedAt());
		values.put(Fields.SYNC.toString(), card.isSync());
		return values;
	}

	@NonNull
	@Override
	public Card fill(@NonNull Cursor c) {
		Card card = new Card();
		card.setId(getLong(c, Fields.ID));
		card.setName(getString(c, Fields.NAME));
		card.setUuid(getString(c, Fields.UUID));
		card.setType(CardType.fromValue(getString(c, Fields.TYPE)));
		card.setAccountUuid(getString(c, Fields.ACCOUNT_UUID));
		card.setServerId(getString(c, Fields.SERVER_ID));
		card.setCreatedAt(getLong(c, Fields.CREATED_AT));
		card.setUpdatedAt(getLong(c, Fields.UPDATED_AT));
		card.setSync(getLong(c, Fields.SYNC) != 0);
		return card;
	}

	@NonNull
	@Override
	public String [] getProjection() {
		return new String[]{
				Fields.ID.toString(),
				Fields.NAME.toString(),
				Fields.UUID.toString(),
				Fields.TYPE.toString(),
				Fields.ACCOUNT_UUID.toString(),
				Fields.SERVER_ID.toString(),
				Fields.CREATED_AT.toString(),
				Fields.UPDATED_AT.toString(),
				Fields.SYNC.toString()
		};
	}
}