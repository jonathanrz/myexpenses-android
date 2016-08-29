package br.com.jonathanzanella.myexpenses.card;

/**
 * Created by jzanella on 8/27/16.
 */

class CardNotFoundException extends RuntimeException {
	CardNotFoundException(String uuid) {
		super("Can't find source with uuid=" + uuid);
	}
}
