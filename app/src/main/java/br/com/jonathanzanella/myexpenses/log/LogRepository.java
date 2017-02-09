package br.com.jonathanzanella.myexpenses.log;

import android.support.annotation.WorkerThread;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;

import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.database.Where;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

/**
 * Created by jzanella on 8/27/16.
 */

public class LogRepository {
	private Repository<Log> repository;
	private LogTable table = new LogTable();

	public LogRepository(Repository<Log> repository) {
		this.repository = repository;
	}

	@WorkerThread
	public Log find(final String uuid) {
		return repository.find(table, uuid);
	}

	public List<Log> all() {
		return repository.query(table, null);
	}

	public List<Log> filter(DateTime initDate, DateTime endDate, Log.LOG_LEVEL logLevel) {
		return repository.query(table, new Where(Fields.DATE).greaterThanOrEq(initDate.getMillis())
							.and(Fields.DATE).lessThanOrEq(endDate.getMillis())
							.and(Fields.TYPE).queryIn(logLevel.getLogLevelsAsString()));
	}

	@WorkerThread
	public OperationResult save(Log log) {
		OperationResult result = new OperationResult();
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