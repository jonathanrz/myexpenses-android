package br.com.jonathanzanella.myexpenses.log;

import android.support.annotation.WorkerThread;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;

import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.database.Where;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

class LogRepository {
	private final Repository<Log> repository;
	private final LogTable table = new LogTable();

	LogRepository(Repository<Log> repository) {
		this.repository = repository;
	}

	@WorkerThread
	public Log find(final String uuid) {
		return repository.find(table, uuid);
	}

	public List<Log> filter(DateTime initDate, DateTime endDate, Log.LogLevel logLevel) {
		return repository.query(table, new Where(Fields.DATE).greaterThanOrEq(initDate.getMillis())
							.and(Fields.DATE).lessThanOrEq(endDate.getMillis())
							.and(Fields.TYPE).queryIn(logLevel.getLogLevelsAsString())
							.orderBy(Fields.DATE));
	}

	@WorkerThread
	public ValidationResult save(Log log) {
		ValidationResult result = new ValidationResult();
		if(StringUtils.isEmpty(log.getTitle()))
			result.addError(ValidationError.TITLE);
		if(StringUtils.isEmpty(log.getDescription()))
			result.addError(ValidationError.DESCRIPTION);
		if(log.getDate() == null)
			result.addError(ValidationError.DATE);
		if(log.getLogLevel() == null)
			result.addError(ValidationError.LOG_LEVEL);
		if(result.isValid())
			repository.saveAtDatabase(table, log);
		return result;
	}
}