package br.com.jonathanzanella.myexpenses.source;

import android.support.annotation.WorkerThread;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static br.com.jonathanzanella.myexpenses.log.Log.warning;

/**
 * Created by jzanella on 8/27/16.
 */

public class SourceRepository {
	private Repository<Source> repository;
	private SourceTable sourceTable = new SourceTable();

	public SourceRepository(Repository<Source> repository) {
		this.repository = repository;
	}

	@WorkerThread
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
			repository.saveAtDatabase(sourceTable, source);
		}
		return result;
	}

	@WorkerThread
	public Source find(final String uuid) {
		return repository.find(sourceTable, uuid);
	}

	@WorkerThread
	public long greaterUpdatedAt() {
		return repository.greaterUpdatedAt(sourceTable);
	}

	@WorkerThread
	List<Source> userSources() {
		return repository.userData(sourceTable);
	}

	@WorkerThread
	public List<Source> unsync() {
		return repository.unsync(sourceTable);
	}

	@WorkerThread
	public void syncAndSave(final Source sourceSync) {
		Source source = find(sourceSync.getUuid());
		if(source != null && source.id != sourceSync.getId()) {
			if(source.getUpdatedAt() != sourceSync.getUpdatedAt())
				warning("Bill overwritten", sourceSync.getData());
			sourceSync.setId(source.id);
		}

		sourceSync.setSync(true);
		repository.saveAtDatabase(sourceTable, sourceSync);
	}
}