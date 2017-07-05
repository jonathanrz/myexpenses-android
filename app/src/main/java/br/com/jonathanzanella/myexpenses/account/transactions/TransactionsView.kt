package br.com.jonathanzanella.myexpenses.account.transactions

import android.content.Context
import android.view.View
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.views.BaseView
import kotlinx.android.synthetic.main.view_account_transactions.view.*
import org.joda.time.DateTime

class TransactionsView(context: Context) : BaseView(context) {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        View.inflate(context, R.layout.view_account_transactions, this)
    }

    fun showTransactions(account: Account, monthToShow: DateTime) {
        thisMonth.setLoadTransactionsCallback(object: LoadTransactionsCallback {
            override fun onTransactionsLoaded(balance: Int) {
                nextMonth.showBalance(account, monthToShow.plusMonths(1), balance)
            }
        })

        thisMonth.showBalance(account, monthToShow, account.balance)
    }
}