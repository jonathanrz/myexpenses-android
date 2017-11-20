package br.com.jonathanzanella.myexpenses.unit.helper.builder;

import br.com.jonathanzanella.myexpenses.account.Account;

public class AccountBuilder {
	private String name = "accountTest";
	private int balance = 0;

	public AccountBuilder name(String name) {
		this.name = name;
		return this;
	}

	public AccountBuilder balance(int balance) {
		this.balance = balance;
		return this;
	}

	public Account build() {
		Account account = new Account();
		account.setName(name);
		account.setBalance(balance);
		return account;
	}
}
