package br.com.jonathanzanella.myexpenses.source

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import java.util.*

open class SourceRepository(val dao: SourceDao = MyApplication.database.sourceDao()) {
    @WorkerThread
    fun find(uuid: String): Source? {
        return dao.find(uuid).blockingFirst().firstOrNull()
    }

    @WorkerThread
    fun greaterUpdatedAt(): Long {
        return dao.greaterUpdatedAt().blockingFirst().firstOrNull()?.updatedAt ?: 0L
    }

    @WorkerThread
    fun all(): List<Source> {
        return dao.all().blockingFirst()
    }

    @WorkerThread
    fun unsync(): List<Source> {
        return dao.unsync().blockingFirst()
    }

    @WorkerThread
    fun save(source: Source): ValidationResult {
        val result = validate(source)
        if (result.isValid) {
            if (source.id == 0L && source.uuid == null)
                source.uuid = UUID.randomUUID().toString()
            source.sync = false
            source.id = dao.saveAtDatabase(source)
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
    fun syncAndSave(unsync: Source): ValidationResult {
        val result = validate(unsync)
        if (!result.isValid) {
            Timber.tag("Source sync vali failed").w(unsync.getData() + "\nerrors: " + result.errorsAsString)
            return result
        }

        val source = find(unsync.uuid!!)
        if (source != null && source.id != unsync.id) {
            if (source.updatedAt != unsync.updatedAt)
                Timber.tag("Source overwritten").w(unsync.getData())
            unsync.id = source.id
        }

        unsync.sync = true
        dao.saveAtDatabase(unsync)

        return result
    }
}