package br.com.jonathanzanella.myexpenses.ui.resume

import android.content.Intent
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.view.View
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.expense.ExpenseDataSource
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder
import br.com.jonathanzanella.myexpenses.receipt.ReceiptDataSource
import br.com.jonathanzanella.myexpenses.source.SourceDataSource
import br.com.jonathanzanella.myexpenses.ui.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.matchToolbarTitle
import br.com.jonathanzanella.myexpenses.views.MainActivity
import junit.framework.Assert.assertTrue
import org.hamcrest.Matchers.allOf
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ShowDetailScreenTest {
    @Rule @JvmField
    var mainActivityTestRule = ActivityTestRule(MainActivity::class.java)
    lateinit var accountDataSource: AccountDataSource
    lateinit var receiptDataSource: ReceiptDataSource
    lateinit var sourceDataSource: SourceDataSource
    lateinit var expenseDataSource: ExpenseDataSource

    private var account: Account? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        App.resetDatabase()

        accountDataSource = App.getApp().appComponent.accountDataSource()
        receiptDataSource = App.getApp().appComponent.receiptDataSource()
        sourceDataSource = App.getApp().appComponent.sourceDataSource()
        expenseDataSource = App.getApp().appComponent.expenseDataSource()

        account = AccountBuilder().build()
        assertTrue(accountDataSource.save(account!!).blockingFirst().isValid)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    fun open_receipt_screen_when_selecting_receipt() {
        val source = SourceBuilder().build()
        assertTrue(sourceDataSource!!.save(source).isValid)
        val receipt = ReceiptBuilder().account(account).source(source).build()
        assertTrue(receiptDataSource!!.save(receipt).isValid)

        mainActivityTestRule.launchActivity(Intent())

        onView(allOf<View>(withId(R.id.name),
                isDescendantOfA(withTagValue(`is`(receipt.uuid)))))
                .perform(scrollTo()).perform(click())

        val showReceiptTitle = getTargetContext().getString(R.string.receipt) + " " + receipt.name
        matchToolbarTitle(showReceiptTitle)

        onView(withId(R.id.act_show_receipt_name)).check(matches(withText(receipt.name)))
        onView(withId(R.id.act_show_receipt_account)).check(matches(withText(account!!.name)))
    }

    @Test
    fun open_expense_screen_when_selecting_expense() {
        val expense = ExpenseBuilder().chargeable(account).build()
        assertTrue(expenseDataSource!!.save(expense).isValid)

        mainActivityTestRule.launchActivity(Intent())

        onView(allOf<View>(withId(R.id.name),
                isDescendantOfA(withTagValue(`is`(expense.uuid)))))
                .perform(scrollTo()).perform(click())

        val showExpenseTitle = getTargetContext().getString(R.string.expense) + " " + expense.name
        matchToolbarTitle(showExpenseTitle)

        onView(withId(R.id.act_show_expense_name)).check(matches(withText(expense.name)))
        onView(withId(R.id.act_show_expense_chargeable)).check(matches(withText(account!!.name)))
    }
}

