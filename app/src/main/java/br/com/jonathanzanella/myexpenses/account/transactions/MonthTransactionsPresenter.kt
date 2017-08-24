package br.com.jonathanzanella.myexpenses.account.transactions

import android.content.Context
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.bill.BillRepository
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.receipt.Receipt
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository
import br.com.jonathanzanella.myexpenses.transaction.TransactionAdapter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime

internal class MonthTransactionsPresenter(ctx: Context, private val view: MonthTransactionsContractView) {
    val adapter = TransactionAdapter()
    private val receiptRepository = ReceiptRepository()
    private val expenseRepository = ExpenseRepository(RepositoryImpl<Expense>(ctx))
    private val billRepository = BillRepository(expenseRepository)
    private var currentBalance: Int = 0

    fun showBalance(account: Account, month: DateTime, balance: Int) {
        currentBalance = balance

        doAsync {
            if (account.isAccountToPayBills)
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
