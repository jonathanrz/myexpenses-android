package br.com.jonathanzanella.myexpenses.account

import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify

@Suppress("IllegalIdentifier")
class AccountPresenterTest {
    lateinit var dataSource: AccountDataSource
    lateinit var view: AccountContract.EditView

    @Before
    fun setUp() {
        dataSource = mock()
        view = mock()
    }

    @Test
    fun `save account with the data from the screen`() {
        val account = Account()
        account.uuid = "uuid"

        whenever(view.fillAccount(account)).thenReturn(account)
        whenever(dataSource.find(account.uuid!!)).thenReturn(Observable.just(account))
        whenever(dataSource.save(account)).thenReturn(Observable.just(ValidationResult()))

        val presenter = AccountPresenter(dataSource)
        presenter.attachView(view)

        presenter.loadAccount(account.uuid!!).blockingFirst()
        presenter.save().blockingFirst()

        verify(view).fillAccount(account)
        verify(dataSource).save(account)
    }
}