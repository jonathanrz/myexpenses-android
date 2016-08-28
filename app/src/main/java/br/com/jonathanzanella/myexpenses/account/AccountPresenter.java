package br.com.jonathanzanella.myexpenses.account;

import android.support.annotation.Nullable;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

/**
 * Created by jzanella on 8/27/16.
 */

class AccountPresenter {
	private AccountContract.View view;
	@Nullable
	private AccountContract.EditView editView;
	private AccountRepository repository;
	private Account account;

	AccountPresenter(AccountContract.View view, AccountRepository repository) {
		this.view = view;
		if(view instanceof AccountContract.EditView)
			editView = (AccountContract.EditView) view;
		this.repository = repository;
	}

	void viewUpdated(boolean invalidateCache) {
		if (account != null) {
			if(invalidateCache)
				account = repository.find(account.getUuid());
			if(editView != null)
				editView.setTitle(R.string.edit_account_title);
			view.showAccount(account);
		} else {
			if(editView != null)
				editView.setTitle(R.string.new_account_title);
		}
	}

	void loadAccount(String uuid) {
		account = repository.find(uuid);
		if(account == null)
			throw new AccountNotFoundException(uuid);
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
