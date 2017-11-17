package br.com.jonathanzanella.myexpenses.source

import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class SourceApi @Inject constructor(private val sourceInterface: SourceInterface,
                                    private val sourceRepository: SourceRepository): UnsyncModelApi<Source> {

    override fun index(): List<Source> {
        val lastUpdatedAt = sourceRepository.greaterUpdatedAt()
        Timber.tag("SourceApi.index with lastUpdatedAt: $lastUpdatedAt")
        val caller = sourceInterface.index(lastUpdatedAt)

        return try {
            val response = caller.execute()
            if (response.isSuccessful) {
                response.body().orEmpty()
            } else {
                Timber.e("Index request error: " + response.message())
                ArrayList()
            }
        } catch (e: IOException) {
            Timber.e("Index request error: " + e.message)
            e.printStackTrace()
            ArrayList()
        }
    }

    override fun save(model: UnsyncModel) {
        val source = model as Source
        val caller: Call<Source>
        caller = if (StringUtils.isNotEmpty(source.serverId))
            sourceInterface.update(source.serverId!!, source)
        else
            sourceInterface.create(source)

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                sourceRepository.syncAndSave(response.body()!!)
                Timber.i("Updated: " + source.getData())
            } else {
                Timber.e("Save request error: " + response.message() + " uuid: " + source.uuid)
            }
        } catch (e: IOException) {
            Timber.e("Save request error: " + e.message + " uuid: " + source.uuid)
            e.printStackTrace()
        }
    }

    override fun syncAndSave(unsync: UnsyncModel) {
        if (unsync !is Source)
            throw UnsupportedOperationException("UnsyncModel is not a Source")
        sourceRepository.syncAndSave(unsync)
    }

    override fun unsyncModels(): List<Source> {
        return sourceRepository.unsync()
    }

    override fun greaterUpdatedAt(): Long {
        return sourceRepository.greaterUpdatedAt()
    }
}
