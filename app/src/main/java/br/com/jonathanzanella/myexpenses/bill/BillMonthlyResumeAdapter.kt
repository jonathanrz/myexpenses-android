package br.com.jonathanzanella.myexpenses.bill

import android.graphics.Typeface
import android.support.annotation.WorkerThread
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.resumeRowCell
import br.com.jonathanzanella.myexpenses.views.anko.singleRowCell
import org.jetbrains.anko.*
import org.joda.time.DateTime

class BillMonthlyResumeAdapter : RecyclerView.Adapter<BillMonthlyResumeAdapter.ViewHolder>() {
    private var bills: List<Bill> = ArrayList()
    private val billRepository: BillRepository
    var totalValue: Int = 0
        private set

    private enum class ViewType {
        TYPE_NORMAL,
        TYPE_TOTAL
    }

    inner class ViewHolder(itemView: View, val name: TextView, val amount: TextView, val day: TextView?) : RecyclerView.ViewHolder(itemView) {
        fun setData(bill: Bill) {
            name.text = bill.name
            amount.text = CurrencyHelper.format(bill.amount)
            day?.text = bill.dueDate.toString()
        }

        fun setTotal(totalValue: Int) {
            amount.text = CurrencyHelper.format(totalValue)
        }
    }

    init {
        val expenseRepository = ExpenseRepository(RepositoryImpl<Expense>(MyApplication.getContext()))
        billRepository = BillRepository(RepositoryImpl<Bill>(MyApplication.getContext()), expenseRepository)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == bills.size) {
            return ViewType.TYPE_TOTAL.ordinal
        } else {
            return ViewType.TYPE_NORMAL.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == ViewType.TYPE_TOTAL.ordinal) {
            val ui = TotalViewUI()
            return ViewHolder(ui.createView(AnkoContext.create(parent.context, parent)), ui.name, ui.amount, null)
        } else {
            val ui = NormalViewUI()
            return ViewHolder(ui.createView(AnkoContext.create(parent.context, parent)), ui.name, ui.amount, ui.day)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == bills.size)
            holder.setTotal(totalValue)
        else
            holder.setData(bills[position])
    }

    override fun getItemCount(): Int {
        return bills.size + 1
    }

    @WorkerThread
    fun loadData(month: DateTime) {
        bills = billRepository.monthly(month)
        totalValue = bills.sumBy { it.amount }
    }
}

class NormalViewUI: AnkoComponent<ViewGroup> {
    lateinit var name: TextView
    lateinit var day: TextView
    lateinit var amount: TextView

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        linearLayout {
            singleRowCell {
                day = textView {
                    id = R.id.row_monthly_resume_bill_day
                }.lparams {
                    marginEnd = resources.getDimensionPixelSize(R.dimen.min_spacing)
                }

                name = textView {
                    id = R.id.row_monthly_resume_bill_name
                }.lparams(width = 0, weight = 1f) {
                    marginEnd = resources.getDimensionPixelSize(R.dimen.min_spacing)
                }

                amount = textView {
                    id = R.id.row_monthly_resume_bill_amount
                    textColor = ResourcesCompat.getColor(resources, R.color.value_unpaid, null)
                }

                gravity = Gravity.CENTER_VERTICAL
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}

class TotalViewUI: AnkoComponent<ViewGroup> {
    lateinit var name: TextView
    lateinit var amount: TextView

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        linearLayout {
            resumeRowCell {
                name = textView {
                    id = R.id.row_monthly_resume_bill_name
                    typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                }.lparams(width = 0, weight = 1f) {
                    marginEnd = resources.getDimensionPixelSize(R.dimen.min_spacing)
                }

                amount = textView {
                    id = R.id.row_monthly_resume_bill_amount
                    typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}