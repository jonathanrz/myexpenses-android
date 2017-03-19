package br.com.jonathanzanella.myexpenses.account;

import android.os.AsyncTask;

import java.util.List;

class AccountAdapterPresenter {
	private final AccountRepository repository;
	private final AccountAdapter adapter;

	private List<Account> accounts;

	AccountAdapterPresenter(AccountAdapter adapter, AccountRepository repository) {
		this.repository = repository;
		this.adapter = adapter;
		loadAccountsAsync();
	}

	final void loadAccountsAsync() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... voids) {
				accounts = repository.userAccounts();
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				adapter.notifyDataSetChanged();
			}
		}.execute();
	}

	Account getAccount(int position) {
		return accounts.get(position);
	}

	int getAccountsSize() {
		return accounts == null ? 0 : accounts.size();
	}
}
