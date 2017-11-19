package br.com.jonathanzanella.myexpenses.ui.receipt

import android.content.Intent
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder
import br.com.jonathanzanella.myexpenses.receipt.Receipt
import br.com.jonathanzanella.myexpenses.receipt.ReceiptDataSource
import br.com.jonathanzanella.myexpenses.receipt.ShowReceiptActivity
import br.com.jonathanzanella.myexpenses.source.SourceDataSource
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import br.com.jonathanzanella.myexpenses.ui.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.*
import junit.framework.Assert.assertTrue
import org.hamcrest.core.Is.`is`
import org.joda.time.DateTime
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class EditReceiptTest {
    @Rule @JvmField
    var activityTestRule = ActivityTestRule(ShowReceiptActivity::class.java, true, false)
    lateinit var accountDataSource: AccountDataSource
    lateinit var sourceDataSource: SourceDataSource
    lateinit var dataSource: ReceiptDataSource

    private lateinit var receipt: Receipt

    @Before
    @Throws(Exception::class)
    fun setUp() {
        App.resetDatabase()

        accountDataSource = App.getApp().appComponent.accountDataSource()
        sourceDataSource = App.getApp().appComponent.sourceDataSource()
        dataSource = App.getApp().appComponent.receiptDataSource()

        val a = AccountBuilder().build()
        assertTrue(accountDataSource.save(a).blockingFirst().isValid)

        val s = SourceBuilder().build()
        assertTrue(sourceDataSource.save(s).isValid)

        receipt = ReceiptBuilder()
                .date(DateTime.now().minusDays(1))
                .account(a)
                .source(s)
                .build()
        assertTrue(dataSource.save(receipt).isValid)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    @Throws(Exception::class)
    fun edit_expense_correctly() {
        val i = Intent()
        i.putExtra(br.com.jonathanzanella.myexpenses.receipt.ShowReceiptActivity.KEY_RECEIPT_UUID, receipt.uuid)
        activityTestRule.launchActivity(i)

        val showReceiptTitle = getTargetContext().getString(R.string.receipt) + " " + receipt.name
        matchToolbarTitle(showReceiptTitle)

        clickIntoView(R.id.action_edit)

        val editReceiptTitle = getTargetContext().getString(R.string.edit_receipt_title)
        matchToolbarTitle(editReceiptTitle)
        onView(withId(R.id.act_edit_receipt_name)).check(matches(withText(receipt.name)))
        val expectedDate = Transaction.SIMPLE_DATE_FORMAT.format(receipt.getDate().toDate())
        onView(withId(R.id.act_edit_receipt_date)).check(matches(withText(expectedDate)))
        onView(withId(R.id.act_edit_receipt_account)).check(matches(withText(receipt.accountFromCache!!.name)))
        clearAndTypeTextIntoView(R.id.act_edit_receipt_name, receipt.name!! + " changed")

        clickIntoView(R.id.action_save)

        matchToolbarTitle(showReceiptTitle + " changed")

        receipt = dataSource.find(receipt.uuid!!)!!

        onView(withId(R.id.act_show_receipt_name)).check(matches(withText(receipt.name)))
        assertThat(dataSource.all().size, `is`(1))
    }
}