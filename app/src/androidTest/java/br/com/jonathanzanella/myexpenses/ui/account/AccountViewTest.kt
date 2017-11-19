package br.com.jonathanzanella.myexpenses.ui.account

import android.content.Intent
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.view.View
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.ui.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.openMenuAndClickItem
import br.com.jonathanzanella.myexpenses.views.MainActivity
import junit.framework.Assert.assertTrue
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AccountViewTest {
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)
    internal lateinit var dataSource: AccountDataSource

    private lateinit var accountToShowInResume: Account
    private lateinit var accountToHideInResume: Account

    @Before
    @Throws(Exception::class)
    fun setUp() {
        App.resetDatabase()

        dataSource = App.getApp().appComponent.accountDataSource()

        accountToShowInResume = AccountBuilder().name("accountToShowInResume").showInResume(true).build()
        accountToHideInResume = AccountBuilder().name("accountToHideInResume").showInResume(false).build()

        assertTrue(dataSource.save(accountToShowInResume).blockingFirst().isValid)
        assertTrue(dataSource.save(accountToHideInResume).blockingFirst().isValid)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    @Throws(Exception::class)
    fun list_all_accounts() {
        activityTestRule.launchActivity(Intent())

        openMenuAndClickItem(R.string.accounts)

        Thread.sleep(500)

        accountNameView(accountToShowInResume).check(matches(withText(accountToShowInResume.name)))
        accountNameView(accountToHideInResume).check(matches(withText(accountToHideInResume.name)))
    }

    private fun accountNameView(account: br.com.jonathanzanella.myexpenses.account.Account?): ViewInteraction {
        return onView(allOf<View>(
                withId(R.id.row_account_name),
                isDescendantOfA(withTagValue(`is`(account!!.uuid)))))
    }
}
