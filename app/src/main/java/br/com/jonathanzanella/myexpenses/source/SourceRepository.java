package br.com.jonathanzanella.myexpenses.source;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.helpers.Subscriber;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import rx.Observable;
import rx.schedulers.Schedulers;

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

	public Observable<Source> find(final String uuid) {
		return Observable.fromCallable(new Callable<Source>() {
			@Override
			public Source call() throws Exception {
				return repository.find(sourceTable, uuid);
			}
		})
		.observeOn(Schedulers.io());
	}

	public long greaterUpdatedAt() {
		return repository.greaterUpdatedAt(sourceTable);
	}

	public List<Source> userSources() {
		return repository.userData(sourceTable);
	}

	public List<Source> unsync() {
		return repository.unsync(sourceTable);
	}

	public void syncAndSave(final Source sourceSync) {
		find(sourceSync.getUuid()).subscribe(new Subscriber<Source>("SourceRepository.syncAndSave") {
			@Override
			public void onNext(Source source) {
				if(source != null && source.id != sourceSync.getId()) {
					if(source.getUpdatedAt() != sourceSync.getUpdatedAt())
						warning("Bill overwritten", sourceSync.getData());
					sourceSync.setId(source.id);
				}

				sourceSync.setSync(true);
				repository.saveAtDatabase(sourceTable, sourceSync);
			}
		});
	}
}