package br.com.jonathanzanella.myexpenses.account;

import android.support.annotation.Nullable;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException;
import br.com.jonathanzanella.myexpenses.helpers.CountingIdlingResource;
import br.com.jonathanzanella.myexpenses.helpers.Subscriber;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jzanella on 8/27/16.
 */

class AccountPresenter {
	private AccountContract.View view;
	@Nullable
	private AccountContract.EditView editView;
	private AccountRepository repository;
	private Account account;
	private CountingIdlingResource idlingResource;

	AccountPresenter(AccountRepository repository, CountingIdlingResource idlingResource) {
		this.repository = repository;
		this.idlingResource = idlingResource;
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

	void viewUpdated(boolean invalidateCache) {
		if (account != null) {
			if(invalidateCache)
				loadAccount(account.getUuid());
		} else {
			if(editView != null)
				editView.setTitle(R.string.new_account_title);
		}
	}

	private void updateView() {
		if(editView != null) {
			editView.setTitle(R.string.edit_account_title);
		} else {
			String title = view.getContext().getString(R.string.account);
			view.setTitle(title.concat(" ").concat(account.getName()));
		}
		view.showAccount(account);
	}

	void loadAccount(String uuid) {
		repository.find(uuid)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Account>("AccountPresenter.loadAccount") {
					@Override
					public void onNext(Account account) {
						AccountPresenter.this.account = account;
						if(account != null)
							updateView();
						idlingResource.decrement();
					}
				});
	}

	void save() {
		if(editView == null)
			throw new InvalidMethodCallException("save", getClass().toString(), "View should be a Edit View");
		if(account == null)
			account = new Account();
		account = editView.fillAccount(account);
		OperationResult result = repository.save(account);

		if(result.isValid()) {
			editView.finishView();
		} else {
			for (ValidationError validationError : result.getErrors())
				editView.showError(validationError);
		}
	}

	String getUuid() {
		return account != null ? account.getUuid() : null;
	}
}
