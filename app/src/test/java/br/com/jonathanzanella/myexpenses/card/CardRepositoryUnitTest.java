package br.com.jonathanzanella.myexpenses.card;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by jzanella on 8/27/16.
 */
public class CardRepositoryUnitTest {
	private CardRepository subject;
	@Mock
	private ExpenseRepository expenseRepository;
	@Mock
	private Card card;
	@Mock
	private Repository<Card> repository;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		subject = new CardRepository(repository, expenseRepository);
	}

	@Test
	public void return_with_error_when_tried_to_save_account_without_name() throws Exception {
		when(card.getName()).thenReturn(null);

		ValidationResult result = subject.save(card);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}

	@Test
	public void return_with_error_when_tried_to_save_account_without_type() throws Exception {
		when(card.getName()).thenReturn("Test");

		ValidationResult result = subject.save(card);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.CARD_TYPE));
	}

	@Test
	public void return_with_error_when_tried_to_save_account_without_account() throws Exception {
		when(card.getName()).thenReturn("Test");
		when(card.getType()).thenReturn(CardType.DEBIT);

		ValidationResult result = subject.save(card);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.ACCOUNT));
	}
}