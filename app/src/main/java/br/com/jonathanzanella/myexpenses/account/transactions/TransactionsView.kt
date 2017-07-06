package br.com.jonathanzanella.myexpenses.account.transactions

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import kotlinx.android.synthetic.main.view_account_transactions.view.*
import org.joda.time.DateTime

class TransactionsView: FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
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