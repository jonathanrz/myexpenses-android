package br.com.jonathanzanella.myexpenses.expense;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.helper.builder.ExpenseBuilder;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class ExpenseRepositoryUnitTest {
	private ExpenseRepository subject;

	@Mock
	private Repository<Expense> repository;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		subject = new ExpenseRepository();
	}

	@Test
	public void return_with_error_when_tried_to_save_source_without_name() throws Exception {
		Expense expense = new ExpenseBuilder()
				.name(null)
				.build();

		ValidationResult result = subject.save(expense);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}
}