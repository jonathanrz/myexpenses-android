package br.com.jonathanzanella.myexpenses.receipt

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
import br.com.jonathanzanella.myexpenses.account.AccountRepository
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper
import br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.source.SourceRepository
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
class ShowReceiptActivityTest {
    @Rule @JvmField
    var activityTestRule = ActivityTestRule(ShowReceiptActivity::class.java, true, false)

    private var receipt: Receipt? = null

    @Inject
    lateinit var repository: ReceiptRepository
    @Inject
    lateinit var sourceRepository: SourceRepository
    @Inject
    lateinit var accountRepository: AccountRepository

    @Before
    @Throws(Exception::class)
    fun setUp() {
        TestApp.getTestComponent().inject(this)
        App.resetDatabase()

        val s = SourceBuilder().build()
        sourceRepository.save(s)

        val a = AccountBuilder().build()
        accountRepository.save(a)

        receipt = ReceiptBuilder().source(s).account(a).build()
        repository.save(receipt!!)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActivityLifecycleHelper.closeAllActivities(getInstrumentation())
    }

    @Test
    @Throws(Exception::class)
    fun shows_receipt_correctly() {
        val i = Intent()
        i.putExtra(ShowReceiptActivity.KEY_RECEIPT_UUID, receipt!!.uuid)
        activityTestRule.launchActivity(i)

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
}