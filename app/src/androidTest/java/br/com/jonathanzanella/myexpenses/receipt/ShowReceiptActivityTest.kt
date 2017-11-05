package br.com.jonathanzanella.myexpenses.receipt

import android.content.Intent
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.source.SourceDataSource
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class ShowReceiptActivityTest {
    @Rule @JvmField
    var activityTestRule = ActivityTestRule(ShowReceiptActivity::class.java, true, false)

    private var receipt: Receipt? = null

    lateinit var dataSource: ReceiptDataSource
    lateinit var sourceDataSource: SourceDataSource
    lateinit var accountDataSource: AccountDataSource

    @Before
    @Throws(Exception::class)
    fun setUp() {
        App.resetDatabase()
        dataSource = App.getApp().appComponent.receiptDataSource()
        accountDataSource = App.getApp().appComponent.accountDataSource()
        sourceDataSource = App.getApp().appComponent.sourceDataSource()

        val s = SourceBuilder().build()
        assertTrue(sourceDataSource.save(s).isValid)

        val a = AccountBuilder().build()
        assertTrue(accountDataSource.save(a).blockingFirst().isValid)

        receipt = ReceiptBuilder().source(s).account(a).build()
        assertTrue(dataSource.save(receipt!!).isValid)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    @Throws(Exception::class)
    fun shows_receipt_correctly() {
        val i = Intent()
        i.putExtra(ShowReceiptActivity.KEY_RECEIPT_UUID, receipt!!.uuid)
        activityTestRule.launchActivity(i)

        val editReceiptTitle = getTargetContext().getString(R.string.receipt) + " " + receipt!!.name
        matchToolbarTitle(editReceiptTitle)

        val incomeAsCurrency = receipt!!.income.toCurrencyFormatted()
        onView(withId(R.id.act_show_receipt_name)).check(matches(withText(receipt!!.name)))
        onView(withId(R.id.act_show_receipt_income)).check(matches(withText(incomeAsCurrency)))
        val account = receipt!!.accountFromCache
        onView(withId(R.id.act_show_receipt_account)).check(matches(withText(account!!.name)))

        val source = receipt!!.source
        onView(withId(R.id.act_show_receipt_source)).check(matches(withText(source!!.name)))
    }
}