package br.com.jonathanzanella.myexpenses.account.transactions

import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.bill.BillDataSource
import br.com.jonathanzanella.myexpenses.expense.ExpenseDataSource
import br.com.jonathanzanella.myexpenses.receipt.ReceiptDataSource
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import io.reactivex.Flowable
import org.joda.time.DateTime

class MonthTransactionsPresenter(val billDataSource: BillDataSource, val expenseDataSource: ExpenseDataSource, val receiptDataSource: ReceiptDataSource) {
    fun getAccountTransactions(account: Account, month: DateTime) : Flowable<List<Transaction>> {
        return expenseDataSource.accountExpenses(account, month)
                .map {
                    val list = it.toMutableList()

                    if (account.accountToPayBills)
                        list.addAll(billDataSource.monthly(month).blockingFirst())
                    list.addAll(receiptDataSource.monthly(month, account))

                    list.toList()
                }
    }

    fun calculateAccountBalance(account: Account, transactions: List<Transaction>): Flowable<Int> = Flowable.fromCallable {
        var currentBalance = account.balance

        for (transaction in transactions) {
            with(transaction) {
                if (!credited()) currentBalance += amount
                if (!debited()) currentBalance -= amount
            }
        }

        currentBalance
    }
}
