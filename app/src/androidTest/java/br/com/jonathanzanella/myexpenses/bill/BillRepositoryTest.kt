package br.com.jonathanzanella.myexpenses.bill

import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import br.com.jonathanzanella.TestApp
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.card.CardRepository
import br.com.jonathanzanella.myexpenses.helpers.TestUtils.waitForIdling
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.BillBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.CardBuilder
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.core.Is
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@SmallTest
class BillRepositoryTest {
    private val firstDayOfJune = DateTime(2016, 6, 1, 0, 0, 0, 0)
    @Inject
    lateinit var accountDataSource: AccountDataSource
    @Inject
    lateinit var cardRepository: CardRepository
    @Inject
    lateinit var billRepository: BillRepository

    @Before
    @Throws(Exception::class)
    fun setUp() {
        TestApp.getTestComponent().inject(this)
        App.resetDatabase()
    }

    @Test
    @Throws(Exception::class)
    fun can_save_bill() {
        val bill = BillBuilder().build()
        billRepository.save(bill).subscribe {
            assertThat(bill.id, `is`(not(0L)))
            assertThat<String>(bill.uuid, `is`(not("")))
        }
    }

    @Test
    @Throws(Exception::class)
    fun can_load_saved_bill() {
        val savedBill = BillBuilder().build()
        billRepository.save(savedBill).subscribe {
            billRepository.find(savedBill.uuid!!).subscribe {
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
        assert(billRepository.save(bill).blockingFirst().isValid)

        val account = AccountBuilder().build()
        assert(accountDataSource.save(account).blockingFirst().isValid)

        val card = CardBuilder().account(account).build(accountDataSource)
        assert(cardRepository.save(card).isValid)

        val expense = ExpenseBuilder()
                .date(firstDayOfJune)
                .bill(bill)
                .chargeable(card)
                .build()

        var emissionCount = 0

        billRepository.monthly(firstDayOfJune).subscribe {
            emissionCount++

            when(emissionCount) {
                1 -> { assertThat(it.size, Is.`is`(1)) }
                2 -> { assertThat(it.size, Is.`is`(0)) }
                else -> { assert(false, { "it should not emit more then twice" }) }
            }
        }

        Thread.sleep(100) //Wait for first emission

        assert(billRepository.expenseDataSource.save(expense).isValid)

        Thread.sleep(100) //Wait for second emission

        assertThat(emissionCount, Is.`is`(2))
    }

    @Test
    @Throws(Exception::class)
    fun bill_greater_updated_at_returns_greater_updated_at() {
        var bill = BillBuilder().name("bill100").updatedAt(100L).build()
        assert(billRepository.save(bill).blockingFirst().isValid)
        bill = BillBuilder().name("bill99").updatedAt(99L).build()
        assert(billRepository.save(bill).blockingFirst().isValid)

        billRepository.greaterUpdatedAt().subscribe {
            assertThat(it, `is`(100L))
        }
    }

    @Test
    @Throws(Exception::class)
    fun bill_unsync_returns_only_not_synced() {
        val billUnsync = BillBuilder().name("billUnsync").build()
        billUnsync.sync = false
        assert(billRepository.save(billUnsync).blockingFirst().isValid)

        val billSync = BillBuilder().name("billSync").build()
        assert(billRepository.save(billSync).blockingFirst().isValid)
        assert(billRepository.syncAndSave(billSync).blockingFirst().isValid)

        waitForIdling()

        billRepository.unsync().subscribe {
            assertThat(it.size, `is`(1))
            assertThat<String>(it[0].uuid, `is`<String>(billUnsync.uuid))
        }
    }

    @Test
    @Throws(Exception::class)
    fun load_user_bills_in_alphabetical_order() {
        val billB = BillBuilder().name("b").build()
        assert(billRepository.save(billB).blockingFirst().isValid)

        val billA = BillBuilder().name("a").build()
        assert(billRepository.save(billA).blockingFirst().isValid)

        billRepository.all().subscribe {
            assertThat<String>(it[0].uuid, `is`<String>(billA.uuid))
            assertThat<String>(it[1].uuid, `is`<String>(billB.uuid))
        }
    }
}