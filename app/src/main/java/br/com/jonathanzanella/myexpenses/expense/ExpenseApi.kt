package br.com.jonathanzanella.myexpenses.expense

import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.log.Log
import br.com.jonathanzanella.myexpenses.server.Server
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import java.io.IOException

class ExpenseApi(private val expenseRepository: ExpenseRepository) : UnsyncModelApi<Expense> {
    private val expenseInterface : ExpenseInterface by lazy {
        Server(MyApplication.getContext()).expenseInterface()
    }

    override fun index(): List<Expense> {
        val caller = expenseInterface.index(greaterUpdatedAt())

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
        val expense = model as Expense
        val caller: Call<Expense>
        if (StringUtils.isNotEmpty(expense.serverId))
            caller = expenseInterface.update(expense.serverId!!, expense)
        else
            caller = expenseInterface.create(expense)

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                expenseRepository.syncAndSave(response.body())
                Log.info(LOG_TAG, "Updated: " + expense.getData())
            } else {
                Log.error(LOG_TAG, "Save request error: " + response.message() + " uuid: " + expense.uuid)
            }
        } catch (e: IOException) {
            Log.error(LOG_TAG, "Save request error: " + e.message + " uuid: " + expense.uuid)
            e.printStackTrace()
        }

    }

    override fun syncAndSave(unsync: UnsyncModel) {
        if (unsync !is Expense)
            throw UnsupportedOperationException("UnsyncModel is not a Expense")
        expenseRepository.syncAndSave(unsync)
    }

    override fun unsyncModels(): List<Expense> {
        return expenseRepository.unsync()
    }

    override fun greaterUpdatedAt(): Long {
        return expenseRepository.greaterUpdatedAt()
    }

    companion object {
        private val LOG_TAG = ExpenseApi::class.java.simpleName
    }
}