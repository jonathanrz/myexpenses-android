package br.com.jonathanzanella.myexpenses.source;

/**
 * Created by jzanella on 8/27/16.
 */

interface SourceContract {
	interface Presenter {
		void viewCreated();
		void loadSource(String uuid);
		void save();
		String getUuid();
	}

	interface View {
		void showSource(Source source);
		void fillSource(Source source);
		void finishView();
	}
}