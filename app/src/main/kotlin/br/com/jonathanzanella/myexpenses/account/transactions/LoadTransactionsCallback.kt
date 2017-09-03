package br.com.jonathanzanella.myexpenses.account.transactions

interface LoadTransactionsCallback {
    fun onTransactionsLoaded(balance: Int)
}
