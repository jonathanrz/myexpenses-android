package br.com.jonathanzanella.myexpenses.card

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
import br.com.jonathanzanella.TestApp
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseDataSource
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView
import br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.CardBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import junit.framework.Assert.assertTrue
import org.joda.time.DateTime
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
class ShowCardActivityTest {
    @Rule @JvmField
    var activityTestRule = ActivityTestRule(ShowCardActivity::class.java, true, false)

    @Inject
    lateinit var expenseDataSource: ExpenseDataSource
    @Inject
    lateinit var dataSource: CardDataSource
    @Inject
    lateinit var accountDataSource: AccountDataSource

    private var card: Card? = null
    private var account: Account? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        TestApp.getTestComponent().inject(this)
        App.resetDatabase()

        account = AccountBuilder().build()
        accountDataSource.save(account!!)

        card = CardBuilder().account(account).type(CardType.CREDIT).build(accountDataSource)
        dataSource.save(card!!)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    @Throws(Exception::class)
    fun shows_account_correctly() {
        callActivity()

        val editCardTitle = getTargetContext().getString(R.string.card) + " " + card!!.name
        matchToolbarTitle(editCardTitle)

        onView(withId(R.id.act_show_card_name)).check(matches(withText(card!!.name)))
        onView(withId(R.id.act_show_card_account)).check(matches(withText(account!!.name)))
    }

    private fun callActivity() {
        val i = Intent()
        i.putExtra(ShowCardActivity.KEY_CREDIT_CARD_UUID, card!!.uuid)
        activityTestRule.launchActivity(i)
    }

    @Test
    @Throws(Exception::class)
    fun generate_and_pay_credit_card_bill() {
        val date = DateTime.now().minusMonths(1)
        var expense1: Expense? = ExpenseBuilder().chargeable(card).date(date).build()
        assertTrue(expenseDataSource.save(expense1!!).isValid)
        var expense2: Expense? = ExpenseBuilder().chargeable(card).date(date).build()
        assertTrue(expenseDataSource.save(expense2!!).isValid)

        callActivity()

        clickIntoView(R.id.act_show_card_pay_credit_card_bill)

        val editExpenseTitle = getTargetContext().getString(R.string.edit_expense_title)
        matchToolbarTitle(editExpenseTitle)

        val cardBillName = getTargetContext().getString(R.string.invoice) + " " + card!!.name
        onView(withId(R.id.act_edit_expense_name)).check(matches(withText(cardBillName)))
        val cardBillValue = (expense1.value + expense2.value).toCurrencyFormatted()
        onView(withId(R.id.act_edit_expense_value)).check(matches(withText(cardBillValue)))
        onView(withId(R.id.act_edit_expense_chargeable)).check(matches(withText(card!!.account!!.name)))

        expense1 = expenseDataSource.find(expense1.uuid!!)
        assertTrue(expense1!!.charged)
        expense2 = expenseDataSource.find(expense1.uuid!!)
        assertTrue(expense2!!.charged)
    }
}