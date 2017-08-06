package br.com.jonathanzanella.myexpenses.resume

import android.content.Intent
import android.graphics.Typeface
import android.os.AsyncTask
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper
import br.com.jonathanzanella.myexpenses.helpers.TransactionsHelper
import br.com.jonathanzanella.myexpenses.receipt.Receipt
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository
import br.com.jonathanzanella.myexpenses.receipt.ShowReceiptActivity
import br.com.jonathanzanella.myexpenses.source.Source
import kotlinx.android.synthetic.main.row_monthly_resume_receipt.view.*
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

internal class ReceiptMonthlyResumeAdapter(private val receiptRepository: ReceiptRepository) : RecyclerView.Adapter<ReceiptMonthlyResumeAdapter.ViewHolder>() {
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
            if (!receipt.isCredited)
                itemView.income!!.setTypeface(null, Typeface.BOLD)

            object : AsyncTask<Void, Void, Source>() {

                override fun doInBackground(vararg voids: Void): Source? {
                    return receipt.source
                }

                override fun onPostExecute(s: Source?) {
                    super.onPostExecute(s)
                    if (s != null)
                        itemView.source?.text = s.name
                }
            }.execute()
        }

        fun setTotal(totalValue: Int) {
            itemView.income.text = CurrencyHelper.format(totalValue)
        }

        fun onIncome() {
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

    override fun getItemViewType(position: Int): Int {
        if (isTotalView(position)) {
            return ViewType.TYPE_TOTAL.ordinal
        } else if (isTotalToPayView(position)) {
            return ViewType.TYPE_TOTAL_TO_PAY.ordinal
        } else {
            return ViewType.TYPE_NORMAL.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View
        if (viewType == ViewType.TYPE_TOTAL.ordinal)
            v = LayoutInflater.from(parent.context).inflate(R.layout.row_monthly_resume_receipt_total, parent, false)
        else if (viewType == ViewType.TYPE_TOTAL_TO_PAY.ordinal)
            v = LayoutInflater.from(parent.context).inflate(R.layout.row_monthly_resume_receipt_total_to_receive, parent, false)
        else
            v = LayoutInflater.from(parent.context).inflate(R.layout.row_monthly_resume_receipt, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (isTotalView(position))
            holder.setTotal(totalValue)
        else if (isTotalToPayView(position))
            holder.setTotal(totalUnreceivedValue)
        else
            holder.setData(receipts[position])
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
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                receipts = receiptRepository.resume(month)
                updateTotalValue()
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                notifyDataSetChanged()
                runnable?.run()
            }
        }.execute()
    }

    private fun getReceipt(position: Int): Receipt? {
        return receipts[position]
    }

    private fun updateTotalValue() {
        totalValue = 0
        totalUnreceivedValue = 0
        for (receipt in receipts) {
            totalValue += receipt.income
            if (!receipt.isCredited)
                totalUnreceivedValue += receipt.income
        }
    }

    companion object {
        val SIMPLE_DATE_FORMAT = SimpleDateFormat("dd/MM", Locale.getDefault())
    }
}
