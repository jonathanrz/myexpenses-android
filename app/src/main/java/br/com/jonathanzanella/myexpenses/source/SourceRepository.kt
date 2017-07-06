package br.com.jonathanzanella.myexpenses.source

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.database.Fields
import br.com.jonathanzanella.myexpenses.database.ModelRepository
import br.com.jonathanzanella.myexpenses.database.Repository
import br.com.jonathanzanella.myexpenses.database.Where
import br.com.jonathanzanella.myexpenses.log.Log
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.apache.commons.lang3.StringUtils
import java.util.*

open class SourceRepository(private val repository: Repository<Source>) : ModelRepository<Source> {
    private val sourceTable = SourceTable()

    @WorkerThread
    fun find(uuid: String): Source? {
        return repository.find(sourceTable, uuid)
    }

    @WorkerThread
    fun greaterUpdatedAt(): Long {
        return repository.greaterUpdatedAt(sourceTable)
    }

    @WorkerThread
    fun all(): List<Source> {
        return repository.query(sourceTable, Where(null).orderBy(Fields.NAME))
    }

    @WorkerThread
    fun unsync(): List<Source> {
        return repository.unsync(sourceTable)
    }

    @WorkerThread
    fun save(source: Source): ValidationResult {
        val result = validate(source)
        if (result.isValid) {
            if (source.id == 0L && source.uuid == null)
                source.uuid = UUID.randomUUID().toString()
            source.sync = false
            repository.saveAtDatabase(sourceTable, source)
        }
        return result
    }

    private fun validate(source: Source): ValidationResult {
        val result = ValidationResult()
        if (StringUtils.isEmpty(source.name))
            result.addError(ValidationError.NAME)
        return result
    }

    @WorkerThread
    override fun syncAndSave(unsync: Source): ValidationResult {
        val result = validate(unsync)
        if (!result.isValid) {
            Log.warning("Source sync validation failed", unsync.getData() + "\nerrors: " + result.errorsAsString)
            return result
        }

        val source = find(unsync.uuid!!)
        if (source != null && source.id != unsync.id) {
            if (source.updatedAt != unsync.updatedAt)
                Log.warning("Source overwritten", unsync.getData())
            unsync.id = source.id
        }

        unsync.sync = true
        repository.saveAtDatabase(sourceTable, unsync)

        return result
    }
}