package br.com.jonathanzanella.myexpenses.source;

import br.com.jonathanzanella.myexpenses.R;

/**
 * Created by jzanella on 8/27/16.
 */

class SourcePresenter {
	private SourceContract.View view;
	private SourceRepository repository;
	private Source source;

	SourcePresenter(SourceContract.View view, SourceRepository repository) {
		this.view = view;
		this.repository = repository;
	}

	void viewUpdated(boolean invalidateCache) {
		if (source != null) {
			if(invalidateCache)
				source = repository.find(source.getUuid());
			view.setTitle(R.string.edit_source_title);
			view.showSource(source);
		} else {
			view.setTitle(R.string.new_source_title);
		}
	}

	void loadSource(String uuid) {
		source = repository.find(uuid);
		if(source == null)
			throw new SourceNotFoundException(uuid);
	}

	void save() {
		if(source == null)
			source = new Source();
		view.fillSource(source);
		repository.save(source);
		view.finishView();
	}

	String getUuid() {
		return source != null ? source.getUuid() : null;
	}
}
