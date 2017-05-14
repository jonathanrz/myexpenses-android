package br.com.jonathanzanella.myexpenses.card

import android.os.AsyncTask
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.R.id.tabs
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapter
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapterBuilder
import br.com.jonathanzanella.myexpenses.views.anko.TemplateToolbar
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.toolbarTemplate
import butterknife.Bind
import org.jetbrains.anko.*
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.support.v4.viewPager
import org.joda.time.DateTime

class CreditCardInvoiceActivity : AppCompatActivity() {

    private var card: Card? = null
    private var initDate: DateTime? = null
    private var cardRepository: CardRepository = CardRepository(RepositoryImpl<Card>(this), ExpenseRepository(RepositoryImpl<Expense>(this)))
    private val ui = CreditCardInvoiceActivityUi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)

        storeBundle(savedInstanceState)
        storeBundle(intent.extras)

        ui.toolbar.setup(this)
    }

    @UiThread
    fun storeBundle(extras: Bundle?) {
        if (extras == null)
            return
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                if (extras.containsKey(KEY_CREDIT_CARD_UUID))
                    card = cardRepository!!.find(extras.getString(KEY_CREDIT_CARD_UUID))
                if (extras.containsKey(KEY_INIT_DATE))
                    initDate = extras.getSerializable(KEY_INIT_DATE) as DateTime
                return null
            }

            override fun onPostExecute(aVoid: Void) {
                super.onPostExecute(aVoid)

                val adapter = MonthlyPagerAdapter(this@CreditCardInvoiceActivity,
                        MonthlyPagerAdapterBuilder { ctx, date -> CreditCardInvoiceView(ctx, card!!, date) })
                ui.pager.adapter = adapter
                ui.pager.currentItem = adapter.getDatePosition(initDate)
                ui.tabs.setupWithViewPager(ui.pager)
            }
        }.execute()
    }

    companion object {
        val KEY_CREDIT_CARD_UUID = "KeyCreateCardUuid"
        val KEY_INIT_DATE = "KeyInitDate"
    }
}

class CreditCardInvoiceActivityUi : AnkoComponent<CreditCardInvoiceActivity> {
    lateinit var toolbar: TemplateToolbar
    lateinit var tabs: TabLayout
    lateinit var pager: ViewPager

    override fun createView(ui: AnkoContext<CreditCardInvoiceActivity>) = with(ui) {
        verticalLayout {
            appBarLayout {
                toolbar = toolbarTemplate {}
                tabs = tabLayout {
                    id = R.id.tabs
                    tabMode = TabLayout.MODE_SCROLLABLE;
                }.lparams(width = matchParent)
            }

            pager = viewPager {
                id = R.id.act_credit_card_invoice_pager
            }.lparams(width = matchParent, height = matchParent)
        }.applyRecursively(::applyTemplateViewStyles)
    }
}