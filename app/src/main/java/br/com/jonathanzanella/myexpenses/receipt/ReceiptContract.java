package br.com.jonathanzanella.myexpenses.receipt;

import android.content.Context;
import android.support.annotation.StringRes;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

interface ReceiptContract {
	interface View {
		Context getContext();
		void setTitle(@StringRes int string);
		void setTitle(String string);
		void showReceipt(Receipt receipt);
	}

	interface EditView extends View {
		Receipt fillReceipt(Receipt receipt);
		void finishView();
		void showError(ValidationError error);
		void onSourceSelected(Source source);
		void onAccountSelected(Account account);
		int getInstallment();
		int getRepetition();
		void onDateChanged(DateTime balanceDate);
	}
}
