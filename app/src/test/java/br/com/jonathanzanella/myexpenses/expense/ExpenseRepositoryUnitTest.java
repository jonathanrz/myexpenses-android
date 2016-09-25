package br.com.jonathanzanella.myexpenses.expense;

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
public class ExpenseRepositoryUnitTest {
	private ExpenseRepository repository = new ExpenseRepository();

	@Mock
	private Expense expense;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
	}

	@Test
	public void return_with_error_when_tried_to_save_source_without_name() throws Exception {
		when(expense.getName()).thenReturn(null);

		OperationResult result = repository.save(expense);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}
}