package br.com.jonathanzanella.myexpenses.source;

import android.support.annotation.Nullable;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException;
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

	SourcePresenter(SourceContract.View view, SourceRepository repository) {
		this.view = view;
		if(view instanceof SourceContract.EditView)
			editView = (SourceContract.EditView) view;
		this.repository = repository;
	}

	void viewUpdated(boolean invalidateCache) {
		if (source != null) {
			if(invalidateCache)
				source = repository.find(source.getUuid());
			if(editView != null)
				editView.setTitle(R.string.edit_source_title);
			view.showSource(source);
		} else {
			if(editView != null)
				editView.setTitle(R.string.new_source_title);
		}
	}

	void loadSource(String uuid) {
		source = repository.find(uuid);
		if(source == null)
			throw new SourceNotFoundException(uuid);
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
