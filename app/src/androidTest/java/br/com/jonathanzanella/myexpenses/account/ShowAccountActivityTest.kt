package br.com.jonathanzanella.myexpenses.account

import android.content.Intent
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import br.com.jonathanzanella.TestApp
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.card.CardRepository
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle
import br.com.jonathanzanella.myexpenses.helpers.builder.CardBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository
import br.com.jonathanzanella.myexpenses.source.SourceRepository
import junit.framework.Assert.assertTrue
import org.hamcrest.core.AllOf.allOf
import org.joda.time.DateTime
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@RunWith(AndroidJUnit4::class)
@MediumTest
class ShowAccountActivityTest {
    @Rule @JvmField
    var activityTestRule = ActivityTestRule(ShowAccountActivity::class.java, true, false)

    private var account: Account? = null
    @Inject
    lateinit var dataSource: AccountDataSource
    @Inject
    lateinit var expenseRepository: ExpenseRepository
    @Inject
    lateinit var receiptRepository: ReceiptRepository
    @Inject
    lateinit var sourceRepository: SourceRepository
    @Inject
    lateinit var cardRepository: CardRepository

    @Before
    @Throws(Exception::class)
    fun setUp() {
        TestApp.getTestComponent().inject(this)

        App.resetDatabase()

        account = Account()
        account!!.name = "test"
        account!!.balance = ACCOUNT_BALANCE
        account!!.accountToPayCreditCard = true
        dataSource.save(account!!)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    @Throws(Exception::class)
    fun shows_account_correctly() {
        launchActivity()

        val editAccountTitle = getTargetContext().getString(R.string.account) + " " + account!!.name
        matchToolbarTitle(editAccountTitle)

        val balanceAsCurrency = account!!.balance.toCurrencyFormatted()
        onView(ViewMatchers.withId(R.id.act_show_account_name)).check(matches(ViewMatchers.withText(account!!.name)))
        onView(ViewMatchers.withId(R.id.act_show_account_balance)).check(matches(ViewMatchers.withText(balanceAsCurrency)))
    }

    @Test
    @Throws(InterruptedException::class)
    fun show_credit_card_bill_in_account_show_activity() {
        val card = CardBuilder().account(account).build(dataSource)
        assertTrue(cardRepository.save(card).isValid)
        val expense = ExpenseBuilder().chargeable(card).build()
        assertTrue(expenseRepository.save(expense).isValid)

        launchActivity()

        val billName = getTargetContext().getString(R.string.invoice) + " " + card.name
        val value = expense.amount.toCurrencyFormatted()

        Thread.sleep(500)
        onView(ViewMatchers.withId(R.id.act_show_account_name)).check(matches(ViewMatchers.withText(account!!.name)))
        onView(ViewMatchers.withId(R.id.name)).check(matches(ViewMatchers.withText(billName)))
        onView(ViewMatchers.withId(R.id.value)).check(matches(ViewMatchers.withText(value)))
    }

    @Test
    @Throws(Exception::class)
    fun calculate_account_balance_correctly() {
        generateTwoMonthsExpenses()
        generateTwoMonthsReceipts()

        launchActivity()

        var expectedBalance = ACCOUNT_BALANCE + RECEIPT_INCOME - EXPENSE_VALUE
        var expectedValue = expectedBalance.toCurrencyFormatted()
        Thread.sleep(500)
        onView(allOf<View>(
                ViewMatchers.withId(R.id.balance),
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.thisMonth))))
                .check(matches(ViewMatchers.withText(expectedValue)))

        expectedBalance = expectedBalance + RECEIPT_INCOME - EXPENSE_VALUE
        expectedValue = expectedBalance.toCurrencyFormatted()

        Thread.sleep(500)
        onView(allOf<View>(
                ViewMatchers.withId(R.id.balance),
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.nextMonth))))
                .check(matches(ViewMatchers.withText(expectedValue)))
    }

    private fun launchActivity() {
        val i = Intent()
        i.putExtra(ShowAccountActivity.KEY_ACCOUNT_UUID, account!!.uuid)
        activityTestRule.launchActivity(i)
    }

    private fun generateTwoMonthsReceipts() {
        val s = SourceBuilder().build()
        sourceRepository.save(s)

        var receipt = ReceiptBuilder()
                .income(RECEIPT_INCOME)
                .date(DateTime.now())
                .account(account)
                .source(s)
                .build()
        assertTrue(receiptRepository.save(receipt).isValid)
        receipt = ReceiptBuilder()
                .income(RECEIPT_INCOME)
                .date(DateTime.now().plusMonths(1))
                .account(account)
                .source(s)
                .build()
        assertTrue(receiptRepository.save(receipt).isValid)
    }

    private fun generateTwoMonthsExpenses() {
        var expense = ExpenseBuilder()
                .value(EXPENSE_VALUE)
                .date(DateTime.now())
                .chargeable(account)
                .build()
        assertTrue(expenseRepository.save(expense).isValid)
        expense = ExpenseBuilder()
                .value(EXPENSE_VALUE)
                .date(DateTime.now().plusMonths(1))
                .chargeable(account)
                .build()
        assertTrue(expenseRepository.save(expense).isValid)
    }

    companion object {
        private val ACCOUNT_BALANCE = 115
        private val EXPENSE_VALUE = 25
        private val RECEIPT_INCOME = 35
    }
}