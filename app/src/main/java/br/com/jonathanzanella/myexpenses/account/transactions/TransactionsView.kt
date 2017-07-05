package br.com.jonathanzanella.myexpenses.account.transactions

import android.content.Context
import android.view.View
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.views.BaseView
import kotlinx.android.synthetic.main.view_account_transactions.view.*
import org.joda.time.DateTime

class TransactionsView(context: Context) : BaseView(context) {
    private var account : Account? = null
    private var monthToShow : DateTime? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        View.inflate(context, R.layout.view_account_transactions, this)

        account?.let {
            showTransactions(it, monthToShow!!)
            account = null
            monthToShow = null
        }
    }

    fun showTransactions(account: Account, monthToShow: DateTime) {
        if(thisMonth != null) {
            thisMonth.setLoadTransactionsCallback(object: LoadTransactionsCallback {
                override fun onTransactionsLoaded(balance: Int) {
                    nextMonth.showBalance(account, monthToShow.plusMonths(1), balance)
                }
            })

            thisMonth.showBalance(account, monthToShow, account.balance)
        } else {
            this.account = account
            this.monthToShow = monthToShow
        }

    }
}