package br.com.jonathanzanella.myexpenses.helpers.builder;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.source.Source;

/**
 * Created by jzanella on 8/28/16.
 */

public class ReceiptBuilder {
	private String name = "receiptTest";
	private Source source = new SourceBuilder().build();
	private Account account = new AccountBuilder().build();
	private DateTime date = DateTime.now();

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

	public Receipt build() {
		Receipt receipt = new Receipt();
		receipt.setName(name);
		receipt.setIncome(1);
		receipt.setSource(source);
		receipt.setAccount(account);
		receipt.setDate(date);
		return receipt;
	}
}