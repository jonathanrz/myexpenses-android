package br.com.jonathanzanella.myexpenses.unit.account

import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountDao
import br.com.jonathanzanella.myexpenses.account.AccountRepository
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

class AccountRepositoryUnitTest {
    @Mock
    private lateinit var account: Account
    @Mock
    private lateinit var accountDao: AccountDao

    private lateinit var accountRepository: AccountRepository

    @Before
    @Throws(Exception::class)
    fun setUp() {
        account = mock()
        accountDao = mock()
        accountRepository = AccountRepository(accountDao)
    }

    @Test
    @Throws(Exception::class)
    fun return_success_when_tried_to_save_valid_account() {
        whenever(account.name).thenReturn("name")
        whenever(account.uuid).thenReturn("uuid")

        val result = accountRepository.save(account).blockingFirst()

        assertTrue(result.isValid)
    }

    @Test
    @Throws(Exception::class)
    fun return_with_error_when_tried_to_save_account_without_name() {
        whenever(account.name).thenReturn(null)
        whenever(account.uuid).thenReturn("uuid")

        val result = accountRepository.save(account).blockingFirst()

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(ValidationError.NAME))
    }
}