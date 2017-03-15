package br.com.jonathanzanella.myexpenses.account;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.database.RepositoryMock;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AccountRepositoryUnitTest {
	@Mock
	private Account account;

	private AccountRepository accountRepository;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		accountRepository = new AccountRepository(new RepositoryMock<Account>());
	}

	@Test
	public void return_success_when_tried_to_save_valid_account() throws Exception {
		when(account.getName()).thenReturn("account");

		OperationResult result = accountRepository.save(account);

		assertTrue(result.isValid());
	}

	@Test
	public void return_with_error_when_tried_to_save_account_without_name() throws Exception {
		when(account.getName()).thenReturn(null);

		OperationResult result = accountRepository.save(account);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}
}