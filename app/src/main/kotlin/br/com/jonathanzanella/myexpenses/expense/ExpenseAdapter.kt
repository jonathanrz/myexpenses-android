package br.com.jonathanzanella.myexpenses.expense

import android.content.Intent
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.helpers.AdapterColorHelper
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import kotlinx.android.synthetic.main.row_expense.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime
import javax.inject.Inject

open class ExpenseAdapter : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {
    @Inject
    lateinit var expenseDataSource: ExpenseDataSource
    private val presenter: ExpenseAdapterPresenter
    private var expenses: List<Expense> = ArrayList()
    private var date: DateTime? = null

    init {
        App.getAppComponent().inject(this)
    }

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
            itemView.apply {
                tag = expense.uuid
                setBackgroundColor(adapterColorHelper.getColorForGridWithTwoColumns(adapterPosition))
                name.text = expense.name
                date.text = Transaction.SIMPLE_DATE_FORMAT.format(expense.getDate().toDate())
                value.text = expense.value.toCurrencyFormatted()
                chargeNextMonth.visibility = if (expense.chargedNextMonth) View.VISIBLE else View.INVISIBLE
            }

            doAsync {
                val chargeable = expense.chargeableFromCache

                uiThread { itemView.chargeable.text = chargeable?.name }
            }

            doAsync {
                val bill = expense.bill

                uiThread {
                    if (bill == null) {
                        itemView.billStt.visibility = View.INVISIBLE
                        itemView.bill.visibility = View.INVISIBLE
                    } else {
                        itemView.billStt.visibility = View.VISIBLE
                        itemView.bill.visibility = View.VISIBLE
                        itemView.bill.text = bill.name
                    }
                }
            }
        }

        override fun onClick(v: View) {
            val expense = getExpense(adapterPosition)
            val i = Intent(itemView.context, ShowExpenseActivity::class.java)
            i.putExtra(ShowExpenseActivity.KEY_EXPENSE_UUID, expense.uuid)
            itemView.context.startActivity(i)
        }
    }

    init {
        presenter = ExpenseAdapterPresenter(expenseDataSource)
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
        expenses = presenter.getExpenses(true, date)
        this.date = date
    }

    private fun getExpense(position: Int): Expense {
        return expenses[position]
    }

    fun filter(filter: String) {
        presenter.filter(filter)
        expenses = presenter.getExpenses(false, date)
    }
}
