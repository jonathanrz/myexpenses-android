package br.com.jonathanzanella.myexpenses.bill

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.server.Server
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import timber.log.Timber
import java.io.IOException

@WorkerThread
class BillApi : UnsyncModelApi<Bill> {
    private val billRepository: BillRepository
    private val billInterface: BillInterface by lazy {
        Server(App.getContext()).billInterface()
    }

    init {
        val expenseRepository = ExpenseRepository()
        billRepository = BillRepository(expenseRepository)
    }

    override fun index(): List<Bill> {
        val lastUpdatedAt = billRepository.greaterUpdatedAt()
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
                billRepository.syncAndSave(response.body())
                Timber.i("Updated: " + bill.getData())
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
        billRepository.syncAndSave(unsync)
    }

    override fun unsyncModels(): List<Bill> {
        return billRepository.unsync()
    }

    override fun greaterUpdatedAt(): Long {
        return billRepository.greaterUpdatedAt()
    }
}
