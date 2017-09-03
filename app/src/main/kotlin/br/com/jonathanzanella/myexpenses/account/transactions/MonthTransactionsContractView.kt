package br.com.jonathanzanella.myexpenses.account.transactions

internal interface MonthTransactionsContractView {
    fun onBalanceUpdated(balance: Int)
}
