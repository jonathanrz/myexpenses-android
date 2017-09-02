package br.com.jonathanzanella.myexpenses.account

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

internal class AccountAdapterPresenter(private val adapter: AccountAdapter, private val repository: AccountRepository) {

    private var accounts: List<Account>? = null

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
