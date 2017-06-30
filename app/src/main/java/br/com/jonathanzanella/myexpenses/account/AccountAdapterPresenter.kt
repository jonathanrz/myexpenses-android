package br.com.jonathanzanella.myexpenses.account

import android.os.AsyncTask

internal class AccountAdapterPresenter(private val adapter: AccountAdapter, private val repository: AccountRepository, format: AccountAdapter.Format) {

    private var accounts: List<Account>? = null

    fun loadAccountsAsync(format: AccountAdapter.Format) {
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                if (format === AccountAdapter.Format.RESUME) {
                    accounts = repository.forResumeScreen()
                } else {
                    accounts = repository.all()
                }
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                adapter.notifyDataSetChanged()
            }
        }.execute()
    }

    fun getAccount(position: Int): Account {
        return accounts!![position]
    }

    val accountsSize: Int
        get() {
            return accounts?.size ?: 0
        }
}
