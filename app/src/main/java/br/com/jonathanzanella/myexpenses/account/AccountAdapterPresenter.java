package br.com.jonathanzanella.myexpenses.account;

import java.util.Collections;
import java.util.List;

/**
 * Created by jzanella on 8/27/16.
 */

class AccountAdapterPresenter {
	private AccountRepository repository;
	private AccountAdapter adapter;

	private List<Account> accounts;

	AccountAdapterPresenter(AccountAdapter adapter, AccountRepository repository) {
		this.repository = repository;
		this.adapter = adapter;
		loadAccounts();
	}

	private void loadAccounts() {
		accounts = repository.userAccounts();
	}

	List<Account> getAccounts(boolean invalidateCache) {
		if(invalidateCache)
			loadAccounts();
		return Collections.unmodifiableList(accounts);
	}

	void addAccount(Account source) {
		accounts.add(source);
		adapter.notifyItemInserted(accounts.size() - 1);
	}
}
