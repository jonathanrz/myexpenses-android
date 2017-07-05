package br.com.jonathanzanella.myexpenses.card

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.expense.CreditCardMonthlyAdapter
import br.com.jonathanzanella.myexpenses.views.BaseView
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.recyclerView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.applyRecursively
import org.jetbrains.anko.frameLayout
import org.joda.time.DateTime

@SuppressLint("ViewConstructor")
class CreditCardInvoiceView(context: Context, val creditCard: Card, val month: DateTime) : BaseView(context) {
    private val ui = CreditCardInvoiceViewUI()
    private var adapter = CreditCardMonthlyAdapter(context)

    override fun onAttachedToWindow() {
        addView(ui.createView(AnkoContext.Companion.create(context, this)))

        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                adapter.loadData(creditCard, month)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                adapter.notifyDataSetChanged()
            }
        }.execute()

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
