package br.com.jonathanzanella.myexpenses.account

import android.support.annotation.WorkerThread
import android.util.Log
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.server.Server
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import java.io.IOException

@WorkerThread
class AccountApi : UnsyncModelApi<Account> {
    private val accountInterface: AccountInterface by lazy {
        Server(MyApplication.getContext()).accountInterface()
    }
    val repository: AccountRepository by lazy {
        AccountRepository()
    }

    override fun index(): List<Account> {
        val lastUpdatedAt = repository.greaterUpdatedAt()
        val caller = accountInterface.index(lastUpdatedAt)

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                return response.body()
            } else {
                Log.e(LOG_TAG, "Index request error: " + response.message())
                return ArrayList()
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Index request error: " + e.message)
            e.printStackTrace()
            return ArrayList()
        }
    }

    override fun save(model: UnsyncModel) {
        val account = model as Account
        val caller: Call<Account>
        if (StringUtils.isEmpty(account.serverId))
            caller = accountInterface.create(account)
        else
            caller = accountInterface.update(account.serverId!!, account)

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                repository.syncAndSave(response.body())
                Log.i(LOG_TAG, "Updated: " + account.getData())
            } else {
                Log.e(LOG_TAG, "Save request error: " + response.message() + " uuid: " + account.uuid)
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Save request error: " + e.message + " uuid: " + account.uuid)
            e.printStackTrace()
        }

    }

    override fun syncAndSave(unsync: UnsyncModel) {
        if (unsync !is Account)
            throw UnsupportedOperationException("UnsyncModel is not an Account")
        repository.syncAndSave(unsync)
    }

    override fun unsyncModels(): List<Account> {
        return repository.unsync()
    }

    override fun greaterUpdatedAt(): Long {
        return repository.greaterUpdatedAt()
    }

    companion object {
        private val LOG_TAG = AccountApi::class.java.simpleName
    }
}