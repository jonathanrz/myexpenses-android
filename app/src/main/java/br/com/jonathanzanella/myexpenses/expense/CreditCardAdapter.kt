package br.com.jonathanzanella.myexpenses.expense

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.card.Card
import br.com.jonathanzanella.myexpenses.card.CardRepository
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper
import br.com.jonathanzanella.myexpenses.receipt.Receipt
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import org.jetbrains.anko.*
import org.joda.time.DateTime

internal class CreditCardAdapter(context: Context) : RecyclerView.Adapter<CreditCardAdapter.ViewHolder>() {
    private var expenses: List<Expense> = ArrayList()
    private val cardRepository: CardRepository
    private var totalValue: Int = 0

    private enum class ViewType {
        TYPE_NORMAL,
        TYPE_TOTAL
    }

    class ViewHolder(itemView: View, val value : TextView, val date : TextView? = null) : RecyclerView.ViewHolder(itemView) {
        fun setData(expense: Expense) {
            if (date != null)
                date.text = Receipt.SIMPLE_DATE_FORMAT.format(expense.date.toDate())
            value.text = CurrencyHelper.format(expense.value)
        }

        fun setTotal(totalValue: Int) {
            value.text = CurrencyHelper.format(totalValue)
        }
    }

    init {
        val expenseRepository = ExpenseRepository(RepositoryImpl<Expense>(context))
        cardRepository = CardRepository(RepositoryImpl<Card>(context), expenseRepository)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == expenses.size) {
            return ViewType.TYPE_TOTAL.ordinal
        } else {
            return ViewType.TYPE_NORMAL.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == ViewType.TYPE_TOTAL.ordinal) {
            val ui = CreditCardTotalViewUI()
            return ViewHolder(ui.createView(AnkoContext.create(parent.context, parent)), ui.value)
        } else {
            val ui = CreditCardViewUI()
            return ViewHolder(ui.createView(AnkoContext.create(parent.context, parent)), ui.value, ui.date)
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

    fun loadData(creditCard: Card, date: DateTime) {
        expenses = cardRepository.creditCardBills(creditCard, date)

        totalValue = expenses.sumBy { it.value }
    }
}

private class CreditCardViewUI : AnkoComponent<ViewGroup> {
    lateinit var date : TextView
    lateinit var value : TextView

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        verticalLayout {
            linearLayout {
                orientation = LinearLayout.HORIZONTAL

                date = textView { id = R.id.row_credit_card_expense_date }
                view {}.lparams(width = 0, height = 1, weight = 1f)
                value = textView { id = R.id.row_credit_card_expense_value }
            }.lparams { margin = resources.getDimensionPixelSize(R.dimen.min_spacing) }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}

private class CreditCardTotalViewUI : AnkoComponent<ViewGroup> {
    lateinit var value : TextView

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        verticalLayout {
            linearLayout {
                orientation = LinearLayout.HORIZONTAL

                textView {
                    text = resources.getString(R.string.total)
                    typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                }
                view {}.lparams(width = 0, height = 1, weight = 1f)
                value = textView {
                    id = R.id.row_credit_card_expense_value
                    typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                }
            }.lparams { margin = resources.getDimensionPixelSize(R.dimen.min_spacing) }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
