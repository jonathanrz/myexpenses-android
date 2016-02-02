package br.com.jonathanzanella.myexpenses.adapter;

import br.com.jonathanzanella.myexpenses.model.Account;

/**
 * Created by jzanella on 2/1/16.
 */
public interface AccountAdapterCallback {
	void onAccountSelected(Account account);
}