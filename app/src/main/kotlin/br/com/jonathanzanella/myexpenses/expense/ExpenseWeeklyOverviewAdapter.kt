package br.com.jonathanzanella.myexpenses.expense

import android.content.Intent
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.card.CreditCardInvoiceActivity
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.singleRowCell
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.collections.ArrayList

class ExpenseWeeklyOverviewAdapter : RecyclerView.Adapter<ExpenseWeeklyOverviewAdapter.ViewHolder>() {
    private var expenses: List<Expense> = ArrayList()
    var totalValue: Int = 0
        private set

    inner class ViewHolder internal constructor(itemView: View, val ui : ExpenseWeeklyOverviewUI) :
            RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        @UiThread
        fun setData(expense: Expense) {
            ui.name.text = expense.name
            synchronized(this) {
                ui.date.text = SIMPLE_DATE_FORMAT.format(expense.getDate().toDate())
            }
            ui.income.text = expense.value.toCurrencyFormatted()

            doAsync {
                val chargeable = expense.chargeableFromCache

                uiThread { ui.source.text = chargeable?.name }
            }
        }

        override fun onClick(v: View) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ui = ExpenseWeeklyOverviewUI()
        return ViewHolder(ui.createView(AnkoContext.create(parent.context, parent)), ui)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(expenses[position])
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    fun setExpenses(expenses: List<Expense>) {
        this.expenses = expenses
        totalValue = expenses.sumBy { it.value }
    }

    private fun getExpense(position: Int): Expense? {
        return expenses[position]
    }

    companion object {
        private val SIMPLE_DATE_FORMAT = SimpleDateFormat("dd", Locale.getDefault())
    }
}

internal class ExpenseWeeklyOverviewUI : AnkoComponent<ViewGroup> {
    lateinit var date: TextView
    lateinit var source: TextView
    lateinit var name: TextView
    lateinit var income: TextView

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        verticalLayout {
            singleRowCell {
                date = textView { id = R.id.row_weekly_overview_expense_date }
                        .lparams { marginEnd = resources.getDimensionPixelSize(R.dimen.min_spacing) }
                source = textView { id = R.id.row_weekly_overview_expense_source }
                        .lparams(width = resources.getDimensionPixelSize(R.dimen.expense_weekly_overview_source_width)) {
                            marginEnd = resources.getDimensionPixelSize(R.dimen.min_spacing)
                        }
                name = textView { id = R.id.row_weekly_overview_expense_name }
                        .lparams(width = 0, weight = 1f) {
                            marginEnd = resources.getDimensionPixelSize(R.dimen.min_spacing)
                        }
                income = textView { id = R.id.row_weekly_overview_expense_income }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
