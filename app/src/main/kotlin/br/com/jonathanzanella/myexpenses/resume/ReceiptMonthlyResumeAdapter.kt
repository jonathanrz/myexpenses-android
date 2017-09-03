package br.com.jonathanzanella.myexpenses.resume

import android.content.Intent
import android.graphics.Typeface
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.helpers.TransactionsHelper
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.receipt.Receipt
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository
import br.com.jonathanzanella.myexpenses.receipt.ShowReceiptActivity
import kotlinx.android.synthetic.main.row_monthly_resume_receipt.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

internal class ReceiptMonthlyResumeAdapter(private val receiptRepository: ReceiptRepository) :
        RecyclerView.Adapter<ReceiptMonthlyResumeAdapter.ViewHolder>() {
    private var receipts: List<Receipt> = ArrayList()
    var totalValue: Int = 0
        private set
    private var totalUnreceivedValue: Int = 0

    private enum class ViewType {
        TYPE_NORMAL,
        TYPE_TOTAL_TO_PAY,
        TYPE_TOTAL
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
            itemView.income.setOnClickListener { onIncome() }
        }

        @UiThread
        fun setData(receipt: Receipt) {
            itemView.tag = receipt.uuid
            if (itemView.name != null)
                itemView.name.text = receipt.name
            if (itemView.date != null) {
                synchronized(this) {
                    itemView.date.text = SIMPLE_DATE_FORMAT.format(receipt.getDate().toDate())
                }
            }
            itemView.income.text = receipt.incomeFormatted
            itemView.income.setTypeface(null, Typeface.NORMAL)
            if (!receipt.credited)
                itemView.income!!.setTypeface(null, Typeface.BOLD)

            doAsync {
                val s = receipt.source

                uiThread { s?.let { itemView.source?.text = it.name } }
            }
        }

        fun setTotal(totalValue: Int) {
            itemView.income.text = totalValue.toCurrencyFormatted()
        }

        private fun onIncome() {
            if (itemViewType != ViewType.TYPE_NORMAL.ordinal)
                return

            val receipt = getReceipt(adapterPosition)
            TransactionsHelper.showConfirmTransactionDialog(receipt!!, itemView.income.context, object: TransactionsHelper.DialogCallback {
                override fun onPositiveButton() {
                    updateTotalValue()
                    notifyDataSetChanged()
                }
            })
        }

        override fun onClick(v: View) {
            if (itemViewType != ViewType.TYPE_NORMAL.ordinal)
                return

            val receipt = getReceipt(adapterPosition)
            if (receipt != null) {
                val i = Intent(itemView.context, ShowReceiptActivity::class.java)
                i.putExtra(ShowReceiptActivity.KEY_RECEIPT_UUID, receipt.uuid)
                itemView.context.startActivity(i)
            }
        }
    }

    override fun getItemViewType(position: Int) =
        when {
            isTotalView(position) -> ViewType.TYPE_TOTAL.ordinal
            isTotalToPayView(position) -> ViewType.TYPE_TOTAL_TO_PAY.ordinal
            else -> ViewType.TYPE_NORMAL.ordinal
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = when (viewType) {
            ViewType.TYPE_TOTAL.ordinal ->
                LayoutInflater.from(parent.context).inflate(R.layout.row_monthly_resume_receipt_total, parent, false)
            ViewType.TYPE_TOTAL_TO_PAY.ordinal ->
                LayoutInflater.from(parent.context).inflate(R.layout.row_monthly_resume_receipt_total_to_receive, parent, false)
            else ->
                LayoutInflater.from(parent.context).inflate(R.layout.row_monthly_resume_receipt, parent, false)
        }

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when {
            isTotalView(position) -> holder.setTotal(totalValue)
            isTotalToPayView(position) -> holder.setTotal(totalUnreceivedValue)
            else -> holder.setData(receipts[position])
        }
    }

    private fun isTotalView(position: Int): Boolean {
        return position == receipts.size + 1
    }

    private fun isTotalToPayView(position: Int): Boolean {
        return position == receipts.size
    }

    override fun getItemCount(): Int {
        return receipts.size + 2
    }

    fun loadDataAsync(month: DateTime, runnable: Runnable?) {
        doAsync {
            receipts = receiptRepository.resume(month)
            updateTotalValue()

            uiThread {
                notifyDataSetChanged()
                runnable?.run()
            }
        }
    }

    private fun getReceipt(position: Int): Receipt? {
        return receipts[position]
    }

    private fun updateTotalValue() {
        totalValue = 0
        totalUnreceivedValue = 0
        for (receipt in receipts) {
            totalValue += receipt.income
            if (!receipt.credited)
                totalUnreceivedValue += receipt.income
        }
    }

    companion object {
        val SIMPLE_DATE_FORMAT = SimpleDateFormat("dd/MM", Locale.getDefault())
    }
}
