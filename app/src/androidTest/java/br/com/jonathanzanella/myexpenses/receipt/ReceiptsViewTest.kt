package br.com.jonathanzanella.myexpenses.receipt

import android.content.Intent
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.AccountRepository
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.helpers.UIHelper.*
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.source.SourceRepository
import br.com.jonathanzanella.myexpenses.views.MainActivity
import junit.framework.Assert.assertTrue
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class ReceiptsViewTest {
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    private var receipt: Receipt? = null
    private var receipt2: Receipt? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MyApplication.resetDatabase()

        val repository = ReceiptRepository()

        val s = SourceBuilder().build()
        assertTrue(SourceRepository().save(s).isValid)

        val a = AccountBuilder().build()
        assertTrue(AccountRepository().save(a).isValid)

        receipt = ReceiptBuilder().name("receipt1").source(s).account(a).build()
        assertTrue(repository.save(receipt!!).isValid)

        receipt2 = ReceiptBuilder().name("receipt2").source(s).account(a).build()
        assertTrue(repository.save(receipt2!!).isValid)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    @Throws(Exception::class)
    fun shows_receipt_correctly() {
        activityTestRule.launchActivity(Intent())

        openMenuAndClickItem(R.string.receipts)

        val receiptsTitle = getTargetContext().getString(R.string.receipts)
        matchToolbarTitle(receiptsTitle)

        clickIntoView(receipt!!.name, R.id.row_receipt_name)

        val editReceiptTitle = getTargetContext().getString(R.string.receipt) + " " + receipt!!.name
        matchToolbarTitle(editReceiptTitle)

        val incomeAsCurrency = receipt!!.income.toCurrencyFormatted()
        onView(withId(R.id.act_show_receipt_name)).check(matches(withText(receipt!!.name)))
        onView(withId(R.id.act_show_receipt_income)).check(matches(withText(incomeAsCurrency)))
        val account = receipt!!.accountFromCache
        onView(withId(R.id.act_show_receipt_account)).check(matches(withText(account!!.name)))

        val source = receipt!!.source
        onView(withId(R.id.act_show_receipt_source)).check(matches(withText(source!!.name)))
    }

    @Test
    @Throws(Exception::class)
    fun filter_do_not_show_receipt2() {
        activityTestRule.launchActivity(Intent())

        openMenuAndClickItem(R.string.receipts)

        val title = getTargetContext().getString(R.string.receipts)
        matchToolbarTitle(title)

        clickIntoView(R.id.search)
        typeTextIntoView(R.id.search_src_text, receipt!!.name)

        onViewReceiptName(receipt).check(matches(isDisplayed()))
        onViewReceiptName(receipt2).check(doesNotExist())
    }

    private fun onViewReceiptName(receipt: Receipt?): ViewInteraction {
        return onView(allOf<View>(
                withId(R.id.row_receipt_name),
                allOf<View>(
                        isDescendantOfA(withTagValue(`is`(receipt!!.uuid)))),
                withText(receipt.name)))
    }
}