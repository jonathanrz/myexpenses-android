package br.com.jonathanzanella.myexpenses.source;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

class SourcePresenter {
	private final SourceRepository repository;
	private SourceContract.View view;
	@Nullable
	private SourceContract.EditView editView;
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

	void viewUpdated() {
		if (source != null) {
			if (editView != null) {
				editView.setTitle(R.string.edit_source_title);
			} else {
				String title = view.getContext().getString(R.string.source);
				view.setTitle(title.concat(" ").concat(source.getName()));
			}
			view.showSource(source);
		} else {
			if(editView != null)
				editView.setTitle(R.string.new_source_title);
		}
	}

	@UiThread
	void loadSource(final String uuid) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... voids) {
				source = repository.find(uuid);
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				viewUpdated();
			}
		}.execute();
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
