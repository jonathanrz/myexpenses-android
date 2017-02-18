package br.com.jonathanzanella.myexpenses.bill;

import android.content.Context;
import android.support.annotation.StringRes;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.validations.ValidationError;

interface BillContract {
	interface View {
		Context getContext();
		void setTitle(@StringRes int string);
		void setTitle(String string);
		void showBill(Bill bill);
	}

	interface EditView extends View {
		Bill fillBill(Bill bill);
		void finishView();
		void showError(ValidationError error);
		void onInitDateChanged(DateTime date);
		void onEndDateChanged(DateTime date);
	}
}
