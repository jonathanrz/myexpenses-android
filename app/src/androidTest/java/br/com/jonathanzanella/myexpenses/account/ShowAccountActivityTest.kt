package br.com.jonathanzanella.myexpenses.account

import android.content.Intent
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import br.com.jonathanzanella.myexpenses.MyApplication
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

@RunWith(AndroidJUnit4::class)
@MediumTest
class ShowAccountActivityTest {
    @Rule
    var activityTestRule = ActivityTestRule(ShowAccountActivity::class.java, true, false)

    private var account: Account? = null
    private var repository: AccountRepository? = null
    private var expenseRepository: ExpenseRepository? = null
    private var receiptRepository: ReceiptRepository? = null
    private var sourceRepository: SourceRepository? = null
    private var cardRepository: CardRepository? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MyApplication.resetDatabase()

        repository = AccountRepository()
        receiptRepository = ReceiptRepository()
        expenseRepository = ExpenseRepository()
        cardRepository = CardRepository(expenseRepository!!)
        sourceRepository = SourceRepository()

        account = Account()
        account!!.name = "test"
        account!!.balance = ACCOUNT_BALANCE
        account!!.accountToPayCreditCard = true
        repository!!.save(account!!)
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
        onView(withId(R.id.act_show_account_name)).check(matches(withText(account!!.name)))
        onView(withId(R.id.act_show_account_balance)).check(matches(withText(balanceAsCurrency)))
    }

    @Test
    @Throws(InterruptedException::class)
    fun show_credit_card_bill_in_account_show_activity() {
        val card = CardBuilder().account(account).build(repository)
        assertTrue(cardRepository!!.save(card).isValid)
        val expense = ExpenseBuilder().chargeable(card).build()
        assertTrue(expenseRepository!!.save(expense).isValid)

        launchActivity()

        val billName = getTargetContext().getString(R.string.invoice) + " " + card.name
        val value = expense.amount.toCurrencyFormatted()

        Thread.sleep(500)
        onView(withId(R.id.act_show_account_name)).check(matches(withText(account!!.name)))
        onView(withId(R.id.name)).check(matches(withText(billName)))
        onView(withId(R.id.value)).check(matches(withText(value)))
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
                withId(R.id.balance),
                isDescendantOfA(withId(R.id.thisMonth))))
                .check(matches(withText(expectedValue)))

        expectedBalance = expectedBalance + RECEIPT_INCOME - EXPENSE_VALUE
        expectedValue = expectedBalance.toCurrencyFormatted()

        Thread.sleep(500)
        onView(allOf<View>(
                withId(R.id.balance),
                isDescendantOfA(withId(R.id.nextMonth))))
                .check(matches(withText(expectedValue)))
    }

    private fun launchActivity() {
        val i = Intent()
        i.putExtra(ShowAccountActivity.KEY_ACCOUNT_UUID, account!!.uuid)
        activityTestRule.launchActivity(i)
    }

    private fun generateTwoMonthsReceipts() {
        val s = SourceBuilder().build()
        sourceRepository!!.save(s)

        var receipt = ReceiptBuilder()
                .income(RECEIPT_INCOME)
                .date(DateTime.now())
                .account(account)
                .source(s)
                .build()
        assertTrue(receiptRepository!!.save(receipt).isValid)
        receipt = ReceiptBuilder()
                .income(RECEIPT_INCOME)
                .date(DateTime.now().plusMonths(1))
                .account(account)
                .source(s)
                .build()
        assertTrue(receiptRepository!!.save(receipt).isValid)
    }

    private fun generateTwoMonthsExpenses() {
        var expense = ExpenseBuilder()
                .value(EXPENSE_VALUE)
                .date(DateTime.now())
                .chargeable(account)
                .build()
        assertTrue(expenseRepository!!.save(expense).isValid)
        expense = ExpenseBuilder()
                .value(EXPENSE_VALUE)
                .date(DateTime.now().plusMonths(1))
                .chargeable(account)
                .build()
        assertTrue(expenseRepository!!.save(expense).isValid)
    }

    companion object {
        private val ACCOUNT_BALANCE = 115
        private val EXPENSE_VALUE = 25
        private val RECEIPT_INCOME = 35
    }
}