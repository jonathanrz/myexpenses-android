package br.com.jonathanzanella.myexpenses.helper;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.database.Table;
import br.com.jonathanzanella.myexpenses.database.Where;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;

public class MockRepository<T extends UnsyncModel> implements Repository<T> {
	private List<T> models = new ArrayList<>();

	@Nullable
	@Override
	public T find(@NotNull Table<T> table, @Nullable String uuid) {
		throw new NotImplementedException("");
	}

	@Nullable
	@Override
	public T querySingle(@NotNull Table<T> table, @Nullable Where where) {
		throw new NotImplementedException("");
	}

	@NotNull
	@Override
	public List<T> query(@NotNull Table<T> table, @Nullable Where where) {
		return models;
	}

	@NotNull
	@Override
	public List<T> query(@NotNull Table<T> table, @Nullable Where where, boolean single) {
		return Collections.singletonList(models.get(0));
	}

	@NotNull
	@Override
	public List<T> unsync(@NotNull Table<T> table) {
		throw new NotImplementedException("");
	}

	@Override
	public long greaterUpdatedAt(@NotNull Table<T> table) {
		throw new NotImplementedException("");
	}

	@Override
	public void saveAtDatabase(@NotNull Table<T> table, @NotNull T data) {
		models.add(data);
	}

	@Override
	public void syncAndSave(@NotNull Table<T> table, @NotNull T unsyncModel) {
		models.add(unsyncModel);
	}
}
