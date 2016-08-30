package br.com.jonathanzanella.myexpenses.bill;

import android.content.Context;
import android.support.annotation.StringRes;

import br.com.jonathanzanella.myexpenses.validations.ValidationError;

/**
 * Created by jzanella on 8/28/16.
 */

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
	}
}
