package br.com.jonathanzanella.myexpenses.bill;

/**
 * Created by jzanella on 8/27/16.
 */

class BillNotFoundException extends RuntimeException {
	BillNotFoundException(String uuid) {
		super("Can't find bill with uuid=" + uuid);
	}
}
