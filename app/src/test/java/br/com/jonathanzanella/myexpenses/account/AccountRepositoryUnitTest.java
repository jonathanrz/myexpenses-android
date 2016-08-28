package br.com.jonathanzanella.myexpenses.account;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by jzanella on 8/27/16.
 */
public class AccountRepositoryUnitTest {
	private AccountRepository repository = new AccountRepository();

	@Mock
	private Account account;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
	}

	@Test
	public void return_with_error_when_tried_to_save_account_without_name() throws Exception {
		when(account.getName()).thenReturn(null);

		OperationResult result = repository.save(account);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}
}