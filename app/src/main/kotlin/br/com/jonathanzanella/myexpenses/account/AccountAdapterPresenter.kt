package br.com.jonathanzanella.myexpenses.account

import br.com.jonathanzanella.myexpenses.App
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class AccountAdapterPresenter(private val adapter: AccountAdapter) {

    private var accounts: List<Account>? = null

    @Inject
    lateinit var repository: AccountRepository

    init {
        App.getAppComponent().inject(this@AccountAdapterPresenter)
    }

    fun loadAccountsAsync(format: AccountAdapter.Format) {
        doAsync {
            accounts = when {
                format === AccountAdapter.Format.RESUME -> repository.forResumeScreen()
                else -> repository.all()
            }

            uiThread { adapter.notifyDataSetChanged() }
        }
    }

    fun getAccount(position: Int): Account {
        return accounts!![position]
    }

    val accountsSize: Int
        get() {
            return accounts?.size ?: 0
        }
}
