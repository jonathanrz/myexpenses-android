package br.com.jonathanzanella.myexpenses.bill

import android.content.Intent
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.helpers.AdapterColorHelper
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.rowPrincipalInformation
import br.com.jonathanzanella.myexpenses.views.anko.rowSecondaryInformation
import br.com.jonathanzanella.myexpenses.views.anko.rowStaticInformation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.*
import javax.inject.Inject

open class BillAdapter @Inject constructor(val repository: BillRepository) : RecyclerView.Adapter<BillAdapter.ViewHolder>() {
    private var dbQueryDisposable: Disposable? = null
    private var callback: BillAdapterCallback? = null
    private var bills: List<Bill> = ArrayList()

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
            ui.billAmount.text = bill.amount.toCurrencyFormatted()
            ui.billDueDate.text = bill.dueDate.toString()
            val datesText = Transaction.SIMPLE_DATE_FORMAT.format(bill.initDate?.toDate()) + " - " +
                    Transaction.SIMPLE_DATE_FORMAT.format(bill.endDate?.toDate())
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ui = ViewUI()
        return ViewHolder(ui.createView(AnkoContext.create(parent.context, parent)), ui)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(getBill(position))
    }

    override fun getItemCount(): Int = bills.size

    fun onDestroy() {
        dbQueryDisposable?.dispose()
    }

    fun refreshData(filter: String? = null) {
        dbQueryDisposable?.dispose()

        var flowable = repository.all()

        if(filter != null)
            flowable = flowable.map { it.filter { it.name!!.contains(filter) } }

        dbQueryDisposable = flowable
            .doOnNext { bills = it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { notifyDataSetChanged() }
    }

    private fun getBill(position: Int): Bill = bills[position]

    fun filter(filter: String) {
        refreshData(filter)
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
