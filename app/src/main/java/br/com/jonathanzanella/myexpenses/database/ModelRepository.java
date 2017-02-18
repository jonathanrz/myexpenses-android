package br.com.jonathanzanella.myexpenses.database;

import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;

public interface ModelRepository<T extends UnsyncModel> {
	void syncAndSave(final T unsyncAccount);
}
