package br.com.jonathanzanella.myexpenses.transaction

import android.graphics.Typeface
import android.support.annotation.ColorRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.bill.Bill
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper
import br.com.jonathanzanella.myexpenses.helpers.TransactionsHelper
import br.com.jonathanzanella.myexpenses.receipt.Receipt
import kotlinx.android.synthetic.main.row_transaction.view.*
import java.util.*

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    private val transactions = ArrayList<Transaction>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.value.setOnClickListener { onValue() }
        }

        fun setData(transaction: Transaction) {
            itemView.date.text = Transaction.SIMPLE_DATE_FORMAT.format(transaction.date.toDate())
            itemView.name.text = transaction.name
            itemView.value.text = CurrencyHelper.format(transaction.amount)
            itemView.value.setTypeface(null, Typeface.NORMAL)
            if (transaction is Receipt) {
                itemView.value.setTextColor(getColor(R.color.receipt))
                if (!transaction.credited())
                    itemView.value.setTypeface(null, Typeface.BOLD)
            } else if (transaction is Expense || transaction is Bill) {
                itemView.value.setTextColor(getColor(R.color.expense))
                if (!transaction.debited())
                    itemView.value.setTypeface(null, Typeface.BOLD)
            }
        }

        fun onValue() {
            val adapterPosition = adapterPosition
            val transaction = transactions[adapterPosition]
            TransactionsHelper.showConfirmTransactionDialog(transaction, itemView.date.context) { notifyItemChanged(adapterPosition) }
        }

        private fun getColor(@ColorRes color: Int): Int {
            return ResourcesCompat.getColor(itemView.context.resources, color, null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_transaction, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(transactions[position])
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun addTransactions(transactions: List<Transaction>) {
        this.transactions.addAll(transactions)
        Collections.sort(this.transactions, Comparator<Transaction> { lhs, rhs ->
            if (lhs.date.isAfter(rhs.date))
                return@Comparator 1
            -1
        })
    }

    fun getTransactions(): List<Transaction> {
        return transactions
    }
}
