package br.com.jonathanzanella.myexpenses.card;

import java.util.Collections;
import java.util.List;

/**
 * Created by jzanella on 8/27/16.
 */

class CardAdapterPresenter {
	private CardRepository repository;
	private CardAdapter adapter;

	private List<Card> cards;

	CardAdapterPresenter(CardAdapter adapter, CardRepository repository) {
		this.repository = repository;
		this.adapter = adapter;
		loadCards();
	}

	private void loadCards() {
		cards = repository.userCards();
	}

	List<Card> getCards(boolean invalidateCache) {
		if(invalidateCache)
			loadCards();
		return Collections.unmodifiableList(cards);
	}

	void addCard(Card source) {
		cards.add(source);
		adapter.notifyItemInserted(cards.size() - 1);
	}
}
