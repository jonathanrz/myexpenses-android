package br.com.jonathanzanella.myexpenses.expense

import android.graphics.Typeface
import android.support.annotation.WorkerThread
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.card.Card
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import org.jetbrains.anko.*
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CreditCardMonthlyAdapter : RecyclerView.Adapter<CreditCardMonthlyAdapter.ViewHolder>() {
    private var expenses: List<Expense> = ArrayList()
    @Inject
    lateinit var expenseDataSource: ExpenseDataSource
    private var totalValue: Int = 0

    private enum class ViewType {
        TYPE_NORMAL,
        TYPE_TOTAL
    }

    init {
        App.getAppComponent().inject(this)
    }

    class ViewHolder(itemView: View, val income: TextView, val name: TextView? = null, val date: TextView? = null, val source: TextView? = null) :
            RecyclerView.ViewHolder(itemView) {
        fun setData(expense: Expense) {
            if (name != null)
                name.text = expense.name
            if (date != null) {
                synchronized(this) {
                    date.text = SIMPLE_DATE_FORMAT.format(expense.getDate().toDate())
                }
            }
            income.text = expense.value.toCurrencyFormatted()
            if (source != null)
                source.visibility = View.GONE
        }

        fun setTotal(totalValue: Int) {
            income.text = totalValue.toCurrencyFormatted()
        }
    }

    override fun getItemViewType(position: Int) =
        when (position) {
            expenses.size -> ViewType.TYPE_TOTAL.ordinal
            else -> ViewType.TYPE_NORMAL.ordinal
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            ViewType.TYPE_TOTAL.ordinal -> {
                val ui = CreditCardMonthlyTotalViewUI()
                ViewHolder(ui.createView(AnkoContext.create(parent.context, parent)), ui.income)
            }
            else -> {
                val ui = CreditCardMonthlyViewUI()
                ViewHolder(ui.createView(AnkoContext.create(parent.context, parent)), ui.income, ui.name, ui.date, ui.source)
            }
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == expenses.size)
            holder.setTotal(totalValue)
        else
            holder.setData(expenses[position])
    }

    override fun getItemCount(): Int {
        return expenses.size + 1
    }

    @WorkerThread
    fun loadData(creditCard: Card, month: DateTime) {
        expenses = expenseDataSource.creditCardBills(creditCard, month)
        totalValue = expenses.sumBy { it.value }
    }

    companion object {
        private val SIMPLE_DATE_FORMAT = SimpleDateFormat("dd/MM", Locale.getDefault())
    }
}

private class CreditCardMonthlyViewUI: AnkoComponent<ViewGroup> {
    lateinit var date : TextView
    lateinit var source : TextView
    lateinit var name : TextView
    lateinit var income : TextView

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        linearLayout {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
            padding = resources.getDimensionPixelOffset(R.dimen.min_spacing)

            date = textView { id = R.id.row_credit_card_monthly_date }
                    .lparams { marginEnd = resources.getDimensionPixelSize(R.dimen.min_spacing) }
            source = textView { id = R.id.row_credit_card_monthly_source }
                    .lparams(width = resources.getDimensionPixelSize(R.dimen.resume_source_width)) {
                        marginEnd = resources.getDimensionPixelSize(R.dimen.min_spacing)
                    }
            name = textView { id = R.id.row_credit_card_monthly_name }
                    .lparams(width = 0, weight = 1f) {
                        marginEnd = resources.getDimensionPixelSize(R.dimen.min_spacing)
                    }
            income = textView { id = R.id.row_credit_card_monthly_income }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}

private class CreditCardMonthlyTotalViewUI: AnkoComponent<ViewGroup> {
    lateinit var income : TextView

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        linearLayout {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
            padding = resources.getDimensionPixelOffset(R.dimen.min_spacing)

            textView {
                text = resources.getString(R.string.total)
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }.lparams(width = 0, height = wrapContent, weight = 1f) {
                marginEnd = resources.getDimensionPixelSize(R.dimen.min_spacing)
            }
            income = textView {
                id = R.id.row_credit_card_monthly_income
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
