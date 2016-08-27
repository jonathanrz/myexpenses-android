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

	public List<Source> getSources() {
		return Collections.unmodifiableList(sources);
	}

	void addSource(Source source) {
		sources.add(source);
		adapter.notifyItemInserted(sources.size() - 1);
	}
}
