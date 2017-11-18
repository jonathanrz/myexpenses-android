package br.com.jonathanzanella.myexpenses.unit.bill

import br.com.jonathanzanella.myexpenses.bill.Bill
import br.com.jonathanzanella.myexpenses.bill.BillContract
import br.com.jonathanzanella.myexpenses.bill.BillDataSource
import br.com.jonathanzanella.myexpenses.bill.BillPresenter
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable.just
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Matchers.any
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

@Ignore("Remove when moving RX to views")
class BillPresenterTest {
    private lateinit var dataSource: BillDataSource
    private lateinit var view: BillContract.EditView

    private lateinit var presenter: BillPresenter

    @Before
    @Throws(Exception::class)
    fun setUp() {
        dataSource = mock()
        view = mock()
        presenter = br.com.jonathanzanella.myexpenses.bill.BillPresenter(dataSource)
        presenter.attachView(view)
    }

    @Test
    fun save_gets_data_from_screen_and_save_to_repository() {
        whenever(view.fillBill(any(Bill::class.java))).thenReturn(Bill())
        whenever(dataSource.save(any(Bill::class.java))).thenReturn(just(ValidationResult()))

        presenter.save()

        verify<BillContract.EditView>(view, times(1)).fillBill(ArgumentMatchers.any(Bill::class.java))
        verify<BillDataSource>(dataSource, times(1)).save(ArgumentMatchers.any(Bill::class.java))
        verify<BillContract.EditView>(view, times(1)).finishView()
    }

    @Test
    fun call_view_with_errors() {
        val result = ValidationResult()
        result.addError(ValidationError.NAME)

        whenever(view.fillBill(any(Bill::class.java))).thenReturn(Bill())
        whenever(dataSource.save(any(Bill::class.java))).thenReturn(just(result))

        presenter.save()

        verify<BillContract.EditView>(view, times(1)).showError(ValidationError.NAME)
    }

    @Test
    @Throws(Exception::class)
    fun empty_bill_does_not_not_call_show_bill() {
        whenever(dataSource.find(UUID)).thenReturn(null)

        presenter.loadBill(UUID)
        verify<BillContract.EditView>(view, times(0)).showBill(ArgumentMatchers.any(Bill::class.java))
    }

    companion object {
        private val UUID = "uuid"
    }
}