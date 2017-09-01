package br.com.jonathanzanella.myexpenses.helpers

import android.content.Context
import android.support.v7.app.AlertDialog
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.receipt.Receipt
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

object TransactionsHelper {

    interface DialogCallback {
        fun onPositiveButton()
    }

    fun showConfirmTransactionDialog(transaction: Transaction, ctx: Context, dialogCallback: DialogCallback) {
        if (transaction is Receipt)
            showConfirmReceiptDialog(transaction, ctx, dialogCallback)
        else if (transaction is Expense)
            showConfirmExpenseDialog(transaction, ctx, dialogCallback)
    }

    private fun showConfirmReceiptDialog(receipt: Receipt, ctx: Context, dialogCallback: DialogCallback) {
        if (receipt.isCredited)
            return
        var message = ctx.getString(R.string.message_confirm_receipt)
        message += (" " + receipt.name + " - " + receipt.incomeFormatted + "?")
        AlertDialog.Builder(ctx)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    doAsync {
                        receipt.credit()

                        uiThread { dialogCallback.onPositiveButton() }
                    }
                }
                .setNegativeButton(android.R.string.no) { dialogInterface, _ -> dialogInterface.dismiss() }
                .show()
    }

    private fun showConfirmExpenseDialog(expense: Expense, ctx: Context, dialogCallback: DialogCallback) {
        if (expense.charged)
            return
        var message = ctx.getString(R.string.message_confirm_expense)
        message += (" " + expense.name + " - " + expense.incomeFormatted + "?")
        AlertDialog.Builder(ctx)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    doAsync {
                        expense.debit()

                        uiThread { dialogCallback.onPositiveButton() }
                    }
                }
                .setNegativeButton(android.R.string.no) { dialogInterface, _ -> dialogInterface.dismiss() }
                .show()
    }
}
