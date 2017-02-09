package br.com.jonathanzanella.myexpenses.receipt;

class ReceiptNotFoundException extends RuntimeException {
	ReceiptNotFoundException(String uuid) {
		super("Can't find receipt with uuid=" + uuid);
	}
}
