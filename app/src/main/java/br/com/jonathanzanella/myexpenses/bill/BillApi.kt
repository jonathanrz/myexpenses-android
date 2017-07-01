package br.com.jonathanzanella.myexpenses.bill

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.account.AccountApi
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.log.Log
import br.com.jonathanzanella.myexpenses.server.Server
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import java.io.IOException

@WorkerThread
class BillApi : UnsyncModelApi<Bill> {
    private val billRepository: BillRepository
    private val billInterface: BillInterface by lazy {
        Server(MyApplication.getContext()).billInterface()
    }

    init {
        val expenseRepository = ExpenseRepository(RepositoryImpl<Expense>(MyApplication.getContext()))
        billRepository = BillRepository(RepositoryImpl<Bill>(MyApplication.getContext()), expenseRepository)
    }

    override fun index(): List<Bill>? {
        val caller = billInterface.index(billRepository.greaterUpdatedAt())

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
        val bill = model as Bill
        val caller: Call<Bill>
        if (StringUtils.isNotEmpty(bill.serverId))
            caller = billInterface.update(bill.serverId!!, bill)
        else
            caller = billInterface.create(bill)

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                billRepository.syncAndSave(response.body())
                Log.info(LOG_TAG, "Updated: " + bill.getData())
            } else {
                Log.error(LOG_TAG, "Save request error: " + response.message() + " uuid: " + bill.uuid)
            }
        } catch (e: IOException) {
            Log.error(LOG_TAG, "Save request error: " + e.message + " uuid: " + bill.uuid)
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

    companion object {
        private val LOG_TAG = AccountApi::class.java.simpleName
    }
}