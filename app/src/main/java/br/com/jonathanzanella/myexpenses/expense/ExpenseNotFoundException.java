package br.com.jonathanzanella.myexpenses.expense;

class ExpenseNotFoundException extends RuntimeException {
	ExpenseNotFoundException(String uuid) {
		super("Can't find expense with uuid=" + uuid);
	}
}
