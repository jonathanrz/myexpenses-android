package br.com.jonathanzanella.myexpenses.account;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

class AccountPresenter {
	private final AccountRepository repository;

	private AccountContract.View view;
	@Nullable
	private AccountContract.EditView editView;
	private Account account;

	AccountPresenter(AccountRepository repository) {
		this.repository = repository;
	}

	void attachView(AccountContract.View view) {
		this.view = view;
	}

	void attachView(AccountContract.EditView view) {
		this.view = view;
		this.editView = view;
	}

	void detachView() {
		this.view = null;
		this.editView = null;
	}

	@UiThread
	void viewUpdated(boolean invalidateCache) {
		if (account == null) {
			if(editView != null)
				editView.setTitle(R.string.new_account_title);
		} else {
			if(invalidateCache)
				loadAccount(account.getUuid());
		}
	}

	@UiThread
	private void updateView() {
		if(editView != null) {
			editView.setTitle(R.string.edit_account_title);
		} else {
			String title = view.getContext().getString(R.string.account);
			view.setTitle(title.concat(" ").concat(account.getName()));
		}
		view.showAccount(account);
	}

	@UiThread
	void reloadAccount() {
		loadAccount(account.getUuid());
	}

	@UiThread
	void loadAccount(final String uuid) {
		new AsyncTask<Void, Void, Account>() {

			@Override
			protected Account doInBackground(Void... voids) {
				account = repository.find(uuid);
				return account;
			}

			@Override
			protected void onPostExecute(Account account) {
				super.onPostExecute(account);
				if(account != null)
					updateView();
			}
		}.execute();
	}

	@UiThread
	void save() {
		if(editView == null)
			throw new InvalidMethodCallException("save", getClass().toString(), "View should be a Edit View");

		if(account == null)
			account = new Account();

		account = editView.fillAccount(account);
		new AsyncTask<Void, Void, ValidationResult>() {

			@Override
			protected ValidationResult doInBackground(Void... voids) {
				return repository.save(account);
			}

			@Override
			protected void onPostExecute(ValidationResult result) {
				super.onPostExecute(result);
				if(result.isValid()) {
					editView.finishView();
				} else {
					for (ValidationError validationError : result.getErrors())
						editView.showError(validationError);
				}
			}
		}.execute();
	}

	String getUuid() {
		return account != null ? account.getUuid() : null;
	}
}
