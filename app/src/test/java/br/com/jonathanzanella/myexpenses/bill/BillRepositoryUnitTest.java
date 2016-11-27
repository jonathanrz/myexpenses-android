package br.com.jonathanzanella.myexpenses.bill;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.database.Repository;
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
	@Mock
	private Repository<Bill> repository;
	@Mock
	private Bill bill;

	private BillRepository billRepository = new BillRepository(repository);

	@Before
	public void setUp() throws Exception {
		initMocks(this);
	}

	@Test
	public void return_with_error_when_tried_to_save_bill_without_name() throws Exception {
		when(bill.getName()).thenReturn(null);

		OperationResult result = billRepository.save(bill);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}

	@Test
	public void return_with_error_when_tried_to_save_bill_without_amount() throws Exception {
		when(bill.getName()).thenReturn("a");
		when(bill.getAmount()).thenReturn(0);

		OperationResult result = billRepository.save(bill);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.AMOUNT));
	}

	@Test
	public void return_with_error_when_tried_to_save_bill_without_due_date() throws Exception {
		when(bill.getDueDate()).thenReturn(0);

		OperationResult result = billRepository.save(bill);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.DUE_DATE));
	}

	@Test
	public void return_with_error_when_tried_to_save_bill_without_dates() throws Exception {
		when(bill.getInitDate()).thenReturn(null);
		when(bill.getEndDate()).thenReturn(null);

		OperationResult result = billRepository.save(bill);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.INIT_DATE));
		assertTrue(result.getErrors().contains(ValidationError.END_DATE));
	}

	@Test
	public void return_with_error_when_tried_to_save_bill_with_init_date_greater_than_end_date() throws Exception {
		when(bill.getInitDate()).thenReturn(new DateTime(2016, 10, 3, 0, 0, 0, DateTimeZone.UTC));
		when(bill.getEndDate()).thenReturn(new DateTime(2016, 10, 2, 0, 0, 0, DateTimeZone.UTC));

		OperationResult result = billRepository.save(bill);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.INIT_DATE_GREATER_THAN_END_DATE));
	}
}