package br.com.jonathanzanella.myexpenses.source;

import java.util.Collections;
import java.util.List;

class SourceAdapterPresenter {
	private final SourceRepository repository;

	private List<Source> sources;

	SourceAdapterPresenter(SourceRepository repository) {
		this.repository = repository;
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
}
