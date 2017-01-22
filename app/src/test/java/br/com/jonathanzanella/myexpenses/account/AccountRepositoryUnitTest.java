package br.com.jonathanzanella.myexpenses.account;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by jzanella on 8/27/16.
 */
public class AccountRepositoryUnitTest {
	@Mock
	private Repository<Account> repository;
	@Mock
	private Account account;

	private AccountRepository accountRepository;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		accountRepository = new AccountRepository(repository);
	}

	@Test
	@Ignore //TODO: find a way to mock repository
	public void return_success_when_tried_to_save_valid_account() throws Exception {
		when(account.getName()).thenReturn("account");
		doNothing().when(repository).saveAtDatabase(accountRepository.accountTable, account);

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