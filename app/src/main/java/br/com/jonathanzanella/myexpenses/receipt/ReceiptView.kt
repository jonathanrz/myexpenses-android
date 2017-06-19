package br.com.jonathanzanella.myexpenses.receipt

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.helpers.DateHelper
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapter
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapterBuilder
import br.com.jonathanzanella.myexpenses.views.BaseView
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import org.jetbrains.anko.*
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.support.v4.viewPager
import org.joda.time.DateTime
import java.lang.ref.WeakReference
import java.util.*

class ReceiptView : BaseView, ViewPager.OnPageChangeListener {
    private val views = HashMap<DateTime, WeakReference<ReceiptMonthlyView>>()

    private val ui = ReceiptViewUI()
    private var adapter: MonthlyPagerAdapter? = null
    private var repository: ReceiptRepository? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addView(ui.createView(AnkoContext.Companion.create(context, this)))

        repository = ReceiptRepository(RepositoryImpl<Receipt>(context))
        adapter = MonthlyPagerAdapter(context, MonthlyPagerAdapterBuilder { ctx, date ->
            val view = ReceiptMonthlyView(ctx, date)
            views.put(date, WeakReference(view))
            view
        })

        ui.pager.adapter = adapter
        ui.pager.currentItem = MonthlyPagerAdapter.INIT_MONTH_VISIBLE
        ui.pager.addOnPageChangeListener(this)
    }

    override fun init() {
        //TODO: remove when convert BaseView to interface
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        filter(filter)
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun setTabs(tabs: TabLayout) {
        tabs.setupWithViewPager(ui.pager)
        tabs.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_ADD_RECEIPT -> if (resultCode == Activity.RESULT_OK)
                loadExpense(data!!.getStringExtra(EditReceiptActivity.KEY_RECEIPT_UUID))
        }
    }

    private fun loadExpense(uuid: String) {
        object : AsyncTask<Void, Void, Receipt>() {

            override fun doInBackground(vararg voids: Void): Receipt {
                return repository!!.find(uuid)
            }

            override fun onPostExecute(receipt: Receipt?) {
                super.onPostExecute(receipt)
                if (receipt != null) {
                    val view = getMonthView(receipt.date)
                    view?.refreshData()
                }
            }
        }.execute()
    }

    override fun filter(s: String) {
        super.filter(s)
        val date = adapter!!.getDate(ui.pager.currentItem)
        val view = getMonthView(date)
        view?.filter(filter)
    }

    private fun getMonthView(date: DateTime): ReceiptMonthlyView? {
        for ((key, value) in views) {
            val viewDateFirstDay = DateHelper.firstDayOfMonth(key)
            val viewDateLastDay = DateHelper.lastDayOfMonth(key)
            if (date.isEqual(viewDateFirstDay) || date.isEqual(viewDateLastDay) ||
                    date.isAfter(viewDateFirstDay) && date.isBefore(viewDateLastDay))
                return value.get()
        }

        return null
    }

    companion object {
        internal val REQUEST_ADD_RECEIPT = 1007
    }
}

private class ReceiptViewUI: AnkoComponent<ReceiptView> {
    lateinit var pager : ViewPager

    override fun createView(ui: AnkoContext<ReceiptView>) = with(ui) {
        frameLayout {
            pager = viewPager { R.id.view_receipts_pager }.lparams(width = matchParent, height = matchParent)
            floatingActionButton {
                id = R.id.view_receipts_fab
                onClick {
                    val ctx = context
                    val i = Intent(context, EditReceiptActivity::class.java)
                    if (ctx is Activity) {
                        ctx.startActivityForResult(i, ReceiptView.REQUEST_ADD_RECEIPT)
                    } else {
                        ctx.startActivity(i)
                    }
                }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}