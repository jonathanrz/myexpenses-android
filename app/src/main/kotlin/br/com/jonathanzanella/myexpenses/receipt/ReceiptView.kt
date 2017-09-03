package br.com.jonathanzanella.myexpenses.receipt

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.helpers.firstDayOfMonth
import br.com.jonathanzanella.myexpenses.helpers.lastDayOfMonth
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapter
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapterBuilder
import br.com.jonathanzanella.myexpenses.views.FilterableView
import br.com.jonathanzanella.myexpenses.views.ResultableView
import br.com.jonathanzanella.myexpenses.views.TabableView
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import org.jetbrains.anko.*
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.support.v4.viewPager
import org.joda.time.DateTime
import java.lang.ref.WeakReference
import java.util.*

class ReceiptView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ResultableView, TabableView, FilterableView, ViewPager.OnPageChangeListener {
    override var filter = ""
    private val views = HashMap<DateTime, WeakReference<ReceiptMonthlyView>>()

    private val ui = ReceiptViewUI()
    private var adapter: MonthlyPagerAdapter? = null
    private var repository: ReceiptRepository? = null

    init {
        addView(ui.createView(AnkoContext.Companion.create(context, this)))

        repository = ReceiptRepository()
        adapter = MonthlyPagerAdapter(context, object : MonthlyPagerAdapterBuilder {
            override fun buildView(ctx: Context, date: DateTime): View {
                val view = ReceiptMonthlyView(ctx, date)
                views.put(date, WeakReference(view))
                return view
            }
        })

        ui.pager.adapter = adapter
        ui.pager.currentItem = MonthlyPagerAdapter.INIT_MONTH_VISIBLE
        ui.pager.addOnPageChangeListener(this)
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
        when (requestCode) {
            REQUEST_ADD_RECEIPT -> if (resultCode == Activity.RESULT_OK)
                loadExpense(data!!.getStringExtra(EditReceiptActivity.KEY_RECEIPT_UUID))
        }
    }

    private fun loadExpense(uuid: String) {
        doAsync {
            val receipt = repository!!.find(uuid)

            uiThread { receipt?.let { getMonthView(it.getDate())?.refreshData() }}
        }
    }

    override fun filter(s: String) {
        super.filter(s)
        val date = adapter!!.getDate(ui.pager.currentItem)
        val view = getMonthView(date)
        view?.filter(filter)
    }

    private fun getMonthView(date: DateTime): ReceiptMonthlyView? {
        for ((key, value) in views) {
            val viewDateFirstDay = key.firstDayOfMonth()
            val viewDateLastDay = key.lastDayOfMonth()
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
