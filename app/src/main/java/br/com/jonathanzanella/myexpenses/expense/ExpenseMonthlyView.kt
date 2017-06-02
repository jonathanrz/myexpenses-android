package br.com.jonathanzanella.myexpenses.expense

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.views.BaseView
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.recyclerView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.applyRecursively
import org.jetbrains.anko.frameLayout
import org.joda.time.DateTime

@SuppressLint("ViewConstructor")
internal class ExpenseMonthlyView(context: Context, private val dateTime: DateTime) : BaseView(context) {
    private val ui = ExpenseMonthlyViewUI()
    private var adapter: ExpenseAdapter = ExpenseAdapter()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addView(ui.createView(AnkoContext.Companion.create(context, this)))

        ui.expenses.adapter = adapter
        ui.expenses.layoutManager = GridLayoutManager(context, 2)
        ui.expenses.itemAnimator = DefaultItemAnimator()
    }

    override fun init() {
        //TODO: remove when convert BaseView to interface
    }

    override fun refreshData() {
        super.refreshData()

        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                adapter.loadData(dateTime)
                return null
            }

            override fun onPostExecute(aVoid: Void) {
                super.onPostExecute(aVoid)
                adapter.notifyDataSetChanged()
            }
        }.execute()
    }

    override fun filter(s: String) {
        super.filter(s)
        adapter.filter(s)
        adapter.notifyDataSetChanged()
    }
}

private class ExpenseMonthlyViewUI: AnkoComponent<ExpenseMonthlyView> {
    lateinit var expenses : RecyclerView

    override fun createView(ui: AnkoContext<ExpenseMonthlyView>) = with(ui) {
        frameLayout {
            expenses = recyclerView { id = R.id.view_expenses_monthly_list }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
