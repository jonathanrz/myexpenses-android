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
import br.com.jonathanzanella.myexpenses.card.CreditCardInvoiceActivity
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ShowExpenseActivity
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper
import br.com.jonathanzanella.myexpenses.helpers.TransactionsHelper
import kotlinx.android.synthetic.main.row_monthly_resume_expense.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

internal class ExpenseMonthlyResumeAdapter : RecyclerView.Adapter<ExpenseMonthlyResumeAdapter.ViewHolder>() {
    private var expenses: List<Expense> = ArrayList()
    var totalValue: Int = 0
        private set
    private var totalUnpaidValue: Int = 0

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
        fun setData(expense: Expense) {
            itemView.tag = expense.uuid
            if (itemView.name != null)
                itemView.name.text = expense.name
            if (itemView.date != null) {
                synchronized(this) {
                    itemView.date.text = SIMPLE_DATE_FORMAT.format(expense.getDate().toDate())
                }
            }
            itemView.income.text = CurrencyHelper.format(expense.value)
            itemView.income.setTypeface(null, Typeface.NORMAL)
            if (!expense.isCharged)
                itemView.income!!.setTypeface(null, Typeface.BOLD)

            object : AsyncTask<Void, Void, Chargeable>() {
                override fun doInBackground(vararg voids: Void): Chargeable {
                    return expense.chargeableFromCache
                }

                override fun onPostExecute(chargeable: Chargeable?) {
                    super.onPostExecute(chargeable)
                    if (itemView.source != null && chargeable != null)
                        itemView.source.text = chargeable.name
                }
            }.execute()
        }

        fun setTotal(totalValue: Int) {
            itemView.income.text = CurrencyHelper.format(totalValue)
        }

        fun onIncome() {
            if (itemViewType != ViewType.TYPE_NORMAL.ordinal)
                return

            val expense = getExpense(adapterPosition)
            TransactionsHelper.showConfirmTransactionDialog(expense, itemView.income.context) {
                updateTotalValue()
                notifyDataSetChanged()
            }
        }

        override fun onClick(v: View) {
            if (itemViewType != ViewType.TYPE_NORMAL.ordinal)
                return

            val expense = getExpense(adapterPosition)
            if (expense != null) {
                val card = expense.creditCard
                if (card != null) {
                    val i = Intent(itemView.context, CreditCardInvoiceActivity::class.java)
                    i.putExtra(CreditCardInvoiceActivity.KEY_CREDIT_CARD_UUID, card.uuid)
                    i.putExtra(CreditCardInvoiceActivity.KEY_INIT_DATE, expense.getDate())
                    itemView.context.startActivity(i)
                } else {
                    val i = Intent(itemView.context, ShowExpenseActivity::class.java)
                    i.putExtra(ShowExpenseActivity.KEY_EXPENSE_UUID, expense.uuid)
                    itemView.context.startActivity(i)
                }
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
            v = LayoutInflater.from(parent.context).inflate(R.layout.row_monthly_resume_expense_total, parent, false)
        else if (viewType == ViewType.TYPE_TOTAL_TO_PAY.ordinal)
            v = LayoutInflater.from(parent.context).inflate(R.layout.row_monthly_resume_expense_total_to_pay, parent, false)
        else
            v = LayoutInflater.from(parent.context).inflate(R.layout.row_monthly_resume_expense, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (isTotalView(position))
            holder.setTotal(totalValue)
        else if (isTotalToPayView(position))
            holder.setTotal(totalUnpaidValue)
        else
            holder.setData(expenses[position])
    }

    private fun isTotalView(position: Int): Boolean {
        return position == expenses.size + 1
    }

    private fun isTotalToPayView(position: Int): Boolean {
        return position == expenses.size
    }

    override fun getItemCount(): Int {
        return expenses.size + 2
    }

    fun setExpenses(expenses: List<Expense>) {
        this.expenses = expenses
        updateTotalValue()
    }

    private fun updateTotalValue() {
        totalValue = 0
        totalUnpaidValue = 0

        for (expense in expenses) {
            totalValue += expense.value
            if (!expense.isCharged)
                totalUnpaidValue += expense.value
        }
    }

    private fun getExpense(position: Int): Expense? {
        return expenses[position]
    }

    companion object {
        val SIMPLE_DATE_FORMAT = SimpleDateFormat("dd/MM", Locale.getDefault())
    }
}
