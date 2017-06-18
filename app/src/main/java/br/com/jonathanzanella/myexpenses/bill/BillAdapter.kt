package br.com.jonathanzanella.myexpenses.bill

import android.content.Intent
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.helpers.AdapterColorHelper
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.rowPrincipalInformation
import br.com.jonathanzanella.myexpenses.views.anko.rowSecondaryInformation
import br.com.jonathanzanella.myexpenses.views.anko.rowStaticInformation
import org.jetbrains.anko.*

open internal class BillAdapter : RecyclerView.Adapter<BillAdapter.ViewHolder>() {
    private var callback: BillAdapterCallback? = null
    private val presenter: BillAdapterPresenter

    inner class ViewHolder(itemView: View, val ui : ViewUI) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val adapterColorHelper: AdapterColorHelper

        init {
            val oddColor = ResourcesCompat.getColor(itemView.context.resources, R.color.color_list_odd, null)
            val evenColor = ResourcesCompat.getColor(itemView.context.resources, R.color.color_list_even, null)
            adapterColorHelper = AdapterColorHelper(oddColor, evenColor)

            itemView.setOnClickListener(this)
        }

        fun setData(bill: Bill) {
            itemView.setBackgroundColor(adapterColorHelper.getColorForGridWithTwoColumns(adapterPosition))
            ui.billName.text = bill.name
            ui.billAmount.text = CurrencyHelper.format(bill.amount)
            ui.billDueDate.text = bill.dueDate.toString()
            val datesText = Bill.SIMPLE_DATE_FORMAT.format(bill.initDate.toDate()) + " - " +
                    Bill.SIMPLE_DATE_FORMAT.format(bill.endDate.toDate())
            ui.billDates.text = datesText
        }

        override fun onClick(v: View) {
            val bill = getBill(adapterPosition)
            if (callback != null) {
                callback!!.onBillSelected(bill)
            } else {
                val i = Intent(itemView.context, ShowBillActivity::class.java)
                i.putExtra(ShowBillActivity.KEY_BILL_UUID, bill.uuid)
                itemView.context.startActivity(i)
            }
        }
    }

    init {
        val expenseRepository = ExpenseRepository(RepositoryImpl<Expense>(MyApplication.getContext()))
        val repository = BillRepository(RepositoryImpl<Bill>(MyApplication.getContext()), expenseRepository)
        this.presenter = BillAdapterPresenter(repository)
        refreshData()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ui = ViewUI()
        return ViewHolder(ui.createView(AnkoContext.create(parent.context, parent)), ui)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(getBill(position))
    }

    override fun getItemCount(): Int {
        return presenter.billsCount
    }

    fun refreshData() {
        presenter.getBills(true)
    }

    private fun getBill(position: Int): Bill {
        return presenter.getBill(position)
    }

    fun filter(filter: String) {
        presenter.filter(filter)
        presenter.getBills(false)
    }

    fun setCallback(callback: BillAdapterCallback) {
        this.callback = callback
    }
}

class ViewUI: AnkoComponent<ViewGroup> {
    lateinit var billName : TextView
    lateinit var billAmount : TextView
    lateinit var billDueDate : TextView
    lateinit var billDates : TextView

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        relativeLayout {
            padding = context.resources.getDimensionPixelSize(R.dimen.default_spacing)

            billName = rowPrincipalInformation {
                id = R.id.row_bill_name
            }.lparams {
                alignParentStart()
                leftOf(R.id.row_bill_amount)
            }

            billAmount = rowSecondaryInformation {
                id = R.id.row_bill_amount
            }.lparams {
                alignParentEnd()
            }

            rowStaticInformation {
                id = R.id.row_bill_balance_due_date_stt
                text = resources.getString(R.string.due_date_abbrev)
            }.lparams {
                marginEnd = dip(5)
                baselineOf(R.id.row_bill_due_date)
                below(R.id.row_bill_name)
            }

            billDueDate = rowSecondaryInformation {
                id = R.id.row_bill_due_date
                textSize = 10f
            }.lparams {
                below(R.id.row_bill_name)
                rightOf(R.id.row_bill_balance_due_date_stt)
            }

            billDates = textView {
                id = R.id.row_bill_dates
                textColor = ResourcesCompat.getColor(context.resources, R.color.color_primary, null)
                textSize = 10f
            }.lparams {
                below(R.id.row_bill_name)
                alignParentEnd()
                baselineOf(R.id.row_bill_due_date)
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
