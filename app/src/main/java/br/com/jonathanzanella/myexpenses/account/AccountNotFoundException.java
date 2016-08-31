package br.com.jonathanzanella.myexpenses.account;

/**
 * Created by jzanella on 8/27/16.
 */

class AccountNotFoundException extends RuntimeException {
	AccountNotFoundException(String uuid) {
		super("Can't find account with uuid=" + uuid);
	}
}
