package br.com.jonathanzanella.myexpenses.account;

import android.content.Context;
import android.support.annotation.StringRes;

import br.com.jonathanzanella.myexpenses.validations.ValidationError;

/**
 * Created by jzanella on 8/28/16.
 */

interface AccountContract {
	interface View {
		Context getContext();
		void setTitle(@StringRes int string);
		void setTitle(String string);
		void showAccount(Account account);
	}

	interface EditView extends View {
		Account fillAccount(Account account);
		void finishView();
		void showError(ValidationError error);
	}
}
