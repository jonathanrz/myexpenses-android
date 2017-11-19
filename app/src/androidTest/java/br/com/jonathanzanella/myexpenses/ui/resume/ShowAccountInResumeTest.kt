package br.com.jonathanzanella.myexpenses.ui.resume

import android.content.Intent
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
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
import br.com.jonathanzanella.myexpenses.views.MainActivity
import junit.framework.Assert.assertTrue
import org.hamcrest.Matchers.allOf
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ShowAccountInResumeTest {
    @Rule @JvmField
    var mainActivityTestRule = ActivityTestRule(MainActivity::class.java)
    internal lateinit var accountDataSource: AccountDataSource

    @Before
    @Throws(Exception::class)
    fun setUp() {
        App.resetDatabase()

        accountDataSource = App.getApp().appComponent.accountDataSource()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    fun show_only_account_marked_to_show() {
        val accountToShow = AccountBuilder().name("accountToShow").showInResume(true).build()
        assertTrue(accountDataSource.save(accountToShow).blockingFirst().isValid)
        val accountToHide = AccountBuilder().name("accountToHide").showInResume(false).build()
        assertTrue(accountDataSource.save(accountToHide).blockingFirst().isValid)

        mainActivityTestRule.launchActivity(Intent())

        getAccountNameView(accountToShow).check(matches(isDisplayed()))
        getAccountNameView(accountToHide).check(doesNotExist())
    }

    private fun getAccountNameView(account: Account): ViewInteraction {
        return onView(allOf<View>(
                withId(R.id.row_account_name),
                allOf<View>(
                        isDescendantOfA(withTagValue(`is`(account.uuid as Any?)))),
                allOf<View>(
                        withText(account.name),
                        isDisplayed())))
    }
}
