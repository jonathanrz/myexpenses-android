package br.com.jonathanzanella.myexpenses.unit.helper.builder;

import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountDataSource;
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.card.CardType;

public class CardBuilder {
	private Account account;
	private String name = "cardTest";
	private CardType type = CardType.CREDIT;

	public CardBuilder account(Account account) {
		this.account = account;
		return this;
	}

	public CardBuilder name(String name) {
		this.name = name;
		return this;
	}

	public CardBuilder type(CardType type) {
		this.type = type;
		return this;
	}

	public Card build(AccountDataSource accountDataSource) {
		if(account == null)
			throw new NullPointerException("Account can't be empty");
		Card card = new Card(accountDataSource);
		card.setName(name);
		card.setType(type);
		card.setAccount(account);
		return card;
	}
}
