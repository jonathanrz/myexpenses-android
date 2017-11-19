package br.com.jonathanzanella.myexpenses.ui.source

import android.content.Intent
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
import br.com.jonathanzanella.myexpenses.source.ShowSourceActivity
import br.com.jonathanzanella.myexpenses.source.Source
import br.com.jonathanzanella.myexpenses.source.SourceDataSource
import br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.matchToolbarTitle
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
class ShowSourceActivityTest {
    @Rule @JvmField
    var activityTestRule = ActivityTestRule(ShowSourceActivity::class.java, true, false)

    @Inject
    internal lateinit var dataSource: SourceDataSource
    private lateinit var source: Source

    @Before
    @Throws(Exception::class)
    fun setUp() {
        TestApp.getTestComponent().inject(this)
        App.resetDatabase()

        source = Source()
        source.name = "test"
        dataSource.save(source)
    }

    @Test
    @Throws(Exception::class)
    fun shows_account_correctly() {
        val i = Intent()
        i.putExtra(ShowSourceActivity.KEY_SOURCE_UUID, source.uuid)
        activityTestRule.launchActivity(i)

        val editSourceTitle = getTargetContext().getString(R.string.source) + " " + source.name
        matchToolbarTitle(editSourceTitle)

        onView(withId(R.id.act_show_source_name)).check(matches(withText(source.name)))
    }
}