package br.com.jonathanzanella.myexpenses.account;

import android.os.AsyncTask;

import java.util.List;

class AccountAdapterPresenter {
	private final AccountRepository repository;
	private final AccountAdapter adapter;

	private List<Account> accounts;

	AccountAdapterPresenter(AccountAdapter adapter, AccountRepository repository, final AccountAdapter.Format format) {
		this.repository = repository;
		this.adapter = adapter;
	}

	final void loadAccountsAsync(final AccountAdapter.Format format) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... voids) {
				if(format == AccountAdapter.Format.RESUME) {
					accounts = repository.forResumeScreen();
				} else {
					accounts = repository.all();
				}
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
