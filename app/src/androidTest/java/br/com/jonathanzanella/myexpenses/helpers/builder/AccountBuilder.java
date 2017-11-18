package br.com.jonathanzanella.myexpenses.helpers.builder;

import br.com.jonathanzanella.myexpenses.account.Account;

import static org.apache.commons.lang3.RandomStringUtils.*;

public class AccountBuilder {
	private String name = "accountTest";
	private String uuid = random(10);
	private boolean accountToPayCreditCard = false;
	private boolean accountToPayBills = false;
	private boolean showInResume = true;

	public AccountBuilder name(String name) {
		this.name = name;
		return this;
	}
	public AccountBuilder uuid(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public AccountBuilder accountToPayCreditCard(boolean accountToPayCreditCard) {
		this.accountToPayCreditCard = accountToPayCreditCard;
		return this;
	}

	public AccountBuilder accountToPayBills(boolean accountToPayBills) {
		this.accountToPayBills = accountToPayBills;
		return this;
	}

	public AccountBuilder showInResume(boolean showInResume) {
		this.showInResume = showInResume;
		return this;
	}

	public Account build() {
		Account account = new Account();
		account.setName(name);
		account.setUuid(uuid);
		account.setAccountToPayCreditCard(accountToPayCreditCard);
		account.setAccountToPayBills(accountToPayBills);
		account.setShowInResume(showInResume);
		return account;
	}
}
