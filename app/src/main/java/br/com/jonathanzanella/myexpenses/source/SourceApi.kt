package br.com.jonathanzanella.myexpenses.source

import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.log.Log
import br.com.jonathanzanella.myexpenses.server.Server
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import java.io.IOException

class SourceApi : UnsyncModelApi<Source> {
    private val sourceInterface: SourceInterface by lazy {
        Server(MyApplication.getContext()).sourceInterface()
    }
    private val sourceRepository: SourceRepository by lazy {
        SourceRepository(RepositoryImpl<Source>(MyApplication.getContext()))
    }

    override fun index(): List<Source>? {
        val lastUpdatedAt = SourceRepository(RepositoryImpl<Source>(MyApplication.getContext())).greaterUpdatedAt()
        val caller = sourceInterface.index(lastUpdatedAt)

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                return response.body()
            } else {
                Log.error(LOG_TAG, "Index request error: " + response.message())
            }
        } catch (e: IOException) {
            Log.error(LOG_TAG, "Index request error: " + e.message)
            e.printStackTrace()
        }

        return null
    }

    override fun save(model: UnsyncModel) {
        val source = model as Source
        val caller: Call<Source>
        if (StringUtils.isNotEmpty(source.serverId))
            caller = sourceInterface.update(source.serverId!!, source)
        else
            caller = sourceInterface.create(source)

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                sourceRepository.syncAndSave(response.body())
                Log.info(LOG_TAG, "Updated: " + source.getData())
            } else {
                Log.error(LOG_TAG, "Save request error: " + response.message() + " uuid: " + source.uuid)
            }
        } catch (e: IOException) {
            Log.error(LOG_TAG, "Save request error: " + e.message + " uuid: " + source.uuid)
            e.printStackTrace()
        }

    }

    override fun syncAndSave(unsync: UnsyncModel) {
        if (unsync !is Source)
            throw UnsupportedOperationException("UnsyncModel is not a Source")
        sourceRepository.syncAndSave(unsync)
    }

    override fun unsyncModels(): List<Source> {
        return SourceRepository(RepositoryImpl<Source>(MyApplication.getContext())).unsync()
    }

    override fun greaterUpdatedAt(): Long {
        return SourceRepository(RepositoryImpl<Source>(MyApplication.getContext())).greaterUpdatedAt()
    }

    companion object {
        private val LOG_TAG = SourceApi::class.java.simpleName
    }
}