package br.com.jonathanzanella.myexpenses.receipt;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReceiptRepositoryUnitTest {
	private ReceiptRepository repository = new ReceiptRepository(new Repository<Receipt>(MyApplication.getContext()));

	@Mock
	private Receipt receipt;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
	}

	@Test
	public void return_with_error_when_tried_to_save_source_without_name() throws Exception {
		when(receipt.getName()).thenReturn(null);

		OperationResult result = repository.save(receipt);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}
}