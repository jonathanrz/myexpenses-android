package br.com.jonathanzanella.myexpenses.ui.expense

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
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseDataSource
import br.com.jonathanzanella.myexpenses.expense.ShowExpenseActivity
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.ui.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.clickIntoView
import br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.matchToolbarTitle
import io.reactivex.disposables.Disposable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
class ShowExpenseActivityTest {
    @Rule @JvmField
    var activityTestRule = ActivityTestRule(ShowExpenseActivity::class.java, true, false)

    @Inject
    lateinit var dataSource: ExpenseDataSource
    @Inject
    lateinit var accountDataSource: AccountDataSource
    private lateinit var expense: Expense
    private lateinit var accountDisposable: Disposable

    @Before
    @Throws(Exception::class)
    fun setUp() {
        TestApp.getTestComponent().inject(this)
        App.resetDatabase()

        val a = AccountBuilder().build()
        accountDisposable = accountDataSource.save(a).subscribe { assert(it.isValid) }

        expense = ExpenseBuilder().chargeable(a).build()
        assert(dataSource.save(expense!!).isValid)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        accountDisposable.dispose()
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    @Throws(Exception::class)
    fun shows_expense_correctly() {
        val i = Intent()
        i.putExtra(ShowExpenseActivity.KEY_EXPENSE_UUID, expense.uuid)
        activityTestRule.launchActivity(i)

        val editExpenseTitle = getTargetContext().getString(R.string.expense) + " " + expense!!.name
        matchToolbarTitle(editExpenseTitle)

        val incomeAsCurrency = expense.value.toCurrencyFormatted()
        onView(withId(R.id.act_show_expense_name)).check(matches(withText(expense.name)))
        onView(withId(R.id.act_show_expense_value)).check(matches(withText(incomeAsCurrency)))
        val chargeable = expense.chargeableFromCache
        onView(withId(R.id.act_show_expense_chargeable)).check(matches(withText(chargeable!!.name)))
    }

    @Test
    @Throws(Exception::class)
    fun calls_edit_expense_activity() {
        val i = Intent()
        i.putExtra(ShowExpenseActivity.KEY_EXPENSE_UUID, expense.uuid)
        activityTestRule.launchActivity(i)

        val showExpenseTitle = getTargetContext().getString(R.string.expense) + " " + expense.name
        matchToolbarTitle(showExpenseTitle)

        clickIntoView(R.id.action_edit)

        val editExpenseTitle = getTargetContext().getString(R.string.edit_expense_title)
        matchToolbarTitle(editExpenseTitle)
    }
}