package br.com.jonathanzanella.myexpenses.bill

import org.apache.commons.lang3.StringUtils
import java.util.*

class BillAdapterPresenter(private val repository: BillRepository) {
    private var bills: MutableList<Bill>? = null
    private var billsFiltered: MutableList<Bill>? = null

    init {
        loadBills()
    }

    private fun loadBills() {
        bills = repository.all() as MutableList<Bill>
        billsFiltered = bills
    }

    fun getBill(position: Int): Bill {
        return billsFiltered!![position]
    }

    fun getBills(invalidateCache: Boolean): List<Bill> {
        if (invalidateCache)
            loadBills()
        return Collections.unmodifiableList(billsFiltered!!)
    }

    val billsCount: Int
        get() = billsFiltered!!.size

    fun filter(filter: String) {
        if (filter.compareTo("") == 0) {
            billsFiltered = bills
            return
        }

        billsFiltered = ArrayList<Bill>()
        bills!!
                .filter { StringUtils.containsIgnoreCase(it.name, filter) }
                .forEach { billsFiltered!!.add(it) }
    }
}
