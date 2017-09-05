package br.com.jonathanzanella.myexpenses.account

import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito.verify

class AccountPresenterTest {

    @Test
    @Ignore("fix when add DI")
    fun save_gets_data_from_screen_and_save_to_repository() {
        val account = Account()
        account.uuid = "uuid"

        val repository = mock<AccountRepository> {
            on { find(account.uuid!!) } doReturn account
            on { save(account) } doReturn ValidationResult()
        }

        val view = mock<AccountContract.EditView>()

        val presenter = AccountPresenter()
        presenter.attachView(view)

        presenter.loadAccount(account.uuid!!)
        presenter.save()

        verify(view).fillAccount(account)
        verify(repository).save(account)
        verify(view).finishView()
    }

//    @Test
//    @Ignore("fix when add DI")
//    fun call_view_with_errors() {
//        val result = ValidationResult()
//        result.addError(ValidationError.NAME)
//
//        `when`(repository!!.save(any(Account::class.java))).thenReturn(result)
//
//        presenter!!.save()

//        verify<EditView>(view, times(1)).showError(ValidationError.NAME)
//    }
}