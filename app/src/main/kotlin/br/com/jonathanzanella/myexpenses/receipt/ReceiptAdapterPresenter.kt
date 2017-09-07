package br.com.jonathanzanella.myexpenses.receipt

import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import java.util.*

class ReceiptAdapterPresenter(private val dataSource: ReceiptDataSource) {

    private var receipts: MutableList<Receipt>? = null
    private var receiptsFiltered: MutableList<Receipt>? = null

    private fun loadReceipts(date: DateTime) {
        receipts = dataSource.monthly(date) as MutableList<Receipt>
        receiptsFiltered = receipts
    }

    fun getReceipts(invalidateCache: Boolean, date: DateTime?): List<Receipt> {
        if (invalidateCache)
            loadReceipts(date!!)
        return Collections.unmodifiableList(receiptsFiltered!!)
    }

    fun filter(filter: String?) {
        if (filter == null || filter.compareTo("") == 0) {
            receiptsFiltered = receipts
            return
        }

        receiptsFiltered = ArrayList()
        receipts!!
                .filter { StringUtils.containsIgnoreCase(it.name, filter) }
                .forEach { receiptsFiltered!!.add(it) }
    }
}
