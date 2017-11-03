package br.com.jonathanzanella.myexpenses.bill

import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.card.CardDataSource
import br.com.jonathanzanella.myexpenses.expense.ExpenseDataSource
import br.com.jonathanzanella.myexpenses.helpers.TestUtils.waitForIdling
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.BillBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.CardBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder
import junit.framework.Assert.assertTrue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.core.Is
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class BillRepositoryTest {
    private val firstDayOfJune = DateTime(2016, 6, 1, 0, 0, 0, 0)
    lateinit var accountDataSource: AccountDataSource
    lateinit var billDataSource: BillDataSource
    lateinit var cardDataSource: CardDataSource
    lateinit var expenseDataSource: ExpenseDataSource

    @Before
    @Throws(Exception::class)
    fun setUp() {
        App.resetDatabase()
        accountDataSource = App.getApp().appComponent.accountDataSource()
        billDataSource = App.getApp().appComponent.billDataSource()
        cardDataSource = App.getApp().appComponent.cardDataSource()
        expenseDataSource = App.getApp().appComponent.expenseDataSource()
    }

    @Test
    @Throws(Exception::class)
    fun can_save_bill() {
        val bill = BillBuilder().build()
        billDataSource.save(bill).subscribe {
            assertThat(bill.id, `is`(not(0L)))
            assertThat<String>(bill.uuid, `is`(not("")))
        }
    }

    @Test
    @Throws(Exception::class)
    fun can_load_saved_bill() {
        val savedBill = BillBuilder().build()
        billDataSource.save(savedBill).subscribe {
            billDataSource.find(savedBill.uuid!!).subscribe {
                assertThat<String>(it.uuid, `is`<String>(savedBill.uuid))
                assertThat<DateTime>(it.initDate, `is`<DateTime>(savedBill.initDate))
            }
        }
    }

    @Test
    fun bill_is_paid_when_paid_with_credit_card() {
        val bill = BillBuilder()
                .initDate(firstDayOfJune)
                .endDate(firstDayOfJune)
                .build()
        assertTrue(billDataSource.save(bill).blockingFirst().isValid)

        val account = AccountBuilder().build()
        assertTrue(accountDataSource.save(account).blockingFirst().isValid)

        val card = CardBuilder().account(account).build(accountDataSource)
        assertTrue(cardDataSource.save(card).isValid)

        val expense = ExpenseBuilder()
                .date(firstDayOfJune)
                .bill(bill)
                .chargeable(card)
                .build()

        var emissionCount = 0

        billDataSource.monthly(firstDayOfJune).subscribe {
            emissionCount++

            when(emissionCount) {
                1 -> { assertThat(it.size, Is.`is`(1)) }
                2 -> { assertThat(it.size, Is.`is`(0)) }
                else -> { assert(false, { "it should not emit more then twice" }) }
            }
        }

        Thread.sleep(100) //Wait for first emission

        assertTrue(expenseDataSource.save(expense).isValid)

        Thread.sleep(100) //Wait for second emission

        assertThat(emissionCount, Is.`is`(2))
    }

    @Test
    @Throws(Exception::class)
    fun bill_greater_updated_at_returns_greater_updated_at() {
        var bill = BillBuilder().name("bill100").updatedAt(100L).build()
        assertTrue(billDataSource.save(bill).blockingFirst().isValid)
        bill = BillBuilder().name("bill99").updatedAt(99L).build()
        assertTrue(billDataSource.save(bill).blockingFirst().isValid)

        billDataSource.greaterUpdatedAt().subscribe {
            assertThat(it, `is`(100L))
        }
    }

    @Test
    @Throws(Exception::class)
    fun bill_unsync_returns_only_not_synced() {
        val billUnsync = BillBuilder().name("billUnsync").build()
        billUnsync.sync = false

        assertTrue(billDataSource.save(billUnsync).blockingFirst().isValid)

        val billSync = BillBuilder().name("billSync").build()
        assertTrue(billDataSource.save(billSync).blockingFirst().isValid)
        assertTrue(billDataSource.syncAndSave(billSync).blockingFirst().isValid)

        waitForIdling()

        val bills = billDataSource.unsync().blockingFirst()

        assertThat(bills.size, `is`(1))
        assertThat<String>(bills[0].uuid, `is`<String>(billUnsync.uuid))
    }

    @Test
    @Throws(Exception::class)
    fun load_user_bills_in_alphabetical_order() {
        val billA = BillBuilder().name("a").build()
        val billB = BillBuilder().name("b").build()

        var asserted = false

        billDataSource.all().subscribe {
            if(it.size == 2) {
                assertThat<String>(it[0].uuid, `is`<String>(billA.uuid))
                assertThat<String>(it[1].uuid, `is`<String>(billB.uuid))

                asserted = true
            }
        }

        assertTrue(billDataSource.save(billB).blockingFirst().isValid)
        assertTrue(billDataSource.save(billA).blockingFirst().isValid)

        Thread.sleep(100)

        assertTrue(asserted)
    }
}