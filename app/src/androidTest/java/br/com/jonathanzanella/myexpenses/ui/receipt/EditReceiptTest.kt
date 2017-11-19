package br.com.jonathanzanella.myexpenses.ui.receipt

import android.content.Intent
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import br.com.jonathanzanella.TestApp
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder
import br.com.jonathanzanella.myexpenses.receipt.Receipt
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository
import br.com.jonathanzanella.myexpenses.receipt.ShowReceiptActivity
import br.com.jonathanzanella.myexpenses.source.SourceRepository
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import br.com.jonathanzanella.myexpenses.ui.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.*
import junit.framework.Assert.assertTrue
import org.hamcrest.core.Is.`is`
import org.joda.time.DateTime
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
class EditReceiptTest {
    @Rule @JvmField
    var activityTestRule = ActivityTestRule(ShowReceiptActivity::class.java, true, false)
    @Inject
    lateinit var accountDataSource: AccountDataSource
    @Inject
    lateinit var sourceRepository: SourceRepository
    @Inject
    lateinit var repository: ReceiptRepository

    private lateinit var receipt: Receipt

    @Before
    @Throws(Exception::class)
    fun setUp() {
        TestApp.getTestComponent().inject(this)
        App.resetDatabase()

        val a = AccountBuilder().build()
        assertTrue(accountDataSource.save(a).blockingFirst().isValid)

        val s = SourceBuilder().build()
        assertTrue(sourceRepository.save(s).isValid)

        receipt = ReceiptBuilder()
                .date(DateTime.now().minusDays(1))
                .account(a)
                .source(s)
                .build()
        assertTrue(repository.save(receipt).isValid)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    @Throws(Exception::class)
    fun edit_expense_correctly() {
        val i = Intent()
        i.putExtra(br.com.jonathanzanella.myexpenses.receipt.ShowReceiptActivity.KEY_RECEIPT_UUID, receipt.uuid)
        activityTestRule.launchActivity(i)

        val showReceiptTitle = getTargetContext().getString(R.string.receipt) + " " + receipt.name
        matchToolbarTitle(showReceiptTitle)

        clickIntoView(R.id.action_edit)

        val editReceiptTitle = getTargetContext().getString(R.string.edit_receipt_title)
        matchToolbarTitle(editReceiptTitle)
        onView(withId(R.id.act_edit_receipt_name)).check(matches(withText(receipt.name)))
        val expectedDate = Transaction.SIMPLE_DATE_FORMAT.format(receipt.getDate().toDate())
        onView(withId(R.id.act_edit_receipt_date)).check(matches(withText(expectedDate)))
        onView(withId(R.id.act_edit_receipt_account)).check(matches(withText(receipt.accountFromCache!!.name)))
        clearAndTypeTextIntoView(R.id.act_edit_receipt_name, receipt.name!! + " changed")

        clickIntoView(R.id.action_save)

        matchToolbarTitle(showReceiptTitle + " changed")

        receipt = repository.find(receipt.uuid!!)!!

        onView(withId(R.id.act_show_receipt_name)).check(matches(withText(receipt.name)))
        assertThat(repository.all().size, `is`(1))
    }
}