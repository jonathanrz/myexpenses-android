package br.com.jonathanzanella.myexpenses.receipt

class ReceiptNotFoundException(uuid: String) : RuntimeException("Can't find receipt with uuid=" + uuid)
