package br.com.jonathanzanella.myexpenses.helpers.builder;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.source.Source;

public class ReceiptBuilder {
	private String name = "receiptTest";
	private Source source = new SourceBuilder().build();
	private Account account = new AccountBuilder().build();
	private DateTime date = DateTime.now();
	private int income = 100;
	private boolean removed = false;

	public ReceiptBuilder name(String name) {
		this.name = name;
		return this;
	}

	public ReceiptBuilder source(Source source) {
		this.source = source;
		return this;
	}

	public ReceiptBuilder account(Account account) {
		this.account = account;
		return this;
	}

	public ReceiptBuilder date(DateTime date) {
		this.date = date;
		return this;
	}

	public ReceiptBuilder income(int income) {
		this.income = income;
		return this;
	}

	public ReceiptBuilder removed(boolean b) {
		this.removed = b;
		return this;
	}

	public Receipt build() {
		Receipt receipt = new Receipt();
		receipt.setName(name);
		receipt.setSource(source);
		receipt.setAccount(account);
		receipt.setDate(date);
		receipt.setIncome(income);
		receipt.setRemoved(removed);
		return receipt;
	}
}