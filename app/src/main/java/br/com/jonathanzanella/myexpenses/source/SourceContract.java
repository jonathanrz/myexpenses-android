package br.com.jonathanzanella.myexpenses.source;

import android.content.Context;
import android.support.annotation.StringRes;

import br.com.jonathanzanella.myexpenses.validations.ValidationError;

/**
 * Created by jzanella on 8/27/16.
 */

interface SourceContract {
	interface View {
		Context getContext();
		void setTitle(@StringRes int string);
		void setTitle(String string);
		void showSource(Source source);
	}

	interface EditView extends View {
		Source fillSource(Source source);
		void finishView();
		void showError(ValidationError error);
	}
}