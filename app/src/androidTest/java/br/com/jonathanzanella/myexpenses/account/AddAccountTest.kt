package br.com.jonathanzanella.myexpenses.account

import android.content.Context
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.helpers.UIHelper
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.views.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class AddAccountTest {
    @Rule @JvmField
    val activityTestRule = ActivityTestRule(MainActivity::class.java)
    @Rule @JvmField
    val editAccountActivityTestRule = ActivityTestRule(EditAccountActivity::class.java)

    @Before
    fun setUp() {
        App.resetDatabase()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    fun add_new_account() {
        activityTestRule.launchActivity(Intent())

        UIHelper.openMenuAndClickItem(R.string.accounts)

        val accountsTitle = context.getString(R.string.accounts)
        UIHelper.matchToolbarTitle(accountsTitle)

        UIHelper.clickIntoView(R.id.view_accounts_fab)

        val newAccountTitle = context.getString(R.string.new_account_title)
        UIHelper.matchToolbarTitle(newAccountTitle)

        val accountTitle = "Test"
        UIHelper.typeTextIntoView(R.id.act_edit_account_name, accountTitle)
        UIHelper.clickIntoView(R.id.action_save)

        UIHelper.matchToolbarTitle(accountsTitle)

        onView(withId(R.id.row_account_name)).check(matches(withText(accountTitle)))
        val balance = 0.toCurrencyFormatted()
        onView(withId(R.id.row_account_balance)).check(matches(withText(balance)))
    }

    @Test
    fun add_new_account_shows_error_with_empty_name() {
        editAccountActivityTestRule.launchActivity(Intent())

        val newAccountTitle = context.getString(R.string.new_account_title)
        UIHelper.matchToolbarTitle(newAccountTitle)

        UIHelper.clickIntoView(R.id.action_save)

        val errorMessage = context.getString(R.string.error_message_name_not_informed)
        UIHelper.matchErrorMessage(R.id.act_edit_account_name, errorMessage)
    }

    private val context: Context
        get() = InstrumentationRegistry.getTargetContext()
}
