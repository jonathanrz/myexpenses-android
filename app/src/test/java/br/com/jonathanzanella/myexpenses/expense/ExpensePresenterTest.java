package br.com.jonathanzanella.myexpenses.expense;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.bill.BillDataSource;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ExpensePresenterTest {
	private static final String UUID = "uuid";
	@Mock
	private ExpenseDataSource dataSource;
	@Mock
	private BillDataSource billDataSource;
	@Mock
	private ExpenseContract.EditView view;

	private ExpensePresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		presenter = new ExpensePresenter(dataSource, billDataSource);
		presenter.attachView(view);
	}

	@Test(expected = ExpenseNotFoundException.class)
	@Ignore("fix when convert tests to kotlin")
	public void load_empty_source_throws_not_found_exception() {
		when(dataSource.find(UUID)).thenReturn(null);

		presenter.loadExpense(UUID);
	}

	@Test
	@Ignore("fix when convert tests to kotlin")
	public void save_gets_data_from_screen_and_save_to_dataSource() {
		when(view.fillExpense(any(Expense.class))).thenReturn(new Expense());
		when(dataSource.save(any(Expense.class))).thenReturn(new ValidationResult());

		presenter.save();

		verify(view, times(1)).fillExpense(any(Expense.class));
		verify(dataSource, times(1)).save(any(Expense.class));
		verify(view, times(1)).finishView();
	}

	@Test
	@Ignore("fix when convert tests to kotlin")
	public void call_view_with_errors() {
		ValidationResult result = new ValidationResult();
		result.addError(ValidationError.NAME);

		when(view.fillExpense(any(Expense.class))).thenReturn(new Expense());
		when(dataSource.save(any(Expense.class))).thenReturn(result);

		presenter.save();

		verify(view, times(1)).showError(ValidationError.NAME);
	}
}