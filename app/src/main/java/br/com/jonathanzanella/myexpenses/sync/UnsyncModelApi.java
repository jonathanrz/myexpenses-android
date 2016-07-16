package br.com.jonathanzanella.myexpenses.sync;

import java.util.List;

/**
 * Created by jzanella on 6/12/16.
 */
public interface UnsyncModelApi<T extends UnsyncModel> {
	List<T> index();
	void save(UnsyncModel model);
	List<T> unsyncModels();
	long greaterUpdatedAt();
}