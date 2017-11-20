package br.com.jonathanzanella.myexpenses.ui.account

import android.content.Intent
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.account.ShowAccountActivity
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.ui.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.*
import junit.framework.Assert.assertTrue
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class EditAccountTest {
    @Rule @JvmField
    var activityTestRule = ActivityTestRule(ShowAccountActivity::class.java, true, false)

    internal lateinit var dataSource: AccountDataSource
    private lateinit var account: Account

    @Before
    @Throws(Exception::class)
    fun setUp() {
        App.resetDatabase()

        dataSource = App.getApp().appComponent.accountDataSource()

        account = AccountBuilder().build()
        assertTrue(dataSource.save(account).blockingFirst().isValid)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    @Throws(Exception::class)
    fun edit_account_correctly() {
        val i = Intent()
        i.putExtra(br.com.jonathanzanella.myexpenses.account.ShowAccountActivity.KEY_ACCOUNT_UUID, account.uuid)
        activityTestRule.launchActivity(i)

        val showAccountTitle = getTargetContext().getString(R.string.account) + " " + account.name
        matchToolbarTitle(showAccountTitle)

        clickIntoView(R.id.action_edit)

        val editAccountTitle = getTargetContext().getString(R.string.edit_account_title)
        matchToolbarTitle(editAccountTitle)
        onView(withId(R.id.act_edit_account_name)).check(matches(withText(account.name)))
        onView(withId(R.id.act_edit_account_show_in_resume)).check(matches(isChecked()))
        clickIntoView(R.id.act_edit_account_show_in_resume)
        clearAndTypeTextIntoView(R.id.act_edit_account_name, account.name!! + " changed")

        clickIntoView(R.id.action_save)

        matchToolbarTitle(showAccountTitle + " changed")

        account = dataSource!!.find(account.uuid!!).blockingFirst()

        onView(withId(R.id.act_show_account_name)).check(matches(withText(account.name)))
        assertThat(dataSource.all().blockingFirst().size, `is`(1))
        assertThat(account.showInResume, `is`(false))
    }
}
