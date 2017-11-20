package br.com.jonathanzanella.myexpenses.unit.card

import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.card.*
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseDataSource
import br.com.jonathanzanella.myexpenses.unit.helper.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.unit.helper.builder.CardBuilder
import br.com.jonathanzanella.myexpenses.unit.helper.builder.ExpenseBuilder
import br.com.jonathanzanella.myexpenses.helpers.ResourcesHelper
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable.just
import org.hamcrest.core.Is.`is`
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import java.util.*

@Ignore("Fix when converting to RX")
class CardPresenterTest {
    private lateinit var dataSource: CardDataSource
    private lateinit var accountDataSource: AccountDataSource
    private lateinit var expenseDataSource: ExpenseDataSource
    private lateinit var view: CardContract.EditView
    private lateinit var resourcesHelper: ResourcesHelper

    private lateinit var presenter: CardPresenter

    @Before
    @Throws(Exception::class)
    fun setUp() {
        dataSource = mock()
        accountDataSource = mock()
        expenseDataSource = mock()
        view = mock()
        resourcesHelper = mock()

        DateTimeZone.setDefault(DateTimeZone.UTC)
        presenter = CardPresenter(accountDataSource, dataSource, expenseDataSource, resourcesHelper)
        presenter.attachView(view)
    }

    @Test(expected = CardNotFoundException::class)
    fun load_empty_card_throws_not_found_exception() {
        whenever(dataSource.find(UUID)).thenReturn(null)

        presenter.loadCard(UUID)
    }

    @Test
    fun save_gets_data_from_screen_and_save_to_repository() {
        val card = Card(accountDataSource)
        whenever(dataSource.find("uuid")).thenReturn(card)
        whenever(dataSource.save(card)).thenReturn(ValidationResult())
        whenever(view.fillCard(card)).thenReturn(Card(accountDataSource))

        presenter.loadCard("uuid")
        presenter.save()

        verify<CardContract.EditView>(view, times(1)).fillCard(card)
        verify<CardDataSource>(dataSource, times(1)).save(card)
        verify<CardContract.EditView>(view, times(1)).finishView()
    }

    @Test
    fun call_view_with_errors() {
        val card = Card(accountDataSource)

        val result = ValidationResult()
        result.addError(ValidationError.NAME)

        whenever(view.fillCard(card)).thenReturn(Card(accountDataSource))
        whenever(dataSource.save(card)).thenReturn(result)

        presenter.save()

        verify<CardContract.EditView>(view, times(1)).showError(ValidationError.NAME)
    }

    @Test
    @Throws(Exception::class)
    fun generate_card_bill_value_correctly() {
        val uuid = "uuid"
        val value = 100
        val account = AccountBuilder().build()
        val card = CardBuilder().account(account).build(accountDataSource)
        whenever(dataSource.find(uuid)).thenReturn(card)
        whenever(accountDataSource.find(ArgumentMatchers.anyString())).thenReturn(just(account))
        whenever(expenseDataSource.save(ArgumentMatchers.any(Expense::class.java))).thenReturn(ValidationResult())
        val expenseList = ArrayList<Expense>()
        expenseList.add(ExpenseBuilder().value(value).build())
        expenseList.add(ExpenseBuilder().value(value).build())
        whenever(expenseDataSource.creditCardBills(any(Card::class.java), any(DateTime::class.java))).thenReturn(expenseList)

        val invoice = "Fatura"
        `when`(resourcesHelper!!.getString(R.string.invoice)).thenReturn(invoice)

        presenter.loadCard(uuid)
        val expense = presenter.generateCreditCardBill(DateTime(2016, 9, 26, 0, 0, 0, DateTimeZone.UTC))

        assertThat<String>(expense!!.name, `is`(invoice + " " + card.name))
        assertThat(expense.value, `is`(value * expenseList.size))
        assertThat<String>(expense.chargeableFromCache!!.name, `is`<String>(account.name))
        assertTrue(expenseList[0].charged)
        assertTrue(expenseList[1].charged)
    }

    @Test
    @Throws(Exception::class)
    fun not_generate_card_bill_when_there_are_no_expenses() {
        val uuid = "uuid"
        val account = AccountBuilder().build()
        val card = CardBuilder().account(account).build(accountDataSource)
        whenever(dataSource.find(uuid)).thenReturn(card)
        val expenseList = ArrayList<Expense>()
        whenever(expenseDataSource.creditCardBills(card, ArgumentMatchers.any(DateTime::class.java))).thenReturn(expenseList)

        val invoice = "Fatura"
        `when`(resourcesHelper!!.getString(R.string.invoice)).thenReturn(invoice)

        presenter.loadCard(uuid)
        val expense = presenter.generateCreditCardBill(DateTime(2016, 9, 26, 0, 0, 0, DateTimeZone.UTC))

        assertNull(expense)
    }

    companion object {
        private val UUID = "uuid"
    }
}