package br.com.jonathanzanella.myexpenses.account

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@WorkerThread
class AccountApi @Inject constructor(private val accountInterface: AccountInterface,
                                     private val repository: AccountRepository) : UnsyncModelApi<Account> {

    override fun index(): List<Account> {
        val lastUpdatedAt = repository.greaterUpdatedAt().blockingFirst()
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

    override fun unsyncModels(): List<Account> = repository.unsync().blockingFirst()

    override fun greaterUpdatedAt(): Long = repository.greaterUpdatedAt().blockingFirst()
}
