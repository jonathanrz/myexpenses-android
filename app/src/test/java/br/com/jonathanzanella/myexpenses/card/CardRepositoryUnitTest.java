package br.com.jonathanzanella.myexpenses.card;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.account.AccountDataSource;
import br.com.jonathanzanella.myexpenses.helper.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helper.builder.CardBuilder;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class CardRepositoryUnitTest {
	private CardRepository subject;
	@Mock
	private AccountDataSource accountDataSource;
	@Mock
	private CardDao cardDao;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		subject = new CardRepository(cardDao);
	}

	@Test
	@Ignore("fix when convert tests to kotlin")
	public void return_with_error_when_tried_to_save_account_without_name() throws Exception {
		Card card = new CardBuilder()
				.name(null)
				.account(new AccountBuilder().build())
				.build(accountDataSource);

		ValidationResult result = subject.save(card);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}

	@Test
	@Ignore("fix when convert tests to kotlin")
	public void return_with_error_when_tried_to_save_account_without_type() throws Exception {
		Card card = new CardBuilder()
				.name("Test")
				.account(new AccountBuilder().build())
				.type(null)
				.build(accountDataSource);

		ValidationResult result = subject.save(card);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.CARD_TYPE));
	}

	@Test
	@Ignore("fix when convert tests to kotlin")
	public void return_with_error_when_tried_to_save_account_without_account() throws Exception {
		Card card = new CardBuilder()
				.name("Test")
				.type(CardType.DEBIT)
				.account(new AccountBuilder().build())
				.build(accountDataSource);

		ValidationResult result = subject.save(card);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.ACCOUNT));
	}
}