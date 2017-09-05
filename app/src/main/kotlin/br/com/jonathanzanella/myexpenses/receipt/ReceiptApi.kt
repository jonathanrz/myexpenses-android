package br.com.jonathanzanella.myexpenses.receipt

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.server.Server
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import java.io.IOException

@WorkerThread
class ReceiptApi : UnsyncModelApi<Receipt> {
    private val receiptInterface: ReceiptInterface by lazy {
        Server(App.getContext()).receiptInterface()
    }
    private val receiptRepository: ReceiptRepository by lazy {
        ReceiptRepository()
    }

    override fun index(): List<Receipt> {
        val lastUpdatedAt = greaterUpdatedAt()
        Timber.tag("ReceiptApi.index with lastUpdatedAt: $lastUpdatedAt")
        val caller = receiptInterface.index(lastUpdatedAt)

        return try {
            val response = caller.execute()
            if (response.isSuccessful) {
                response.body()
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
        val receipt = model as Receipt
        val caller = if (StringUtils.isNotEmpty(receipt.serverId))
            receiptInterface.update(receipt.serverId, receipt)
        else
            receiptInterface.create(receipt)

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                receiptRepository.syncAndSave(response.body())
                Timber.i("Updated: " + receipt.getData())
            } else {
                Timber.e("Save request error: " + response.message() + " uuid: " + receipt.uuid)
            }
        } catch (e: IOException) {
            Timber.e("Save request error: " + e.message + " uuid: " + receipt.uuid)
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
}
