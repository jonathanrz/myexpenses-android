package br.com.jonathanzanella.myexpenses.account

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.server.Server
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import timber.log.Timber
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

        Timber.tag("AccountApi.index with lastUpdatedAt: $lastUpdatedAt")

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
        val account = model as Account
        val caller: Call<Account>
        caller = when {
            StringUtils.isEmpty(account.serverId) -> accountInterface.create(account)
            else -> accountInterface.update(account.serverId!!, account)
        }

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                repository.syncAndSave(response.body())
                Timber.i("Updated: " + account.getData())
            } else {
                Timber.e("Save request error: " + response.message() + " uuid: " + account.uuid)
            }
        } catch (e: IOException) {
            Timber.e("Save request error: " + e.message + " uuid: " + account.uuid)
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
}