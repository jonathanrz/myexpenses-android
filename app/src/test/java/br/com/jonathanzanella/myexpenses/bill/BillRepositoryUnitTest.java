package br.com.jonathanzanella.myexpenses.bill;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class BillRepositoryUnitTest {
	@Mock
	private Repository<Bill> repository;
	@Mock
	private ExpenseRepository expenseRepository;

	private BillRepository billRepository;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		billRepository = new BillRepository(expenseRepository);
	}

	@Test
	public void return_with_error_when_tried_to_save_bill_without_name() throws Exception {
		Bill bill = new Bill();
		bill.setName(null);

		ValidationResult result = billRepository.save(bill);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}

	@Test
	public void return_with_error_when_tried_to_save_bill_without_amount() throws Exception {
		Bill bill = new Bill();
		bill.setName("a");
		bill.setAmount(0);

		ValidationResult result = billRepository.save(bill);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.AMOUNT));
	}

	@Test
	public void return_with_error_when_tried_to_save_bill_without_due_date() throws Exception {
		Bill bill = new Bill();
		bill.setDueDate(0);

		ValidationResult result = billRepository.save(bill);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.DUE_DATE));
	}

	@Test
	public void return_with_error_when_tried_to_save_bill_without_dates() throws Exception {
		Bill bill = new Bill();
		bill.setInitDate(null);
		bill.setEndDate(null);

		ValidationResult result = billRepository.save(bill);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.INIT_DATE));
		assertTrue(result.getErrors().contains(ValidationError.END_DATE));
	}

	@Test
	public void return_with_error_when_tried_to_save_bill_with_init_date_greater_than_end_date() throws Exception {
		Bill bill = new Bill();
		bill.setInitDate(new DateTime(2016, 10, 3, 0, 0, 0, DateTimeZone.UTC));
		bill.setEndDate(new DateTime(2016, 10, 2, 0, 0, 0, DateTimeZone.UTC));

		ValidationResult result = billRepository.save(bill);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.INIT_DATE_GREATER_THAN_END_DATE));
	}
}