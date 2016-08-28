package br.com.jonathanzanella.myexpenses.card;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.account.ListAccountActivity;
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static android.app.Activity.RESULT_OK;

/**
 * Created by jzanella on 8/27/16.
 */

class CardPresenter {
	private static final int REQUEST_SELECT_ACCOUNT = 1006;

	private CardContract.View view;
	@Nullable
	private CardContract.EditView editView;
	private CardRepository repository;
	private AccountRepository accountRepository;
	private Card card;
	private Account account;

	CardPresenter(CardRepository repository, AccountRepository accountRepository) {
		this.repository = repository;
		this.accountRepository = accountRepository;
	}

	void attachView(CardContract.View view) {
		this.view = view;
	}

	void attachView(CardContract.EditView view) {
		this.view = view;
		this.editView = view;
	}

	void detachView() {
		this.view = null;
		this.editView = null;
	}

	void viewUpdated(boolean invalidateCache) {
		if (card != null) {
			if(invalidateCache)
				card = repository.find(card.getUuid());
			if(editView != null) {
				editView.setTitle(R.string.edit_account_title);
			} else {
				String title = view.getContext().getString(R.string.account);
				view.setTitle(title.concat(" ").concat(card.getName()));
			}
			view.showCard(card);
			if(account != null)
				editView.onAccountSelected(account);
		} else {
			if(editView != null)
				editView.setTitle(R.string.new_account_title);
		}
	}

	void loadCard(String uuid) {
		card = repository.find(uuid);
		if(card == null)
			throw new CardNotFoundException(uuid);
	}

	void save() {
		if(editView == null)
			throw new InvalidMethodCallException("save", getClass().toString(), "View should be a Edit View");
		if(card == null)
			card = new Card();
		card = editView.fillCard(card);
		if(account != null)
			card.setAccount(account);
		OperationResult result = repository.save(card);

		if(result.isValid()) {
			editView.finishView();
		} else {
			for (ValidationError validationError : result.getErrors())
				editView.showError(validationError);
		}
	}

	String getUuid() {
		return card != null ? card.getUuid() : null;
	}

	void showSelectAccountActivity(Activity act) {
		if(card == null)
			act.startActivityForResult(new Intent(act, ListAccountActivity.class), REQUEST_SELECT_ACCOUNT);
	}

	void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_SELECT_ACCOUNT: {
				if(resultCode == RESULT_OK) {
					account = accountRepository.find(data.getStringExtra(ListAccountActivity.KEY_ACCOUNT_SELECTED_UUID));
					if(account != null)
						editView.onAccountSelected(account);
				}
				break;
			}
		}
	}
}
