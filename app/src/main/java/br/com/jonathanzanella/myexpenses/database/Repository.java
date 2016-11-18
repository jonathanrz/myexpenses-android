package br.com.jonathanzanella.myexpenses.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.helpers.Subscriber;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import lombok.Getter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static br.com.jonathanzanella.myexpenses.log.Log.warning;

public class Repository <T extends UnsyncModel> {
	@Getter
	private DatabaseHelper databaseHelper;

	public Repository(Context ctx) {
		this.databaseHelper = new DatabaseHelper(ctx);
	}

	public Observable<T> find(final Table<T> table, final String uuid) {
		return Observable.fromCallable(new Callable<T>() {
			@Override
			public T call() throws Exception {
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
					if(c.getCount() == 0)
						return null;
					c.moveToFirst();
					return table.fill(c);
				}
			}
		})
		.subscribeOn(Schedulers.io())
		.observeOn(AndroidSchedulers.mainThread());
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
			try {
				long newId = db.insertOrThrow(table.getName(), null, table.fillContentValues(data));
				data.setId(newId);
			} catch (SQLException e) {
				Log.e("Repository", "error inserting the record into the database, error=" + e.getMessage());
				throw e;
			}
		} else {
			Select select = new Where(Fields.ID).eq(data.getId()).query();
			db.update(table.getName(), table.fillContentValues(data), select.getWhere(), select.getParameters());
		}
	}

	public void syncAndSave(final Table<T> table, final T unsyncModel) {
		find(table, unsyncModel.getUuid())
				.observeOn(Schedulers.io())
				.subscribe(new Subscriber<T>("Repository.syncAndSave") {
					@Override
					public void onNext(T unsyncSource) {
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
				});
	}
}
