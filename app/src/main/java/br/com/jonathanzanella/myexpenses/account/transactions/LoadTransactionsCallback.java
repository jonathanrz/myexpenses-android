package br.com.jonathanzanella.myexpenses.account.transactions;

interface LoadTransactionsCallback {
	void onTransactionsLoaded(int balance);
}