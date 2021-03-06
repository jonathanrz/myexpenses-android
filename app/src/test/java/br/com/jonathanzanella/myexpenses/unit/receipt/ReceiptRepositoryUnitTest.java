package br.com.jonathanzanella.myexpenses.unit.receipt;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.unit.helper.builder.ReceiptBuilder;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ReceiptRepositoryUnitTest {
	private br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository subject;
	@Mock
	private br.com.jonathanzanella.myexpenses.receipt.ReceiptDao dao;

	@Before
	public void setUp() throws Exception {
		subject = new br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository(dao);
	}

	@Test
	@Ignore("fix when convert to kotlin")
	public void return_with_error_when_tried_to_save_source_without_name() throws Exception {
		br.com.jonathanzanella.myexpenses.receipt.Receipt receipt = new ReceiptBuilder().name(null).build();

		ValidationResult result = subject.save(receipt);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}
}