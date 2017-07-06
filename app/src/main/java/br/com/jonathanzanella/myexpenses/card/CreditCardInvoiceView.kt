package br.com.jonathanzanella.myexpenses.card

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.expense.CreditCardMonthlyAdapter
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.recyclerView
import org.jetbrains.anko.*
import org.joda.time.DateTime

@SuppressLint("ViewConstructor")
class CreditCardInvoiceView(context: Context, val creditCard: Card, val month: DateTime) : FrameLayout(context) {
    private val ui = CreditCardInvoiceViewUI()
    private var adapter = CreditCardMonthlyAdapter(context)

    init {
        addView(ui.createView(AnkoContext.Companion.create(context, this)))

        doAsync {
            adapter.loadData(creditCard, month)

            uiThread { adapter.notifyDataSetChanged() }
        }

        ui.list.adapter = adapter
        ui.list.setHasFixedSize(true)
        ui.list.layoutManager = LinearLayoutManager(context)
    }
}

class CreditCardInvoiceViewUI: AnkoComponent<CreditCardInvoiceView> {
    lateinit var list : RecyclerView

    override fun createView(ui: AnkoContext<CreditCardInvoiceView>) = with(ui) {
        frameLayout {
            list = recyclerView { id = R.id.view_credit_card_invoice_list }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
