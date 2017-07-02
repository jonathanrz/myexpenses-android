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

    fun getExpenses(invalidateCache: Boolean, date: DateTime): List<Expense> {
        if (invalidateCache)
            loadExpenses(date)
        if (receiptsFiltered == null)
            return ArrayList()
        return Collections.unmodifiableList(receiptsFiltered!!)
    }

    fun filter(filter: String?) {
        if (filter == null || filter.compareTo("") == 0) {
            receiptsFiltered = receipts
            return
        }

        receiptsFiltered = ArrayList<Expense>()
        for (bill in receipts!!) {
            if (StringUtils.containsIgnoreCase(bill.name, filter))
                receiptsFiltered!!.add(bill)
        }
    }
}
