package br.com.jonathanzanella.myexpenses.account.transactions

import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.bill.BillRepository
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository
import br.com.jonathanzanella.myexpenses.transaction.TransactionAdapter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime
import javax.inject.Inject

class MonthTransactionsPresenter(private val view: MonthTransactionsContractView) {
    val adapter = TransactionAdapter()
    @Inject
    lateinit var receiptRepository: ReceiptRepository
    @Inject
    lateinit var expenseRepository: ExpenseRepository
    @Inject
    lateinit var billRepository: BillRepository
    private var currentBalance: Int = 0

    init {
        App.getAppComponent().inject(this)
    }

    fun showBalance(account: Account, month: DateTime, balance: Int) {
        currentBalance = balance

        doAsync {
            if (account.accountToPayBills)
                adapter.addTransactions(billRepository.monthly(month))
            adapter.addTransactions(expenseRepository.accountExpenses(account, month))
            adapter.addTransactions(receiptRepository.monthly(month, account))

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
