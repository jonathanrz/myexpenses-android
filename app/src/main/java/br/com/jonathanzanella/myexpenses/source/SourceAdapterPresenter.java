package br.com.jonathanzanella.myexpenses.source;

import java.util.Collections;
import java.util.List;

/**
 * Created by jzanella on 8/27/16.
 */

class SourceAdapterPresenter {
	private SourceRepository repository;
	private SourceAdapter adapter;

	private List<Source> sources;

	SourceAdapterPresenter(SourceAdapter adapter, SourceRepository repository) {
		this.repository = repository;
		this.adapter = adapter;
		loadSources();
	}

	private void loadSources() {
		sources = repository.userSources();
	}

	List<Source> getSources(boolean invalidateCache) {
		if(invalidateCache)
			loadSources();
		return Collections.unmodifiableList(sources);
	}

	void addSource(Source source) {
		int i = sources.indexOf(source);
		if(i != -1) {
			sources.set(i, source);
		} else {
			sources.add(source);
			i = sources.size() - 1;
		}
		adapter.notifyItemInserted(i);
	}
}
