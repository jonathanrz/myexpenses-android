package br.com.jonathanzanella.myexpenses.expense

import android.content.Context
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import android.view.View
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountRepository
import br.com.jonathanzanella.myexpenses.bill.Bill
import br.com.jonathanzanella.myexpenses.bill.BillRepository
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.helpers.UIHelper.*
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.BillBuilder
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import br.com.jonathanzanella.myexpenses.views.MainActivity
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

    private var account: Account? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MyApplication.resetDatabase()

        val uiDevice = UiDevice.getInstance(getInstrumentation())
        if (!uiDevice.isScreenOn)
            uiDevice.wakeUp()

        account = AccountBuilder().build()
        AccountRepository().save(account!!)
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

        openMenuAndClickItem(R.string.expenses)

        val expensesTitle = context.getString(R.string.expenses)
        matchToolbarTitle(expensesTitle)

        clickIntoView(R.id.view_expenses_fab)

        val newExpenseTitle = context.getString(R.string.new_expense_title)
        matchToolbarTitle(newExpenseTitle)

        val expenseName = "Test"
        typeTextIntoView(R.id.act_edit_expense_name, expenseName)
        typeTextIntoView(R.id.act_edit_expense_value, "100")
        clickIntoView(R.id.act_edit_expense_value_to_show_in_overview)
        clickIntoView(R.id.act_edit_expense_date)
        val time = DateTime.now().plusMonths(1)
        setTimeInDatePicker(time.year, time.monthOfYear, time.dayOfMonth)
        selectChargeable()

        onView(withId(R.id.act_edit_expense_date))
                .check(matches(withText(Transaction.SIMPLE_DATE_FORMAT.format(time.toDate()))))

        clickIntoView(R.id.action_save)

        matchToolbarTitle(expensesTitle)

        onView(withId(R.id.name)).check(matches(withText(expenseName)))
        onView(withId(R.id.billLayout)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.date)).check(matches(withText(Transaction.SIMPLE_DATE_FORMAT.format(time.toDate()))))
    }

    @Test
    fun add_new_expense_shows_error_with_empty_name() {
        editExpenseActivityTestRule.launchActivity(Intent())

        val newExpenseTitle = context.getString(R.string.new_expense_title)
        matchToolbarTitle(newExpenseTitle)

        clickIntoView(R.id.action_save)

        val errorMessage = context.getString(R.string.error_message_name_not_informed)
        matchErrorMessage(R.id.act_edit_expense_name, errorMessage)
    }

    @Test
    fun add_new_expense_shows_error_without_value() {
        editExpenseActivityTestRule.launchActivity(Intent())

        val newExpenseTitle = context.getString(R.string.new_expense_title)
        matchToolbarTitle(newExpenseTitle)

        clickIntoView(R.id.action_save)

        val errorMessage = context.getString(R.string.error_message_amount_zero)
        matchErrorMessage(R.id.act_edit_expense_value, errorMessage)
    }

    @Test
    fun add_new_expense_shows_error_with_empty_chargeable() {
        editExpenseActivityTestRule.launchActivity(Intent())

        val newExpenseTitle = context.getString(R.string.new_expense_title)
        matchToolbarTitle(newExpenseTitle)

        clickIntoView(R.id.action_save)

        val errorMessage = context.getString(R.string.error_message_chargeable_not_informed)
        matchErrorMessage(R.id.act_edit_expense_chargeable, errorMessage)
    }

    @Test
    @Throws(Exception::class)
    fun add_new_expense_with_bill() {
        val bill = BillBuilder().build()
        val expenseRepository = ExpenseRepository()
        BillRepository(expenseRepository, MyApplication.database.billDao()).save(bill)

        mainActivityTestRule.launchActivity(Intent())

        openMenuAndClickItem(R.string.expenses)

        val expensesTitle = context.getString(R.string.expenses)
        matchToolbarTitle(expensesTitle)

        clickIntoView(R.id.view_expenses_fab)

        val newExpenseTitle = context.getString(R.string.new_expense_title)
        matchToolbarTitle(newExpenseTitle)

        selectBill(bill)
        selectChargeable()

        clickIntoView(R.id.action_save)

        matchToolbarTitle(expensesTitle)

        onView(withId(R.id.name)).check(matches(withText(bill.name)))
        onView(withId(R.id.billLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.bill)).check(matches(withText(bill.name)))
    }

    @Test
    @Throws(InterruptedException::class)
    fun add_new_expense_with_reimburse() {
        mainActivityTestRule.launchActivity(Intent())

        openMenuAndClickItem(R.string.expenses)

        val expensesTitle = context.getString(R.string.expenses)
        matchToolbarTitle(expensesTitle)

        clickIntoView(R.id.view_expenses_fab)

        val newExpenseTitle = context.getString(R.string.new_expense_title)
        matchToolbarTitle(newExpenseTitle)

        val expenseName = "Test"
        val value = 100
        clearAndTypeTextIntoView(R.id.act_edit_expense_name, expenseName)
        clearAndTypeTextIntoView(R.id.act_edit_expense_value, value.toString())
        clickIntoView(R.id.act_edit_expense_repayment)
        selectChargeable()

        clickIntoView(R.id.action_save)

        matchToolbarTitle(expensesTitle)

        onView(withId(R.id.name)).check(matches(withText(expenseName)))
        val expectedValue = (value * -1).toCurrencyFormatted()
        onView(withId(R.id.value)).check(matches(withText(expectedValue)))
    }

    private fun selectChargeable() {
        val selectChargeableTitle = context.getString(R.string.select_chargeable_title)
        clickIntoView(R.id.act_edit_expense_chargeable)
        matchToolbarTitle(selectChargeableTitle)
        clickIntoView(account!!.name)
    }

    private fun selectBill(bill: Bill) {
        val title = context.getString(R.string.select_bill_title)
        clickIntoView(R.id.act_edit_expense_bill)
        matchToolbarTitle(title)
        clickIntoView(bill.name)
    }

    private val context: Context
        get() = InstrumentationRegistry.getTargetContext()
}
