package br.com.jonathanzanella.myexpenses.source;

import android.support.annotation.Nullable;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException;
import br.com.jonathanzanella.myexpenses.helpers.Subscriber;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

/**
 * Created by jzanella on 8/27/16.
 */

class SourcePresenter {
	private SourceContract.View view;
	@Nullable
	private SourceContract.EditView editView;
	private SourceRepository repository;
	private Source source;

	SourcePresenter(SourceRepository repository) {
		this.repository = repository;
	}

	void attachView(SourceContract.View view) {
		this.view = view;
	}

	void attachView(SourceContract.EditView view) {
		this.view = view;
		this.editView = view;
	}

	void detachView() {
		this.view = null;
		this.editView = null;
	}

	void viewUpdated(boolean invalidateCache) {
		if (source != null) {
			if(invalidateCache) {
				repository.find(source.getUuid()).subscribe(new Subscriber<Source>("SourcePresenter.viewUpdated") {
					@Override
					public void onNext(Source source) {
						SourcePresenter.this.source = source;
						if (editView != null) {
							editView.setTitle(R.string.edit_source_title);
						} else {
							String title = view.getContext().getString(R.string.source);
							view.setTitle(title.concat(" ").concat(source.getName()));
						}
						view.showSource(source);
					}
				});
			}
		} else {
			if(editView != null)
				editView.setTitle(R.string.new_source_title);
		}
	}

	void loadSource(final String uuid) {
		repository.find(uuid).subscribe(new Subscriber<Source>("SourcePresenter.loadSource") {
			@Override
			public void onNext(Source source) {
				SourcePresenter.this.source = source;
				if(source == null)
					throw new SourceNotFoundException(uuid);
			}
		});
	}

	void save() {
		if(editView == null)
			throw new InvalidMethodCallException("save", getClass().toString(), "View should be a Edit View");
		if(source == null)
			source = new Source();
		source = editView.fillSource(source);
		OperationResult result = repository.save(source);

		if(result.isValid()) {
			editView.finishView();
		} else {
			for (ValidationError validationError : result.getErrors())
				editView.showError(validationError);
		}
	}

	String getUuid() {
		return source != null ? source.getUuid() : null;
	}
}
