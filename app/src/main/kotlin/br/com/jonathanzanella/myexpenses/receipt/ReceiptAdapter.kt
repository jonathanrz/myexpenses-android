package br.com.jonathanzanella.myexpenses.receipt

import android.content.Intent
import android.support.annotation.UiThread
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.helpers.AdapterColorHelper
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.jetbrains.anko.*
import org.joda.time.DateTime
import javax.inject.Inject

open class ReceiptAdapter : RecyclerView.Adapter<ReceiptAdapter.ViewHolder>() {
    @Inject
    lateinit var receiptDataSource: ReceiptDataSource
    private var receipts: List<Receipt> = ArrayList()
    private val presenter: ReceiptAdapterPresenter
    private var date: DateTime? = null

    init {
        App.getAppComponent().inject(this)
        presenter = ReceiptAdapterPresenter(receiptDataSource)
    }

    inner class ViewHolder internal constructor(itemView: View, private val ui: ViewUI) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val adapterColorHelper: AdapterColorHelper

        init {
            val oddColor = ResourcesCompat.getColor(itemView.context.resources, R.color.color_list_odd, null)
            val evenColor = ResourcesCompat.getColor(itemView.context.resources, R.color.color_list_even, null)
            adapterColorHelper = AdapterColorHelper(oddColor, evenColor)

            itemView.setOnClickListener(this)
        }

        @UiThread
        fun setData(receipt: Receipt) {
            itemView.tag = receipt.uuid
            itemView.setBackgroundColor(adapterColorHelper.getColorForGridWithTwoColumns(adapterPosition))
            ui.name.text = receipt.name
            ui.date.text = Transaction.SIMPLE_DATE_FORMAT.format(receipt.getDate().toDate())
            ui.income.text = receipt.income.toCurrencyFormatted()

            doAsync {
                val s = receipt.source
                val a = receipt.accountFromCache

                uiThread {
                    ui.source.text = s?.name
                    ui.account.text = a?.name
                }
            }

            ui.showInResume.visibility = if (receipt.isShowInResume) View.VISIBLE else View.INVISIBLE
        }

        override fun onClick(v: View) {
            val i = Intent(itemView.context, ShowReceiptActivity::class.java)
            i.putExtra(ShowReceiptActivity.KEY_RECEIPT_UUID, getReceipt(adapterPosition).uuid)
            itemView.context.startActivity(i)
        }
    }

    fun loadData(date: DateTime) {
        receipts = presenter.getReceipts(true, date)
        this.date = date
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ui = ViewUI()
        return ViewHolder(ui.createView(AnkoContext.create(parent.context, parent)), ui)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(getReceipt(position))
    }

    override fun getItemCount(): Int {
        return receipts.size
    }

    private fun getReceipt(position: Int): Receipt {
        return receipts[position]
    }

    fun filter(filter: String) {
        presenter.filter(filter)
        receipts = presenter.getReceipts(false, date)
    }
}

internal class ViewUI: AnkoComponent<ViewGroup> {
    lateinit var name: TextView
    lateinit var date: TextView
    lateinit var income: TextView
    lateinit var source: TextView
    lateinit var account: TextView
    lateinit var showInResume: TextView

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        relativeLayout {
            padding = resources.getDimensionPixelSize(R.dimen.row_spacing)

            name = rowPrincipalInformation { id = R.id.row_receipt_name
            }.lparams {
                marginEnd = resources.getDimensionPixelSize(R.dimen.min_spacing)
                leftOf(R.id.row_receipt_income)
                alignParentStart()
            }
            date = rowDetailInformation { id = R.id.row_receipt_date }.lparams { below(R.id.row_receipt_name) }
            income = rowSecondaryInformation { id = R.id.row_receipt_income }.lparams { alignParentEnd() }

            linearLayout {
                id = R.id.row_receipt_source_layout
                orientation = LinearLayout.HORIZONTAL

                rowStaticInformation { text = resources.getString(R.string.source)
                }.lparams { marginEnd = resources.getDimensionPixelSize(R.dimen.min_spacing) }

                source = rowSecondaryInformation { id = R.id.row_receipt_source }
            }.lparams(height = wrapContent) {
                topMargin = resources.getDimensionPixelSize(R.dimen.min_spacing)
                below(R.id.row_receipt_date)
            }

            linearLayout {
                id = R.id.row_receipt_account_layout
                orientation = LinearLayout.HORIZONTAL

                rowStaticInformation { text = resources.getString(R.string.account)
                }.lparams { marginEnd = resources.getDimensionPixelSize(R.dimen.min_spacing) }

                account = rowSecondaryInformation { id = R.id.row_receipt_account }
            }.lparams(height = wrapContent) {
                topMargin = resources.getDimensionPixelSize(R.dimen.min_spacing)
                below(R.id.row_receipt_source_layout)
            }

            showInResume = rowDetailInformation {
                id = R.id.row_receipt_show_in_resume_stt
                text = resources.getString(R.string.show_in_resume)
                visibility = View.INVISIBLE
            }.lparams {
                below(R.id.row_receipt_account_layout)
                alignParentEnd()
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
