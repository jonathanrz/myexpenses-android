package br.com.jonathanzanella.myexpenses.account;

import android.support.annotation.StringRes;

import br.com.jonathanzanella.myexpenses.validations.ValidationError;

/**
 * Created by jzanella on 8/28/16.
 */

interface AccountContract {
	interface View {
		void showAccount(Account account);
	}

	interface EditView extends View {
		void setTitle(@StringRes int string);
		Account fillAccount(Account account);
		void finishView();
		void showError(ValidationError error);
	}
}
