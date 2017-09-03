package br.com.jonathanzanella.myexpenses.expense

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

class ExpenseView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ResultableView, TabableView, FilterableView, ViewPager.OnPageChangeListener {

    override var filter = ""
    private val ui = ExpenseViewUI()
    internal var adapter: MonthlyPagerAdapter
    internal var expenseRepository: ExpenseRepository = ExpenseRepository()

    private val views = HashMap<DateTime, WeakReference<ExpenseMonthlyView>>()

    init {
        addView(ui.createView(AnkoContext.Companion.create(context, this)))

        adapter = MonthlyPagerAdapter(context, object : MonthlyPagerAdapterBuilder {
            override fun buildView(ctx: Context, date: DateTime): View {
                val view = ExpenseMonthlyView(ctx, date)
                views.put(date, WeakReference(view))
                view.filter(filter)
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

    internal fun onFab() {
        val ctx = context
        val i = Intent(context, EditExpenseActivity::class.java)
        if (ctx is Activity) {
            ctx.startActivityForResult(i, REQUEST_ADD_EXPENSE)
        } else {
            ctx.startActivity(i)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ADD_EXPENSE -> if (resultCode == Activity.RESULT_OK)
                loadExpense(data!!.getStringExtra(EditExpenseActivity.KEY_EXPENSE_UUID))
        }
    }

    private fun loadExpense(uuid: String) {
        doAsync {
            val expense = expenseRepository.find(uuid)

            uiThread {
                expense?.let {
                    val view = getMonthView(it.getDate())
                    view?.refreshData()
                }
            }
        }
    }

    override fun filter(s: String) {
        super.filter(s)
        val date = adapter.getDate(ui.pager.currentItem)
        val view = getMonthView(date)
        view?.filter(filter)
    }

    private fun getMonthView(date: DateTime): ExpenseMonthlyView? {
        for ((key, value) in views) {
            val viewDateFirstDay = key.firstDayOfMonth()
            val viewDateLastDay = key.lastDayOfMonth()
            val afterOrEqualToFirstDay = date.isEqual(viewDateFirstDay) || date.isAfter(viewDateFirstDay)
            if (afterOrEqualToFirstDay || date.isEqual(viewDateLastDay) && date.isBefore(viewDateLastDay))
                return value.get()
        }

        return null
    }

    companion object {
        private val REQUEST_ADD_EXPENSE = 1006
    }
}

private class ExpenseViewUI: AnkoComponent<ExpenseView> {
    lateinit var pager : ViewPager

    override fun createView(ui: AnkoContext<ExpenseView>) = with(ui) {
        frameLayout {
            pager = viewPager { R.id.view_expenses_pager }.lparams(width = matchParent, height = matchParent)
            floatingActionButton {
                id = R.id.view_expenses_fab
                onClick { ui.owner.onFab() }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
