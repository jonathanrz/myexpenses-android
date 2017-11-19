package br.com.jonathanzanella.myexpenses.ui.receipt

import android.content.Context
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder
import br.com.jonathanzanella.myexpenses.source.Source
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import br.com.jonathanzanella.myexpenses.ui.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.*
import br.com.jonathanzanella.myexpenses.views.MainActivity
import junit.framework.Assert.assertTrue
import org.joda.time.DateTime
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddReceiptTest {
    @Rule @JvmField
    var mainActivityTestRule = ActivityTestRule(MainActivity::class.java)
    @Rule @JvmField
    var editReceiptActivityTestRule = ActivityTestRule(br.com.jonathanzanella.myexpenses.receipt.EditReceiptActivity::class.java)

    private lateinit var account: Account
    private lateinit var source: Source

    private val context: Context
        get() = getTargetContext()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        App.resetDatabase()

        val accountDataSource = App.getApp().appComponent.accountDataSource()
        val sourceDataSource = App.getApp().appComponent.sourceDataSource()

        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        if (!uiDevice.isScreenOn)
            uiDevice.wakeUp()

        account = AccountBuilder().build()
        assertTrue(accountDataSource.save(account).blockingFirst().isValid)

        source = SourceBuilder().build()
        assertTrue(sourceDataSource.save(source).isValid)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    @Throws(InterruptedException::class)
    fun add_new_receipt() {
        mainActivityTestRule.launchActivity(Intent())

        openMenuAndClickItem(R.string.receipts)

        val receiptsTitle = context.getString(R.string.receipts)
        matchToolbarTitle(receiptsTitle)

        clickIntoView(R.id.view_receipts_fab)

        val newReceiptTitle = context.getString(R.string.new_receipt_title)
        matchToolbarTitle(newReceiptTitle)

        val receiptName = "Test"
        clearAndTypeTextIntoView(R.id.act_edit_receipt_name, receiptName)
        clearAndTypeTextIntoView(R.id.act_edit_receipt_income, "100")
        clickIntoView(R.id.act_edit_receipt_date)
        val time = DateTime.now().plusMonths(1)
        setTimeInDatePicker(time.year, time.monthOfYear, time.dayOfMonth)

        selectAccount()
        selectSource()
        matchToolbarTitle(newReceiptTitle)

        val formattedDate = Transaction.SIMPLE_DATE_FORMAT.format(time.toDate())
        onView(withId(R.id.act_edit_receipt_date)).check(matches(withText(formattedDate)))

        clickIntoView(R.id.action_save)

        matchToolbarTitle(receiptsTitle)

        onView(withId(R.id.row_receipt_name)).check(matches(withText(receiptName)))
        onView(withId(R.id.row_receipt_date)).check(matches(withText(formattedDate)))
    }

    @Test
    fun add_new_receipt_shows_error_with_empty_name() {
        editReceiptActivityTestRule.launchActivity(Intent())

        val newReceiptTitle = context.getString(R.string.new_receipt_title)
        matchToolbarTitle(newReceiptTitle)

        clickIntoView(R.id.action_save)

        val errorMessage = context.getString(R.string.error_message_name_not_informed)
        matchErrorMessage(R.id.act_edit_receipt_name, errorMessage)
    }

    @Test
    fun add_new_receipt_shows_error_with_empty_income() {
        editReceiptActivityTestRule.launchActivity(Intent())

        val newReceiptTitle = context.getString(R.string.new_receipt_title)
        matchToolbarTitle(newReceiptTitle)

        clickIntoView(R.id.action_save)

        val errorMessage = context.getString(R.string.error_message_amount_zero)
        matchErrorMessage(R.id.act_edit_receipt_income, errorMessage)
    }

    @Test
    fun add_new_receipt_shows_error_with_empty_source() {
        editReceiptActivityTestRule.launchActivity(Intent())

        val newReceiptTitle = context.getString(R.string.new_receipt_title)
        matchToolbarTitle(newReceiptTitle)

        clickIntoView(R.id.action_save)

        val errorMessage = context.getString(R.string.error_message_source_not_informed)
        matchErrorMessage(R.id.act_edit_receipt_source, errorMessage)
    }

    @Test
    fun add_new_receipt_shows_error_with_empty_account() {
        editReceiptActivityTestRule.launchActivity(Intent())

        val newReceiptTitle = context.getString(R.string.new_receipt_title)
        matchToolbarTitle(newReceiptTitle)

        clickIntoView(R.id.action_save)

        val errorMessage = context.getString(R.string.error_message_account_not_informed)
        matchErrorMessage(R.id.act_edit_receipt_account, errorMessage)
    }

    private fun selectSource() {
        val selectSourceTitle = context.getString(R.string.select_source_title)
        clickIntoView(R.id.act_edit_receipt_source)
        matchToolbarTitle(selectSourceTitle)
        clickIntoView(source.name)
    }

    private fun selectAccount() {
        val selectAccountTitle = context.getString(R.string.select_account_title)
        clickIntoView(R.id.act_edit_receipt_account)
        matchToolbarTitle(selectAccountTitle)
        clickIntoView(account.name)
    }
}
