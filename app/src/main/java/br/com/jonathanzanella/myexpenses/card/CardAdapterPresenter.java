package br.com.jonathanzanella.myexpenses.card;

import android.support.annotation.UiThread;

import java.util.Collections;
import java.util.List;

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

	@UiThread
	void addCard(Card source) {
		cards.add(source);
		adapter.notifyItemInserted(cards.size() - 1);
	}
}
