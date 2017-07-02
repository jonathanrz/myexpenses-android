package br.com.jonathanzanella.myexpenses.expense

internal class ExpenseNotFoundException(uuid: String) : RuntimeException("Can't find expense with uuid=" + uuid)
