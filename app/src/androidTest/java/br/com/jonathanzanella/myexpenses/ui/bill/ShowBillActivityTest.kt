package br.com.jonathanzanella.myexpenses.ui.bill

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
import br.com.jonathanzanella.myexpenses.bill.Bill
import br.com.jonathanzanella.myexpenses.bill.BillDataSource
import br.com.jonathanzanella.myexpenses.bill.ShowBillActivity
import br.com.jonathanzanella.myexpenses.ui.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.ui.helpers.TestUtils.waitForIdling
import br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.matchToolbarTitle
import br.com.jonathanzanella.myexpenses.helpers.builder.BillBuilder
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class ShowBillActivityTest {
    @Rule @JvmField
    var activityTestRule = ActivityTestRule(ShowBillActivity::class.java, true, false)

    private lateinit var bill: Bill
    lateinit var dataSource: BillDataSource

    @Before
    @Throws(Exception::class)
    fun setUp() {
        App.resetDatabase()
        dataSource = App.getApp().appComponent.billDataSource()

        bill = BillBuilder().build()
        dataSource.save(bill).subscribe { assert(it.isValid) }
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    @Throws(Exception::class)
    fun shows_account_correctly() {
        val i = Intent()
        i.putExtra(ShowBillActivity.KEY_BILL_UUID, bill.uuid)
        activityTestRule.launchActivity(i)

        waitForIdling()

        val editBillTitle = getTargetContext().getString(R.string.bill) + " " + bill.name
        matchToolbarTitle(editBillTitle)

        val balanceAsCurrency = bill.amount.toCurrencyFormatted()
        onView(withId(R.id.act_show_bill_name)).check(matches(withText(bill.name)))
        onView(withId(R.id.act_show_bill_amount)).check(matches(withText(balanceAsCurrency)))
    }
}