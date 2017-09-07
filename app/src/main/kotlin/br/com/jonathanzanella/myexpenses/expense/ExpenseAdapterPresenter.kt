package br.com.jonathanzanella.myexpenses.expense

import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.ArrayList

class ExpenseAdapterPresenter(private val dataSource: ExpenseDataSource) {
    private var expenses: List<Expense> = ArrayList()
    private var expensesFiltered: MutableList<Expense> = ArrayList()

    private fun loadExpenses(date: DateTime) {
        expenses = dataSource.monthly(date)
        expensesFiltered = expenses as MutableList<Expense>
    }

    fun getExpenses(invalidateCache: Boolean, date: DateTime?): List<Expense> {
        if (invalidateCache)
            loadExpenses(date!!)
        return Collections.unmodifiableList(expensesFiltered)
    }

    fun filter(filter: String) {
        if (filter.compareTo("") == 0) {
            expensesFiltered = expenses as MutableList<Expense>
            return
        }

        expensesFiltered = ArrayList()
        expenses.filter { StringUtils.containsIgnoreCase(it.name, filter) }
                .forEach { expensesFiltered.add(it) }
    }
}
