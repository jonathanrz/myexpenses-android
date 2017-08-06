package br.com.jonathanzanella.myexpenses.receipt

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.log.Log
import br.com.jonathanzanella.myexpenses.server.Server
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import java.io.IOException

@WorkerThread
class ReceiptApi : UnsyncModelApi<Receipt> {
    private val receiptInterface: ReceiptInterface by lazy {
        Server(MyApplication.getContext()).receiptInterface()
    }
    private val receiptRepository: ReceiptRepository by lazy {
        ReceiptRepository(RepositoryImpl<Receipt>(MyApplication.getContext()))
    }

    override fun index(): List<Receipt> {
        val caller = receiptInterface.index(greaterUpdatedAt())

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                return response.body()
            } else {
                Log.error(LOG_TAG, "Index request error: " + response.message())
                return ArrayList()
            }
        } catch (e: IOException) {
            Log.error(LOG_TAG, "Index request error: " + e.message)
            e.printStackTrace()
            return ArrayList()
        }
    }

    override fun save(model: UnsyncModel) {
        val receipt = model as Receipt
        val caller: Call<Receipt>
        if (StringUtils.isNotEmpty(receipt.serverId))
            caller = receiptInterface.update(receipt.serverId, receipt)
        else
            caller = receiptInterface.create(receipt)

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                receiptRepository.syncAndSave(response.body())
                Log.info(LOG_TAG, "Updated: " + receipt.getData())
            } else {
                Log.error(LOG_TAG, "Save request error: " + response.message() + " uuid: " + receipt.uuid)
            }
        } catch (e: IOException) {
            Log.error(LOG_TAG, "Save request error: " + e.message + " uuid: " + receipt.uuid)
            e.printStackTrace()
        }

    }

    override fun syncAndSave(unsync: UnsyncModel) {
        if (unsync !is Receipt)
            throw UnsupportedOperationException("UnsyncModel is not a Receipt")
        receiptRepository.syncAndSave(unsync)
    }

    override fun unsyncModels(): List<Receipt> {
        return receiptRepository.unsync()
    }

    override fun greaterUpdatedAt(): Long {
        return receiptRepository.greaterUpdatedAt()
    }

    companion object {
        private val LOG_TAG = ReceiptApi::class.java.simpleName
    }
}