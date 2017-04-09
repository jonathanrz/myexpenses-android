package br.com.jonathanzanella.myexpenses.expense;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ExpenseRepositoryUnitTest {
	private ExpenseRepository subject;

	@Mock
	private Expense expense;
	@Mock
	private Repository<Expense> repository;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		subject = new ExpenseRepository(repository);
	}

	@Test
	public void return_with_error_when_tried_to_save_source_without_name() throws Exception {
		when(expense.getName()).thenReturn(null);

		ValidationResult result = subject.save(expense);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}
}