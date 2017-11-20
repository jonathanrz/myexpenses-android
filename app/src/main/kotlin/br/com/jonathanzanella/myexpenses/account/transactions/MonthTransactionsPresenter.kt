package br.com.jonathanzanella.myexpenses.account.transactions

import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.bill.BillDataSource
import br.com.jonathanzanella.myexpenses.expense.ExpenseDataSource
import br.com.jonathanzanella.myexpenses.receipt.ReceiptDataSource
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import br.com.jonathanzanella.myexpenses.transaction.TransactionAdapter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime
import javax.inject.Inject

class MonthTransactionsPresenter(private val view: MonthTransactionsContractView) {
    val adapter = TransactionAdapter()
    @Inject
    lateinit var receiptDataSource: ReceiptDataSource
    @Inject
    lateinit var expenseDataSource: ExpenseDataSource
    @Inject
    lateinit var billDataSource: BillDataSource
    private var currentBalance: Int = 0

    init {
        App.getAppComponent().inject(this)
    }

    fun showBalance(account: Account, month: DateTime, balance: Int) {
        currentBalance = balance

        doAsync {
            val list = ArrayList<Transaction>()

            if (account.accountToPayBills)
                list.addAll(billDataSource.monthly(month).blockingFirst())
            list.addAll(expenseDataSource.accountExpenses(account, month))
            list.addAll(receiptDataSource.monthly(month, account))

            adapter.setTransactions(list)

            for (transaction in adapter.getTransactions()) {
                with(transaction) {
                    if (!credited()) currentBalance += amount
                    if (!debited()) currentBalance -= amount
                }
            }

            uiThread {
                adapter.notifyDataSetChanged()
                view.onBalanceUpdated(currentBalance)
            }
        }
    }
}
