package br.com.jonathanzanella.myexpenses.resume

import android.content.Intent
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.SmallTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountRepository
import br.com.jonathanzanella.myexpenses.bill.BillRepository
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView
import br.com.jonathanzanella.myexpenses.helpers.builder.*
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository
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

@RunWith(AndroidJUnit4::class)
@SmallTest
class CalculateMonthBalanceCorrectlyTest {

    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    private val monthlyPagerAdapterHelper = MonthlyPagerAdapterHelper()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MyApplication.resetDatabase()

        val a = AccountBuilder().build()
        assertTrue(AccountRepository().save(a).isValid)

        val s = SourceBuilder().build()
        assertTrue(SourceRepository().save(s).isValid)

        val now = DateTime.now().withDayOfMonth(1)
        val b = BillBuilder()
                .initDate(now)
                .endDate(now.plusMonths(12))
                .amount(BILL_AMOUNT)
                .build()
        assertTrue(BillRepository(ExpenseRepository(), MyApplication.database.billDao()).save(b).isValid)

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
        val receiptRepository = ReceiptRepository()
        var r = ReceiptBuilder()
                .account(a)
                .source(s)
                .date(dateTime)
                .income(RECEIPT_INCOME)
                .build()
        assertTrue(receiptRepository.save(r).isValid)
        dateTime = dateTime.plusMonths(1)
        r = ReceiptBuilder()
                .account(a)
                .source(s)
                .date(dateTime)
                .income(RECEIPT_INCOME * 2)
                .build()
        assertTrue(receiptRepository.save(r).isValid)
        dateTime = dateTime.plusMonths(1)
        r = ReceiptBuilder()
                .account(a)
                .source(s)
                .date(dateTime)
                .income(RECEIPT_INCOME * 3)
                .build()
        assertTrue(receiptRepository.save(r).isValid)
    }

    private fun generateThreeMonthlyExpenses(a: Account) {
        var dateTime = DateTime.now()
        val expenseRepository = ExpenseRepository()
        var r = ExpenseBuilder()
                .chargeable(a)
                .date(dateTime)
                .value(EXPENSE_VALUE)
                .build()
        assertTrue(expenseRepository.save(r).isValid)
        dateTime = dateTime.plusMonths(1)
        r = ExpenseBuilder()
                .chargeable(a)
                .date(dateTime)
                .value(EXPENSE_VALUE * 2)
                .build()
        assertTrue(expenseRepository.save(r).isValid)
        dateTime = dateTime.plusMonths(1)
        r = ExpenseBuilder()
                .chargeable(a)
                .date(dateTime)
                .value(EXPENSE_VALUE * 3)
                .build()
        assertTrue(expenseRepository.save(r).isValid)
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
                withId(R.id.balance),
                isDescendantOfA(allOf<View>(
                        withId(R.id.view_monthly_resume),
                        isDisplayed()))))
                .check(matches(withText(expectedBalance)))
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
