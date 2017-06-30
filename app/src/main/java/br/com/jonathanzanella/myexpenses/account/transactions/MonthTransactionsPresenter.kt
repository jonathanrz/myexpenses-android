package br.com.jonathanzanella.myexpenses.account.transactions

import android.content.Context
import android.os.AsyncTask
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.bill.Bill
import br.com.jonathanzanella.myexpenses.bill.BillRepository
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.receipt.Receipt
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository
import br.com.jonathanzanella.myexpenses.transaction.TransactionAdapter
import org.joda.time.DateTime

internal class MonthTransactionsPresenter(ctx: Context, private val view: MonthTransactionsContractView) : LoadTransactionsCallback {
    val adapter: TransactionAdapter
    private val receiptRepository: ReceiptRepository
    private val expenseRepository: ExpenseRepository
    private val billRepository: BillRepository
    private var currentBalance: Int = 0

    init {
        adapter = TransactionAdapter()
        receiptRepository = ReceiptRepository(RepositoryImpl<Receipt>(ctx))
        expenseRepository = ExpenseRepository(RepositoryImpl<Expense>(ctx))
        billRepository = BillRepository(RepositoryImpl<Bill>(ctx), expenseRepository)
    }

    fun showBalance(account: Account, month: DateTime, balance: Int) {
        currentBalance = balance
        LoadData(this).load(account, month)
    }

    override fun onTransactionsLoaded(balance: Int) {
        for (transaction in adapter.getTransactions()) {
            if (!transaction.credited()) {
                currentBalance += transaction.amount
            } else if (!transaction.debited()) {
                currentBalance -= transaction.amount
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
            loadedBills = false

            if (account.isAccountToPayBills) {
                object : AsyncTask<Void, Void, Void>() {

                    override fun doInBackground(vararg voids: Void): Void? {
                        val bills = billRepository.monthly(month)
                        adapter.addTransactions(bills)
                        return null
                    }

                    override fun onPostExecute(v: Void?) {
                        super.onPostExecute(v)
                        adapter.notifyDataSetChanged()
                        loadedBills = true
                        onDataLoaded()
                    }
                }.execute()
            } else {
                loadedBills = true
            }
        }

        private fun loadExpenses(account: Account, month: DateTime) {
            loadedExpenses = false

            object : AsyncTask<Void, Void, Void>() {

                override fun doInBackground(vararg voids: Void): Void? {
                    val expenses = expenseRepository.accountExpenses(account, month)
                    adapter.addTransactions(expenses)
                    return null
                }

                override fun onPostExecute(v: Void?) {
                    super.onPostExecute(v)
                    adapter.notifyDataSetChanged()
                    loadedExpenses = true
                    onDataLoaded()
                }
            }.execute()
        }

        private fun loadReceipts(account: Account, month: DateTime) {
            loadedReceipts = false

            object : AsyncTask<Void, Void, Void>() {
                override fun doInBackground(vararg voids: Void): Void? {
                    adapter.addTransactions(receiptRepository.monthly(month, account))
                    return null
                }

                override fun onPostExecute(aVoid: Void) {
                    super.onPostExecute(aVoid)
                    adapter.notifyDataSetChanged()
                    loadedReceipts = true
                    onDataLoaded()
                }
            }.execute()
        }

        private fun onDataLoaded() {
            if (loadedBills && loadedExpenses && loadedReceipts)
                callback.onTransactionsLoaded(currentBalance)
        }
    }
}
