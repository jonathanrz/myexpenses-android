package br.com.jonathanzanella.myexpenses.source;

/**
 * Created by jzanella on 8/27/16.
 */

class SourcePresenter implements SourceContract.Presenter {
	private SourceContract.View view;
	private SourceRepository repository = new SourceRepository();
	private Source source;

	SourcePresenter(SourceContract.View view) {
		this.view = view;
	}

	@Override
	public void viewCreated() {
		if (source != null)
			view.showSource(source);
	}

	@Override
	public void loadSource(String uuid) {
		source = repository.find(uuid);
		if(source == null)
			throw new SourceNotFoundException(uuid);
	}

	@Override
	public void save() {
		if(source == null)
			source = new Source();
		view.fillSource(source);
		repository.save(source);
	}

	@Override
	public String getUuid() {
		return source != null ? source.getUuid() : null;
	}
}
