package br.com.jonathanzanella.myexpenses.source;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.ModelRepository;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.database.Where;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;

import static br.com.jonathanzanella.myexpenses.log.Log.warning;

public class SourceRepository implements ModelRepository<Source>  {
	private final Repository<Source> repository;
	private final SourceTable sourceTable = new SourceTable();

	public SourceRepository(Repository<Source> repository) {
		this.repository = repository;
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
	List<Source> all() {
		return repository.query(sourceTable, new Where(null).orderBy(Fields.NAME));
	}

	@WorkerThread
	public List<Source> unsync() {
		return repository.unsync(sourceTable);
	}

	@WorkerThread
	public ValidationResult save(Source source) {
		ValidationResult result = validate(source);
		if(result.isValid()) {
			if(source.getId() == 0 && source.getUuid() == null)
				source.setUuid(UUID.randomUUID().toString());
			source.setSync(false);
			repository.saveAtDatabase(sourceTable, source);
		}
		return result;
	}

	@NonNull
	private ValidationResult validate(Source source) {
		ValidationResult result = new ValidationResult();
		if(StringUtils.isEmpty(source.getName()))
			result.addError(ValidationError.NAME);
		return result;
	}

	@WorkerThread
	@Override
	public ValidationResult syncAndSave(final Source sourceSync) {
		ValidationResult result = validate(sourceSync);
		if(!result.isValid()) {
			warning("Source sync validation failed", sourceSync.getData() + "\nerrors: " + result.getErrorsAsString());
			return result;
		}

		Source source = find(sourceSync.getUuid());
		if(source != null && source.getId() != sourceSync.getId()) {
			if(source.getUpdatedAt() != sourceSync.getUpdatedAt())
				warning("Source overwritten", sourceSync.getData());
			sourceSync.setId(source.getId());
		}

		sourceSync.setSync(true);
		repository.saveAtDatabase(sourceTable, sourceSync);

		return result;
	}
}