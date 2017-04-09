package br.com.jonathanzanella.myexpenses.database;

import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;

public interface ModelRepository<T extends UnsyncModel> {
	ValidationResult syncAndSave(final T unsyncAccount);
}
