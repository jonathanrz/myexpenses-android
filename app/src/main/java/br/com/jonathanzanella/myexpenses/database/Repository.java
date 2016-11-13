package br.com.jonathanzanella.myexpenses.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import lombok.Getter;

import static br.com.jonathanzanella.myexpenses.log.Log.warning;

public class Repository <T extends UnsyncModel> {
	@Getter
	private DatabaseHelper databaseHelper;

	public Repository(Context ctx) {
		this.databaseHelper = new DatabaseHelper(ctx);
	}

	public @Nullable T find(Table<T> table, String uuid) {
		if(uuid == null)
			return null;
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Select select = new Where(Fields.UUID).eq(uuid).query();
		try (Cursor c = db.query(
				table.getName(),
				table.getProjection(),
				select.getWhere(),
				select.getParameters(),
				null,
				null,
				null
		)) {
			c.moveToFirst();
			return table.fill(c);
		}
	}

	public List<T> query(Table<T> table, Where where) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Select select = where.query();
		try (Cursor c = db.query(
				table.getName(),
				table.getProjection(),
				select.getWhere(),
				select.getParameters(),
				null,
				null,
				Fields.NAME.toString()
		)) {
			List<T> sources = new ArrayList<>();
			c.moveToFirst();
			while (!c.isAfterLast()) {
				sources.add(table.fill(c));
				c.moveToNext();
			}
			return sources;
		}
	}

	public List<T> userData(Table<T> table) {
		return query(table, new Where(Fields.USER_UUID).eq(Environment.CURRENT_USER_UUID));
	}

	public List<T> unsync(Table<T> table) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Select select = new Where(Fields.SYNC).eq(false).query();
		try (Cursor c = db.query(
				table.getName(),
				table.getProjection(),
				select.getWhere(),
				select.getParameters(),
				null,
				null,
				Fields.NAME.toString()
		)) {
			List<T> sources = new ArrayList<>();
			c.moveToFirst();
			for(int i = 0; i < c.getCount(); i++) {
				c.move(i);
				sources.add(table.fill(c));
			}
			return sources;
		}
	}

	public long greaterUpdatedAt(Table<T> table) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		try (Cursor c = db.query(
				table.getName(),
				table.getProjection(),
				null,
				null,
				null,
				null,
				Fields.UPDATED_AT.toString() + " DESC",
				"1"
		)) {
			c.moveToFirst();
			return table.fill(c).getUpdatedAt();
		}
	}

	public void saveAtDatabase(Table<T> table, T data) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		if(data.getId() == 0) {
			long newId = db.insert(table.getName(), null, table.fillContentValues(data));
			data.setId(newId);
		} else {
			Select select = new Where(Fields.ID).eq(data.getId()).query();
			db.update(table.getName(), table.fillContentValues(data), select.getWhere(), select.getParameters());
		}
	}

	public void syncAndSave(Table<T> table, T unsyncModel) {
		T unsyncSource = find(table, unsyncModel.getUuid());

		if(unsyncSource != null && unsyncSource.getId() != unsyncModel.getId()) {
			if(unsyncSource.getUpdatedAt() != unsyncModel.getUpdatedAt())
				warning("Source overwritten", unsyncModel.getData());
			unsyncModel.setId(unsyncSource.getId());
		}

		unsyncModel.setServerId(unsyncModel.getServerId());
		unsyncModel.setCreatedAt(unsyncModel.getCreatedAt());
		unsyncModel.setUpdatedAt(unsyncModel.getUpdatedAt());
		unsyncModel.setSync(true);
		saveAtDatabase(table, unsyncModel);
	}
}
