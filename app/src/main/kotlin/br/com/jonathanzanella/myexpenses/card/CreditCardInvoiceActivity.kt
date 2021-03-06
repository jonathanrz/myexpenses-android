package br.com.jonathanzanella.myexpenses.card

import android.content.Context
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapter
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapterBuilder
import br.com.jonathanzanella.myexpenses.views.anko.TemplateToolbar
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.toolbarTemplate
import org.jetbrains.anko.*
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.support.v4.viewPager
import org.joda.time.DateTime
import javax.inject.Inject

class CreditCardInvoiceActivity : AppCompatActivity() {
    private var card: Card? = null
    private var initDate: DateTime? = null
    @Inject
    lateinit var cardDataSource: CardDataSource
    private val ui = CreditCardInvoiceActivityUi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getAppComponent().inject(this)
        ui.setContentView(this)

        storeBundle(savedInstanceState)
        storeBundle(intent.extras)

        ui.toolbar.setup(this)
    }

    @UiThread
    fun storeBundle(extras: Bundle?) {
        if (extras == null)
            return

        doAsync {
            if (extras.containsKey(KEY_CREDIT_CARD_UUID))
                card = cardDataSource.find(extras.getString(KEY_CREDIT_CARD_UUID))
            if (extras.containsKey(KEY_INIT_DATE))
                initDate = extras.getSerializable(KEY_INIT_DATE) as DateTime

            uiThread {
                val adapter = MonthlyPagerAdapter(this@CreditCardInvoiceActivity, object : MonthlyPagerAdapterBuilder {
                    override fun buildView(ctx: Context, date: DateTime): View {
                        return CreditCardInvoiceView(ctx, card!!, date)
                    }
                })
                ui.pager.adapter = adapter
                ui.pager.currentItem = adapter.getDatePosition(initDate!!)
                ui.tabs.setupWithViewPager(ui.pager)
            }
        }
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
            appBarLayout(android.R.style.ThemeOverlay_Material_Dark_ActionBar) {
                toolbar = toolbarTemplate {}
                tabs = tabLayout {
                    id = R.id.tabs
                    tabMode = TabLayout.MODE_SCROLLABLE
                }.lparams(width = matchParent)
            }

            pager = viewPager {
                id = R.id.act_credit_card_invoice_pager
            }.lparams(width = matchParent, height = matchParent)
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
