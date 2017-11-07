package br.com.jonathanzanella.myexpenses.bill

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@WorkerThread
class BillApi @Inject constructor(private val billInterface: BillInterface,
                                  private val billRepository: BillRepository): UnsyncModelApi<Bill> {

    override fun index(): List<Bill> {
        val lastUpdatedAt = billRepository.greaterUpdatedAt().blockingFirst()
        Timber.tag("BillApi.index with lastUpdatedAt: $lastUpdatedAt")

        val caller = billInterface.index(lastUpdatedAt)

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
        val bill = model as Bill
        val caller: Call<Bill>
        caller = if (StringUtils.isNotEmpty(bill.serverId))
            billInterface.update(bill.serverId!!, bill)
        else
            billInterface.create(bill)

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                val validationResult = billRepository.syncAndSave(response.body()).blockingFirst()
                Timber.i("Updated: ${bill.getData()} errors ${validationResult.errorsAsString}")
            } else {
                Timber.e("Save request error: " + response.message() + " uuid: " + bill.uuid)
            }
        } catch (e: IOException) {
            Timber.e("Save request error: " + e.message + " uuid: " + bill.uuid)
            e.printStackTrace()
        }

    }

    override fun syncAndSave(unsync: UnsyncModel) {
        if (unsync !is Bill)
            throw UnsupportedOperationException("UnsyncModel is not a Bill")
        val validationResult = billRepository.syncAndSave(unsync).blockingFirst()
        Timber.i("Updated: ${unsync.getData()} errors ${validationResult.errorsAsString}")
    }

    override fun unsyncModels(): List<Bill> = billRepository.unsync().blockingFirst()

    override fun greaterUpdatedAt(): Long = billRepository.greaterUpdatedAt().blockingFirst()
}
