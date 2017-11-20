package br.com.jonathanzanella.myexpenses.ui.source

import android.content.Context
import android.content.Intent
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.source.EditSourceActivity
import br.com.jonathanzanella.myexpenses.ui.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.*
import br.com.jonathanzanella.myexpenses.views.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddSourceTest {
    @Rule @JvmField
    var mainActivityTestRule = ActivityTestRule(MainActivity::class.java)
    @Rule @JvmField
    var editSourceActivityTestRule = ActivityTestRule(EditSourceActivity::class.java)

    private val context: Context
        get() = getTargetContext()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        App.resetDatabase()

        val uiDevice = UiDevice.getInstance(getInstrumentation())
        if (!uiDevice.isScreenOn)
            uiDevice.wakeUp()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    fun add_new_source() {
        mainActivityTestRule.launchActivity(Intent())

        openMenuAndClickItem(R.string.sources)

        val sourcesTitle = context.getString(R.string.sources)
        matchToolbarTitle(sourcesTitle)

        clickIntoView(R.id.view_sources_fab)

        val newSourceTitle = context.getString(R.string.new_source_title)
        matchToolbarTitle(newSourceTitle)

        val sourceTitle = "Test"
        clearAndTypeTextIntoView(R.id.act_edit_source_name, sourceTitle)
        clickIntoView(R.id.action_save)

        matchToolbarTitle(sourcesTitle)

        onView(withId(R.id.row_source_name)).check(matches(withText(sourceTitle)))
    }

    @Test
    fun add_new_source_shows_error_with_empty_name() {
        editSourceActivityTestRule.launchActivity(Intent())

        val newSourceTitle = context.getString(R.string.new_source_title)
        matchToolbarTitle(newSourceTitle)

        clickIntoView(R.id.action_save)

        val errorMessage = context.getString(R.string.error_message_name_not_informed)
        matchErrorMessage(R.id.act_edit_source_name, errorMessage)
    }
}
