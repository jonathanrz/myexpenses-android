package br.com.jonathanzanella.myexpenses.receipt

internal class ReceiptNotFoundException(uuid: String) : RuntimeException("Can't find receipt with uuid=" + uuid)
