package br.com.jonathanzanella.myexpenses.card;

class CardNotFoundException extends RuntimeException {
	CardNotFoundException(String uuid) {
		super("Can't find card with uuid=" + uuid);
	}
}