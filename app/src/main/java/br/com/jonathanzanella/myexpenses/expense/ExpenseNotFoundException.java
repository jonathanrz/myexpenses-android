package br.com.jonathanzanella.myexpenses.expense;

/**
 * Created by jzanella on 8/27/16.
 */

class ExpenseNotFoundException extends RuntimeException {
	ExpenseNotFoundException(String uuid) {
		super("Can't find expense with uuid=" + uuid);
	}
}
