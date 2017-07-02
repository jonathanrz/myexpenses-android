package br.com.jonathanzanella.myexpenses.receipt

import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import java.util.*

internal class ReceiptAdapterPresenter(private val repository: ReceiptRepository) {

    private var receipts: MutableList<Receipt>? = null
    private var receiptsFiltered: MutableList<Receipt>? = null

    private fun loadReceipts(date: DateTime) {
        receipts = repository.monthly(date) as MutableList<Receipt>
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

        receiptsFiltered = ArrayList<Receipt>()
        for (bill in receipts!!) {
            if (StringUtils.containsIgnoreCase(bill.name, filter))
                receiptsFiltered!!.add(bill)
        }
    }
}
