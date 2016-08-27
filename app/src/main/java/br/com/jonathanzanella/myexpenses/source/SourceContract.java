package br.com.jonathanzanella.myexpenses.source;

import android.support.annotation.StringRes;

/**
 * Created by jzanella on 8/27/16.
 */

interface SourceContract {
	interface Presenter {
		void viewUpdated(boolean invalidateCache);
		void loadSource(String uuid);
		void save();
		String getUuid();
	}

	interface View {
		void setTitle(@StringRes int string);
		void showSource(Source source);
		void fillSource(Source source);
		void finishView();
	}
}