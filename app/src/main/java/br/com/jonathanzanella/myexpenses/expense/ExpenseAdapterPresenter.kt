package br.com.jonathanzanella.myexpenses.expense

import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import java.util.*

class ExpenseAdapterPresenter(private val repository: ExpenseRepository) {
    private var receipts: MutableList<Expense>? = null
    private var receiptsFiltered: MutableList<Expense>? = null

    private fun loadExpenses(date: DateTime) {
        receipts = repository.monthly(date) as MutableList<Expense>
        receiptsFiltered = receipts
    }

    fun getExpenses(invalidateCache: Boolean, date: DateTime?): List<Expense> {
        if (invalidateCache)
            loadExpenses(date!!)
        return Collections.unmodifiableList(receiptsFiltered ?: ArrayList())
    }

    fun filter(filter: String) {
        if (filter.compareTo("") == 0) {
            receiptsFiltered = receipts
            return
        }

        receiptsFiltered = ArrayList()
        receipts!!
                .filter { StringUtils.containsIgnoreCase(it.name, filter) }
                .forEach { receiptsFiltered!!.add(it) }
    }
}
