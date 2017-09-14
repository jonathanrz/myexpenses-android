package br.com.jonathanzanella.myexpenses.account

import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Maybe
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito.verify

class AccountPresenterTest {
    lateinit var dataSource: AccountDataSource
    lateinit var view: AccountContract.EditView

    @Before
    fun setUp() {
        dataSource = mock()
        view = mock()
    }

    @Test
    @Ignore("Fix when add RX")
    fun save_gets_data_from_screen_and_save_to_repository() {
        val account = Account()
        account.uuid = "uuid"

        whenever(dataSource.find(account.uuid!!)).thenReturn(Maybe.just(account))
        whenever(dataSource.save(account)).thenReturn(ValidationResult())

        val presenter = AccountPresenter(dataSource)
        presenter.attachView(view)

        presenter.loadAccount(account.uuid!!)
        presenter.save()

        verify(view).fillAccount(account)
        verify(dataSource).save(account)
        verify(view).finishView()
    }

//    @Test
//    @Ignore("fix when add DI")
//    fun call_view_with_errors() {
//        val result = ValidationResult()
//        result.addError(ValidationError.NAME)
//
//        `when`(dataSource!!.save(any(Account::class.java))).thenReturn(result)
//
//        presenter!!.save()

//        verify<EditView>(view, times(1)).showError(ValidationError.NAME)
//    }
}