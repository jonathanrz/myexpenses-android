package br.com.jonathanzanella.myexpenses.log

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.database.Fields
import br.com.jonathanzanella.myexpenses.database.Repository
import br.com.jonathanzanella.myexpenses.database.Where
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime

internal class LogRepository(private val repository: Repository<Log>) {
    private val table = LogTable()

    @WorkerThread
    fun find(uuid: String): Log? {
        return repository.find(table, uuid)
    }

    fun filter(initDate: DateTime, endDate: DateTime, logLevel: Log.LogLevel): List<Log> {
        return repository.query(table, Where(Fields.DATE).greaterThanOrEq(initDate.millis)
                .and(Fields.DATE).lessThanOrEq(endDate.millis)
                .and(Fields.TYPE).queryIn(logLevel.logLevelsAsString)
                .orderBy(Fields.DATE))
    }

    @WorkerThread
    fun save(log: Log): ValidationResult {
        val result = ValidationResult()
        if (StringUtils.isEmpty(log.title))
            result.addError(ValidationError.TITLE)
        if (StringUtils.isEmpty(log.description))
            result.addError(ValidationError.DESCRIPTION)
        if (log.date == null)
            result.addError(ValidationError.DATE)
        if (result.isValid)
            repository.saveAtDatabase(table, log)
        return result
    }
}