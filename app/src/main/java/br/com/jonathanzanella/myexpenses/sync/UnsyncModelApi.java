package br.com.jonathanzanella.myexpenses.sync;

import java.util.List;

public interface UnsyncModelApi<T extends UnsyncModel> {
	List<T> index();
	void save(UnsyncModel model);
	void syncAndSave(UnsyncModel unsync);
	List<T> unsyncModels();
	long greaterUpdatedAt();
}