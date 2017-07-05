package br.com.jonathanzanella.myexpenses.helpers

import android.content.Context
import android.support.v7.app.AlertDialog
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.receipt.Receipt
import br.com.jonathanzanella.myexpenses.transaction.Transaction

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
                    receipt.credit()
                    dialogCallback.onPositiveButton()
                }
                .setNegativeButton(android.R.string.no) { dialogInterface, _ -> dialogInterface.dismiss() }
                .show()
    }

    private fun showConfirmExpenseDialog(expense: Expense, ctx: Context, dialogCallback: DialogCallback) {
        if (expense.isCharged)
            return
        var message = ctx.getString(R.string.message_confirm_expense)
        message += (" " + expense.name + " - " + expense.incomeFormatted + "?")
        AlertDialog.Builder(ctx)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    expense.debit()
                    dialogCallback.onPositiveButton()
                }
                .setNegativeButton(android.R.string.no) { dialogInterface, _ -> dialogInterface.dismiss() }
                .show()
    }
}
