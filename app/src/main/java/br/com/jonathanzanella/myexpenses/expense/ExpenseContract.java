package br.com.jonathanzanella.myexpenses.expense;

import android.content.Context;
import android.support.annotation.StringRes;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

interface ExpenseContract {
	interface View {
		Context getContext();
		void setTitle(@StringRes int string);
		void setTitle(String string);
		void showExpense(Expense expense);
	}

	interface EditView extends View {
		Expense fillExpense(Expense expense);
		void finishView();
		void showError(ValidationError error);
		int getInstallment();
		int getRepetition();
		void onDateChanged(DateTime date);
		void onBillSelected(Bill bill);
		void onChargeableSelected(Chargeable chargeable);
	}
}
