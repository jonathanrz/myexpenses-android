package br.com.jonathanzanella.myexpenses.bill;

import org.joda.time.DateTime;
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
public class BillRepositoryUnitTest {
	private BillRepository repository = new BillRepository();

	@Mock
	private Bill bill;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
	}

	@Test
	public void return_with_error_when_tried_to_save_bill_without_name() throws Exception {
		when(bill.getName()).thenReturn(null);

		OperationResult result = repository.save(bill);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}

	@Test
	public void return_with_error_when_tried_to_save_bill_without_amount() throws Exception {
		when(bill.getName()).thenReturn("a");
		when(bill.getAmount()).thenReturn(0);

		OperationResult result = repository.save(bill);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.AMOUNT));
	}

	@Test
	public void return_with_error_when_tried_to_save_bill_without_due_date() throws Exception {
		when(bill.getDueDate()).thenReturn(0);

		OperationResult result = repository.save(bill);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.DUE_DATE));
	}

	@Test
	public void return_with_error_when_tried_to_save_bill_without_dates() throws Exception {
		when(bill.getInitDate()).thenReturn(null);
		when(bill.getEndDate()).thenReturn(null);

		OperationResult result = repository.save(bill);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.INIT_DATE));
		assertTrue(result.getErrors().contains(ValidationError.END_DATE));
	}

	@Test
	public void return_with_error_when_tried_to_save_bill_with_init_date_greater_than_end_date() throws Exception {
		when(bill.getInitDate()).thenReturn(DateTime.now().plusDays(1));
		when(bill.getEndDate()).thenReturn(DateTime.now());

		OperationResult result = repository.save(bill);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.INIT_DATE_GREATER_THAN_END_DATE));
	}
}