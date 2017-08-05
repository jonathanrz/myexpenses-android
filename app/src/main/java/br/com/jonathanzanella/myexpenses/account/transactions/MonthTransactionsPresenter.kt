package br.com.jonathanzanella.myexpenses.account.transactions

import android.content.Context
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.bill.Bill
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

internal class MonthTransactionsPresenter(ctx: Context, private val view: MonthTransactionsContractView) : LoadTransactionsCallback {
    val adapter = TransactionAdapter()
    private val receiptRepository = ReceiptRepository(RepositoryImpl<Receipt>(ctx))
    private val expenseRepository = ExpenseRepository(RepositoryImpl<Expense>(ctx))
    private val billRepository = BillRepository(RepositoryImpl<Bill>(ctx), expenseRepository)
    private var currentBalance: Int = 0

    fun showBalance(account: Account, month: DateTime, balance: Int) {
        currentBalance = balance
        LoadData(this).load(account, month)
    }

    override fun onTransactionsLoaded(balance: Int) {
        for (transaction in adapter.getTransactions()) {
            with(transaction) {
                if (!credited()) currentBalance += amount
                if (!debited()) currentBalance -= amount
            }
        }

        view.onBalanceUpdated(currentBalance)
    }

    private inner class LoadData internal constructor(private val callback: LoadTransactionsCallback) {
        private var loadedBills = false
        private var loadedExpenses = false
        private var loadedReceipts = false

        internal fun load(account: Account, month: DateTime) {
            loadBills(account, month)
            loadExpenses(account, month)
            loadReceipts(account, month)
        }

        private fun loadBills(account: Account, month: DateTime) {
            if (!account.isAccountToPayBills) {
                loadedBills = true
                return
            }

            loadedBills = false
            doAsync {
                adapter.addTransactions(billRepository.monthly(month))

                uiThread {
                    adapter.notifyDataSetChanged()
                    loadedBills = true
                    onDataLoaded()
                }
            }
        }

        private fun loadExpenses(account: Account, month: DateTime) {
            loadedExpenses = false

            doAsync {
                adapter.addTransactions(expenseRepository.accountExpenses(account, month))

                uiThread {
                    adapter.notifyDataSetChanged()
                    loadedExpenses = true
                    onDataLoaded()
                }
            }
        }

        private fun loadReceipts(account: Account, month: DateTime) {
            loadedReceipts = false

            doAsync {
                adapter.addTransactions(receiptRepository.monthly(month, account))

                uiThread {
                    adapter.notifyDataSetChanged()
                    loadedReceipts = true
                    onDataLoaded()
                }
            }
        }

        private fun onDataLoaded() {
            if (loadedBills && loadedExpenses && loadedReceipts)
                callback.onTransactionsLoaded(currentBalance)
        }
    }
}
