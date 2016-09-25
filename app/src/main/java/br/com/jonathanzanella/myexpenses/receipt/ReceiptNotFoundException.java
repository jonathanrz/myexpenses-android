package br.com.jonathanzanella.myexpenses.receipt;

/**
 * Created by jzanella on 8/27/16.
 */

class ReceiptNotFoundException extends RuntimeException {
	ReceiptNotFoundException(String uuid) {
		super("Can't find receipt with uuid=" + uuid);
	}
}
