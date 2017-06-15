package br.com.jonathanzanella.myexpenses.account.transactions;

public interface LoadTransactionsCallback {
	void onTransactionsLoaded(int balance);
}