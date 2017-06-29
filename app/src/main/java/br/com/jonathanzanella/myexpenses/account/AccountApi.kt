package br.com.jonathanzanella.myexpenses.account

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
class AccountApi : UnsyncModelApi<Account> {
    private var accountInterface: AccountInterface? = null
    private var accountRepository: AccountRepository? = null

    override fun index(): List<Account>? {
        val caller = `interface`.index(repository.greaterUpdatedAt())

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
        val account = model as Account
        val caller: Call<Account>
        if (StringUtils.isEmpty(account.serverId))
            caller = `interface`.create(account)
        else
            caller = `interface`.update(account.serverId!!, account)

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                repository.syncAndSave(response.body())
                Log.info(LOG_TAG, "Updated: " + account.getData())
            } else {
                Log.error(LOG_TAG, "Save request error: " + response.message() + " uuid: " + account.uuid)
            }
        } catch (e: IOException) {
            Log.error(LOG_TAG, "Save request error: " + e.message + " uuid: " + account.uuid)
            e.printStackTrace()
        }

    }

    override fun syncAndSave(unsyncAccount: UnsyncModel) {
        if (unsyncAccount !is Account)
            throw UnsupportedOperationException("UnsyncModel is not an Account")
        repository.syncAndSave(unsyncAccount)
    }

    override fun unsyncModels(): List<Account> {
        return repository.unsync()
    }

    override fun greaterUpdatedAt(): Long {
        return repository.greaterUpdatedAt()
    }

    val repository: AccountRepository by lazy {
        AccountRepository(RepositoryImpl<Account>(MyApplication.getContext()))
    }

    private val `interface`: AccountInterface by lazy {
        Server(MyApplication.getContext()).accountInterface()
    }

    companion object {
        private val LOG_TAG = AccountApi::class.java.simpleName
    }
}