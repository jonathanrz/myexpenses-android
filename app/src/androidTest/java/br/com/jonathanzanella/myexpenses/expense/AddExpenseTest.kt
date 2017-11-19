package br.com.jonathanzanella.myexpenses.expense

import android.content.Context
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import android.view.View
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.bill.Bill
import br.com.jonathanzanella.myexpenses.bill.BillDataSource
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.helpers.UIHelper
import br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.BillBuilder
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import br.com.jonathanzanella.myexpenses.views.MainActivity
import junit.framework.Assert.assertTrue
import org.hamcrest.core.IsNot.not
import org.joda.time.DateTime
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddExpenseTest {
    @Rule @JvmField
    var mainActivityTestRule = ActivityTestRule(MainActivity::class.java)
    @Rule @JvmField
    var editExpenseActivityTestRule = ActivityTestRule(EditExpenseActivity::class.java)

    lateinit var billDataSource: BillDataSource
    private val account = AccountBuilder().build()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        App.resetDatabase()

        val accountDataSource = App.getApp().appComponent.accountDataSource()
        billDataSource = App.getApp().appComponent.billDataSource()

        val uiDevice = UiDevice.getInstance(getInstrumentation())
        if (!uiDevice.isScreenOn)
            uiDevice.wakeUp()

        accountDataSource.save(account).subscribe { assertTrue(it.isValid) }
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    @Throws(InterruptedException::class)
    fun add_new_expense() {
        mainActivityTestRule.launchActivity(Intent())

        UIHelper.openMenuAndClickItem(R.string.expenses)

        val expensesTitle = context.getString(R.string.expenses)
        UIHelper.matchToolbarTitle(expensesTitle)

        UIHelper.clickIntoView(R.id.view_expenses_fab)

        val newExpenseTitle = context.getString(R.string.new_expense_title)
        UIHelper.matchToolbarTitle(newExpenseTitle)

        val expenseName = "Test"
        UIHelper.clearAndTypeTextIntoView(R.id.act_edit_expense_name, expenseName)
        UIHelper.clearAndTypeTextIntoView(R.id.act_edit_expense_value, "100")
        UIHelper.clickIntoView(R.id.act_edit_expense_date)
        val time = DateTime.now().plusMonths(1)
        UIHelper.setTimeInDatePicker(time.year, time.monthOfYear, time.dayOfMonth)
        selectChargeable()

        onView(ViewMatchers.withId(R.id.act_edit_expense_date))
                .check(matches(ViewMatchers.withText(Transaction.SIMPLE_DATE_FORMAT.format(time.toDate()))))

        UIHelper.clickIntoView(R.id.action_save)

        UIHelper.matchToolbarTitle(expensesTitle)

        onView(ViewMatchers.withId(R.id.name)).check(matches(ViewMatchers.withText(expenseName)))
        onView(ViewMatchers.withId(R.id.billLayout)).check(matches(not<View>(ViewMatchers.isDisplayed())))
        onView(ViewMatchers.withId(R.id.date)).check(matches(ViewMatchers.withText(Transaction.SIMPLE_DATE_FORMAT.format(time.toDate()))))
    }

    @Test
    fun add_new_expense_shows_error_with_empty_name() {
        editExpenseActivityTestRule.launchActivity(Intent())

        val newExpenseTitle = context.getString(R.string.new_expense_title)
        UIHelper.matchToolbarTitle(newExpenseTitle)

        UIHelper.clickIntoView(R.id.action_save)

        val errorMessage = context.getString(R.string.error_message_name_not_informed)
        UIHelper.matchErrorMessage(R.id.act_edit_expense_name, errorMessage)
    }

    @Test
    fun add_new_expense_shows_error_without_value() {
        editExpenseActivityTestRule.launchActivity(Intent())

        val newExpenseTitle = context.getString(R.string.new_expense_title)
        UIHelper.matchToolbarTitle(newExpenseTitle)

        UIHelper.clickIntoView(R.id.action_save)

        val errorMessage = context.getString(R.string.error_message_amount_zero)
        UIHelper.matchErrorMessage(R.id.act_edit_expense_value, errorMessage)
    }

    @Test
    fun add_new_expense_shows_error_with_empty_chargeable() {
        editExpenseActivityTestRule.launchActivity(Intent())

        val newExpenseTitle = context.getString(R.string.new_expense_title)
        UIHelper.matchToolbarTitle(newExpenseTitle)

        UIHelper.clickIntoView(R.id.action_save)

        val errorMessage = context.getString(R.string.error_message_chargeable_not_informed)
        UIHelper.matchErrorMessage(R.id.act_edit_expense_chargeable, errorMessage)
    }

    @Test
    @Throws(Exception::class)
    fun add_new_expense_with_bill() {
        val bill = BillBuilder().build()
        assertTrue(billDataSource.save(bill).blockingFirst().isValid)

        mainActivityTestRule.launchActivity(Intent())

        UIHelper.openMenuAndClickItem(R.string.expenses)

        val expensesTitle = context.getString(R.string.expenses)
        UIHelper.matchToolbarTitle(expensesTitle)

        UIHelper.clickIntoView(R.id.view_expenses_fab)

        val newExpenseTitle = context.getString(R.string.new_expense_title)
        UIHelper.matchToolbarTitle(newExpenseTitle)

        Thread.sleep(100)

        selectBill(bill)
        selectChargeable()

        UIHelper.clickIntoView(R.id.action_save)

        clickIntoView(context.getString(R.string.yes))

        UIHelper.matchToolbarTitle(expensesTitle)

        onView(ViewMatchers.withId(R.id.name)).check(matches(ViewMatchers.withText(bill.name)))
        onView(ViewMatchers.withId(R.id.billLayout)).check(matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.bill)).check(matches(ViewMatchers.withText(bill.name)))
    }

    @Test
    @Throws(InterruptedException::class)
    fun add_new_expense_with_reimburse() {
        mainActivityTestRule.launchActivity(Intent())

        UIHelper.openMenuAndClickItem(R.string.expenses)

        val expensesTitle = context.getString(R.string.expenses)
        UIHelper.matchToolbarTitle(expensesTitle)

        UIHelper.clickIntoView(R.id.view_expenses_fab)

        val newExpenseTitle = context.getString(R.string.new_expense_title)
        UIHelper.matchToolbarTitle(newExpenseTitle)

        val expenseName = "Test"
        val value = 100
        UIHelper.clearAndTypeTextIntoView(R.id.act_edit_expense_name, expenseName)
        UIHelper.clearAndTypeTextIntoView(R.id.act_edit_expense_value, value.toString())
        UIHelper.clickIntoView(R.id.act_edit_expense_repayment)
        selectChargeable()

        UIHelper.clickIntoView(R.id.action_save)

        clickIntoView(context.getString(R.string.yes))

        UIHelper.matchToolbarTitle(expensesTitle)

        onView(ViewMatchers.withId(R.id.name)).check(matches(ViewMatchers.withText(expenseName)))
        val expectedValue = (value * -1).toCurrencyFormatted()
        onView(ViewMatchers.withId(R.id.value)).check(matches(ViewMatchers.withText(expectedValue)))
    }

    private fun selectChargeable() {
        val selectChargeableTitle = context.getString(R.string.select_chargeable_title)
        UIHelper.clickIntoView(R.id.act_edit_expense_chargeable)
        UIHelper.matchToolbarTitle(selectChargeableTitle)
        UIHelper.clickIntoView(account!!.name)
    }

    private fun selectBill(bill: Bill) {
        val title = context.getString(R.string.select_bill_title)
        UIHelper.clickIntoView(R.id.act_edit_expense_bill)
        UIHelper.matchToolbarTitle(title)
        UIHelper.clickIntoView(bill.name)
    }

    private val context: Context
        get() = InstrumentationRegistry.getTargetContext()
}
