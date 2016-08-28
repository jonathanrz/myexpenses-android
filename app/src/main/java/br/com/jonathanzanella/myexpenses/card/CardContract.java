package br.com.jonathanzanella.myexpenses.card;

import android.content.Context;
import android.support.annotation.StringRes;

import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

/**
 * Created by jzanella on 8/28/16.
 */

interface CardContract {
	interface View {
		Context getContext();
		void setTitle(@StringRes int string);
		void setTitle(String string);
		void showCard(Card card);
	}

	interface EditView extends View {
		Card fillCard(Card card);
		void finishView();
		void showError(ValidationError error);
		void onAccountSelected(Account account);
	}
}
