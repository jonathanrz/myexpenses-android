package br.com.jonathanzanella.myexpenses.resume

import android.content.Intent
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.filters.SmallTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import br.com.jonathanzanella.TestApp
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.bill.BillDataSource
import br.com.jonathanzanella.myexpenses.expense.ExpenseDataSource
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView
import br.com.jonathanzanella.myexpenses.helpers.builder.BillBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.receipt.ReceiptDataSource
import br.com.jonathanzanella.myexpenses.source.Source
import br.com.jonathanzanella.myexpenses.source.SourceRepository
import br.com.jonathanzanella.myexpenses.views.MainActivity
import junit.framework.Assert.assertTrue
import org.hamcrest.Matchers.allOf
import org.joda.time.DateTime
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@SmallTest
class CalculateMonthBalanceCorrectlyTest {

    @Rule @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Inject
    lateinit var billDataSource: BillDataSource
    @Inject
    lateinit var sourceRepository: SourceRepository
    @Inject
    lateinit var accountDataSource: AccountDataSource
    @Inject
    lateinit var receiptDataSource: ReceiptDataSource
    @Inject
    lateinit var expenseDataSource: ExpenseDataSource

    private val monthlyPagerAdapterHelper = MonthlyPagerAdapterHelper()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        TestApp.getTestComponent().inject(this)
        App.resetDatabase()

        val a = br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder().build()
        assertTrue(accountDataSource.save(a).isValid)

        val s = SourceBuilder().build()
        assertTrue(sourceRepository.save(s).isValid)

        val now = DateTime.now().withDayOfMonth(1)
        val b = BillBuilder()
                .initDate(now)
                .endDate(now.plusMonths(12))
                .amount(BILL_AMOUNT)
                .build()
        assertTrue(billDataSource.save(b).isValid)

        generateThreeMonthlyReceipts(a, s)
        generateThreeMonthlyExpenses(a)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    private fun generateThreeMonthlyReceipts(a: Account, s: Source) {
        var dateTime = DateTime.now()
        var r = ReceiptBuilder()
                .account(a)
                .source(s)
                .date(dateTime)
                .income(RECEIPT_INCOME)
                .build()
        assertTrue(receiptDataSource.save(r).isValid)
        dateTime = dateTime.plusMonths(1)
        r = ReceiptBuilder()
                .account(a)
                .source(s)
                .date(dateTime)
                .income(RECEIPT_INCOME * 2)
                .build()
        assertTrue(receiptDataSource.save(r).isValid)
        dateTime = dateTime.plusMonths(1)
        r = ReceiptBuilder()
                .account(a)
                .source(s)
                .date(dateTime)
                .income(RECEIPT_INCOME * 3)
                .build()
        assertTrue(receiptDataSource.save(r).isValid)
    }

    private fun generateThreeMonthlyExpenses(a: Account) {
        var dateTime = DateTime.now()
        var r = ExpenseBuilder()
                .chargeable(a)
                .date(dateTime)
                .value(EXPENSE_VALUE)
                .build()
        assertTrue(expenseDataSource.save(r).isValid)
        dateTime = dateTime.plusMonths(1)
        r = ExpenseBuilder()
                .chargeable(a)
                .date(dateTime)
                .value(EXPENSE_VALUE * 2)
                .build()
        assertTrue(expenseDataSource.save(r).isValid)
        dateTime = dateTime.plusMonths(1)
        r = ExpenseBuilder()
                .chargeable(a)
                .date(dateTime)
                .value(EXPENSE_VALUE * 3)
                .build()
        assertTrue(expenseDataSource.save(r).isValid)
    }

    @Test
    @Throws(Exception::class)
    fun verify_month_balance() {
        activityTestRule.launchActivity(Intent())

        val balance = RECEIPT_INCOME - EXPENSE_VALUE - BILL_AMOUNT
        val twoMonthsBalance = RECEIPT_INCOME * 3 - EXPENSE_VALUE * 3 - BILL_AMOUNT
        val expectedBalance = balance.toCurrencyFormatted()
        val twoMonthsExpectedBalance = twoMonthsBalance.toCurrencyFormatted()

        validateExpectedBalance(expectedBalance)

        scrollToMonth(DateTime.now().plusMonths(2))
        validateExpectedBalance(twoMonthsExpectedBalance)

        scrollToMonth(DateTime.now())
        validateExpectedBalance(expectedBalance)
    }

    private fun validateExpectedBalance(expectedBalance: String) {
        onView(allOf<View>(
                ViewMatchers.withId(R.id.balance),
                ViewMatchers.isDescendantOfA(allOf<View>(
                        ViewMatchers.withId(R.id.view_monthly_resume),
                        ViewMatchers.isDisplayed()))))
                .check(matches(ViewMatchers.withText(expectedBalance)))
    }

    private fun scrollToMonth(dateTime: DateTime) {
        val twoMonthsFromNow = monthlyPagerAdapterHelper.formatMonthForView(dateTime)
        clickIntoView(twoMonthsFromNow)
    }

    companion object {
        private val EXPENSE_VALUE = 100
        private val RECEIPT_INCOME = 200
        private val BILL_AMOUNT = 25
    }
}
