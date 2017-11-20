package br.com.jonathanzanella.myexpenses.unit.expense;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.card.CardRepository;
import br.com.jonathanzanella.myexpenses.unit.helper.builder.ExpenseBuilder;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class ExpenseRepositoryUnitTest {
	private br.com.jonathanzanella.myexpenses.expense.ExpenseRepository subject;

	@Mock
	private br.com.jonathanzanella.myexpenses.expense.ExpenseDao dao;
	@Mock
	private CardRepository cardRepository;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		subject = new br.com.jonathanzanella.myexpenses.expense.ExpenseRepository(dao, cardRepository);
	}

	@Test
	@Ignore("update when remove dataSource from models")
	public void return_with_error_when_tried_to_save_source_without_name() throws Exception {
		br.com.jonathanzanella.myexpenses.expense.Expense expense = new ExpenseBuilder()
				.name(null)
				.build();

		ValidationResult result = subject.save(expense);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}
}