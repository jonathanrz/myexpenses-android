package br.com.jonathanzanella.myexpenses.expense

import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.server.Server
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import java.io.IOException

class ExpenseApi(private val expenseRepository: ExpenseRepository) : UnsyncModelApi<Expense> {
    private val expenseInterface : ExpenseInterface by lazy {
        Server(MyApplication.getContext()).expenseInterface()
    }

    override fun index(): List<Expense> {
        val lastUpdatedAt = greaterUpdatedAt()
        Timber.tag("AccountApi.index with lastUpdatedAt: $lastUpdatedAt")
        val caller = expenseInterface.index(lastUpdatedAt)

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
        val expense = model as Expense
        val caller = if (StringUtils.isNotEmpty(expense.serverId))
            expenseInterface.update(expense.serverId!!, expense)
        else
            expenseInterface.create(expense)

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                expenseRepository.syncAndSave(response.body())
                Timber.i("Updated: " + expense.getData())
            } else {
                Timber.e("Save request error: " + response.message() + " uuid: " + expense.uuid)
            }
        } catch (e: IOException) {
            Timber.e("Save request error: " + e.message + " uuid: " + expense.uuid)
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
}
