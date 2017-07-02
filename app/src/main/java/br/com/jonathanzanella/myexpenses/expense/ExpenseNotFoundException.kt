package br.com.jonathanzanella.myexpenses.expense

class ExpenseNotFoundException(uuid: String) : RuntimeException("Can't find expense with uuid=" + uuid)
