package br.com.jonathanzanella.myexpenses.source;

/**
 * Created by jzanella on 8/27/16.
 */

class SourceNotFoundException extends RuntimeException {
	SourceNotFoundException(String uuid) {
		super("Can't find source with uuid=" + uuid);
	}
}
