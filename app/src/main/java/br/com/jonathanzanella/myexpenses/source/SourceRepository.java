package br.com.jonathanzanella.myexpenses.source;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static br.com.jonathanzanella.myexpenses.log.Log.warning;

/**
 * Created by jzanella on 8/27/16.
 */

public class SourceRepository {
	private DatabaseHelper databaseHelper;
	private SourceTable sourceTable = new SourceTable();

	public SourceRepository(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public Source find(String uuid) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Pair<String, String[]> selection = sourceTable.getSelectionByUuid(uuid);
		try (Cursor c = db.query(
				SourceTable.TABLE_NAME,
				sourceTable.getProjection(),
				selection.first,
				selection.second,
				null,
				null,
				null
		)) {
			c.moveToFirst();
			return sourceTable.fillSource(c);
		}
	}

	List<Source> userSources() {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Pair<String, String[]> selection = sourceTable.getSelectionByUserUuid(Environment.CURRENT_USER_UUID);
		try (Cursor c = db.query(
				SourceTable.TABLE_NAME,
				sourceTable.getProjection(),
				selection.first,
				selection.second,
				null,
				null,
				sourceTable.getOrderByName()
		)) {
			List<Source> sources = new ArrayList<>();
			c.moveToFirst();
			for(int i = 0; i < c.getCount(); i++) {
				c.move(i);
				sources.add(sourceTable.fillSource(c));
			}
			return sources;
		}
	}

	List<Source> unsync() {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Pair<String, String[]> selection = sourceTable.getSelectionBySync(false);
		try (Cursor c = db.query(
				SourceTable.TABLE_NAME,
				sourceTable.getProjection(),
				selection.first,
				selection.second,
				null,
				null,
				sourceTable.getOrderByName()
		)) {
			List<Source> sources = new ArrayList<>();
			c.moveToFirst();
			for(int i = 0; i < c.getCount(); i++) {
				c.move(i);
				sources.add(sourceTable.fillSource(c));
			}
			return sources;
		}
	}

	public long greaterUpdatedAt() {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		try (Cursor c = db.query(
				SourceTable.TABLE_NAME,
				sourceTable.getProjection(),
				null,
				null,
				null,
				null,
				sourceTable.getOrderByUpdatedAt(),
				"1"
		)) {
			c.moveToFirst();
			return sourceTable.fillSource(c).getUpdatedAt();
		}
	}

	public OperationResult save(Source source) {
		OperationResult result = new OperationResult();
		if(StringUtils.isEmpty(source.getName()))
			result.addError(ValidationError.NAME);
		if(result.isValid()) {
			if(source.getId() == 0 && source.getUuid() == null)
				source.setUuid(UUID.randomUUID().toString());
			if(source.getId() == 0 && source.getUserUuid() == null)
				source.setUserUuid(Environment.CURRENT_USER_UUID);
			source.setSync(false);
			saveAtDatabase(source);
		}
		return result;
	}

	private void saveAtDatabase(Source source) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		if(source.getId() == 0) {
			long newId = db.insert(SourceTable.TABLE_NAME, null, sourceTable.fillContentValues(source));
			source.setId(newId);
		} else {
			Pair<String, String[]> selection = sourceTable.getSelectionById(source.getId());
			db.update(SourceTable.TABLE_NAME, sourceTable.fillContentValues(source), selection.first, selection.second);
		}
	}

	public void syncAndSave(Source s, UnsyncModel unsyncModel) {
		Source unsyncSource = find(unsyncModel.getUuid());

		if(unsyncSource != null && unsyncSource.id != s.getId()) {
			if(unsyncSource.getUpdatedAt() != s.getUpdatedAt())
				warning("Source overwritten", s.getData());
			s.setId(unsyncSource.getId());
		}

		s.setServerId(unsyncModel.getServerId());
		s.setCreatedAt(unsyncModel.getCreatedAt());
		s.setUpdatedAt(unsyncModel.getUpdatedAt());
		s.setSync(true);
		saveAtDatabase(s);
	}

	public void resetSources() {
		sourceTable.recreate(databaseHelper.getWritableDatabase());
	}
}