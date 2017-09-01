package br.com.jonathanzanella.myexpenses.source

import android.util.Log
import br.com.jonathanzanella.myexpenses.MyApplication
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
        SourceRepository()
    }

    override fun index(): List<Source> {
        val lastUpdatedAt = SourceRepository().greaterUpdatedAt()
        val caller = sourceInterface.index(lastUpdatedAt)

        return try {
            val response = caller.execute()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e(LOG_TAG, "Index request error: " + response.message())
                ArrayList()
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Index request error: " + e.message)
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
                sourceRepository.syncAndSave(response.body())
                Log.i(LOG_TAG, "Updated: " + source.getData())
            } else {
                Log.e(LOG_TAG, "Save request error: " + response.message() + " uuid: " + source.uuid)
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Save request error: " + e.message + " uuid: " + source.uuid)
            e.printStackTrace()
        }
    }

    override fun syncAndSave(unsync: UnsyncModel) {
        if (unsync !is Source)
            throw UnsupportedOperationException("UnsyncModel is not a Source")
        sourceRepository.syncAndSave(unsync)
    }

    override fun unsyncModels(): List<Source> {
        return SourceRepository().unsync()
    }

    override fun greaterUpdatedAt(): Long {
        return SourceRepository().greaterUpdatedAt()
    }

    companion object {
        private val LOG_TAG = SourceApi::class.java.simpleName
    }
}