package br.com.jonathanzanella.myexpenses.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;

/**
 * Created by jzanella on 7/9/16.
 */
public class TransactionsHelper {
	public interface DialogCallback {
		void onPositiveButton();
	}

	public static void showConfirmTransactionDialog(Transaction transaction, Context ctx, final DialogCallback dialogCallback) {
		if(transaction instanceof Receipt)
			showConfirmReceiptDialog((Receipt) transaction, ctx, dialogCallback);
		else if(transaction instanceof Expense)
			showConfirmExpenseDialog((Expense) transaction, ctx, dialogCallback);
	}

	private static void showConfirmReceiptDialog(final Receipt receipt, Context ctx, final DialogCallback dialogCallback) {
		if(receipt.isCredited())
			return;
		String message = ctx.getString(R.string.message_confirm_receipt);
		message = message.concat(" " + receipt.getName() + " - " + receipt.getIncomeFormatted() + "?");
		new AlertDialog.Builder(ctx)
				.setMessage(message)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						receipt.credit();
						dialogCallback.onPositiveButton();
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				})
				.show();
	}

	private static void showConfirmExpenseDialog(final Expense expense, Context ctx, final DialogCallback dialogCallback) {
		if(expense.isCharged())
			return;
		String message = ctx.getString(R.string.message_confirm_expense);
		message = message.concat(" " + expense.getName() + " - " + expense.getIncomeFormatted() + "?");
		new AlertDialog.Builder(ctx)
				.setMessage(message)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						expense.debit();
						dialogCallback.onPositiveButton();
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				})
				.show();
	}
}
