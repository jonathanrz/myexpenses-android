package br.com.jonathanzanella.myexpenses.expense

import android.content.Intent
import android.os.AsyncTask
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.bill.Bill
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.helpers.AdapterColorHelper
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import kotlinx.android.synthetic.main.row_expense.view.*
import org.joda.time.DateTime

internal open class ExpenseAdapter : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {
    private val expenseRepository = ExpenseRepository(RepositoryImpl<Expense>(MyApplication.getContext()))
    private val presenter: ExpenseAdapterPresenter
    private var expenses: List<Expense> = ArrayList()
    private var date: DateTime? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val adapterColorHelper: AdapterColorHelper

        init {
            val oddColor = ResourcesCompat.getColor(itemView.context.resources, R.color.color_list_odd, null)
            val evenColor = ResourcesCompat.getColor(itemView.context.resources, R.color.color_list_even, null)
            adapterColorHelper = AdapterColorHelper(oddColor, evenColor)

            itemView.setOnClickListener(this)
        }

        @UiThread
        fun setData(expense: Expense) {
            itemView.tag = expense.uuid
            itemView.setBackgroundColor(adapterColorHelper.getColorForGridWithTwoColumns(adapterPosition))
            itemView.name.text = expense.name
            itemView.date.text = Transaction.SIMPLE_DATE_FORMAT.format(expense.getDate().toDate())
            itemView.value.text = CurrencyHelper.format(expense.value)
            object : AsyncTask<Void, Void, Chargeable>() {

                override fun doInBackground(vararg voids: Void): Chargeable? {
                    return expense.chargeableFromCache
                }

                override fun onPostExecute(c: Chargeable?) {
                    super.onPostExecute(c)
                    itemView.chargeable.text = c?.name
                }
            }.execute()

            itemView.chargeNextMonth.visibility = if (expense.isChargedNextMonth) View.VISIBLE else View.INVISIBLE
            object : AsyncTask<Void, Void, Bill>() {

                override fun doInBackground(vararg voids: Void): Bill? {
                    return expense.bill
                }

                override fun onPostExecute(bill: Bill?) {
                    super.onPostExecute(bill)
                    if (bill == null) {
                        itemView.billStt.visibility = View.INVISIBLE
                        itemView.bill.visibility = View.INVISIBLE
                    } else {
                        itemView.billStt.visibility = View.VISIBLE
                        itemView.bill.visibility = View.VISIBLE
                        itemView.bill.text = bill.name
                    }
                }
            }.execute()
        }

        override fun onClick(v: View) {
            val expense = getExpense(adapterPosition)
            val i = Intent(itemView.context, ShowExpenseActivity::class.java)
            i.putExtra(ShowExpenseActivity.KEY_EXPENSE_UUID, expense.uuid)
            itemView.context.startActivity(i)
        }
    }

    init {
        presenter = ExpenseAdapterPresenter(expenseRepository)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_expense, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(getExpense(position))
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    @WorkerThread
    fun loadData(date: DateTime) {
        expenses = expenseRepository.monthly(date) as ArrayList<Expense>
        expenses = presenter.getExpenses(true, date)
        this.date = date
    }

    private fun getExpense(position: Int): Expense {
        return expenses[position]
    }

    fun filter(filter: String) {
        presenter.filter(filter)
        expenses = presenter.getExpenses(false, date!!)
    }
}