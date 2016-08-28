package br.com.jonathanzanella.myexpenses.source;

import android.support.annotation.StringRes;

import br.com.jonathanzanella.myexpenses.validations.ValidationError;

/**
 * Created by jzanella on 8/27/16.
 */

interface SourceContract {
	interface View {
		void setTitle(@StringRes int string);
		void showSource(Source source);
		Source fillSource(Source source);
		void finishView();
		void showError(ValidationError error);
	}
}