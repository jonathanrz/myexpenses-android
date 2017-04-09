package br.com.jonathanzanella.myexpenses.expense;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.bill.BillRepository;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ExpensePresenterTest {
	private static final String UUID = "uuid";
	@Mock
	private ExpenseRepository repository;
	@Mock
	private BillRepository billRepository;
	@Mock
	private ExpenseContract.EditView view;

	private ExpensePresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		presenter = new ExpensePresenter(repository, billRepository);
		presenter.attachView(view);
	}

	@Test(expected = ExpenseNotFoundException.class)
	public void load_empty_source_throws_not_found_exception() {
		when(repository.find(UUID)).thenReturn(null);

		presenter.loadExpense(UUID);
	}

	@Test
	public void save_gets_data_from_screen_and_save_to_repository() {
		when(view.fillExpense(any(Expense.class))).thenReturn(new Expense());
		when(repository.save(any(Expense.class))).thenReturn(new ValidationResult());

		presenter.save();

		verify(view, times(1)).fillExpense(any(Expense.class));
		verify(repository, times(1)).save(any(Expense.class));
		verify(view, times(1)).finishView();
	}

	@Test
	public void call_view_with_errors() {
		ValidationResult result = new ValidationResult();
		result.addError(ValidationError.NAME);

		when(view.fillExpense(any(Expense.class))).thenReturn(new Expense());
		when(repository.save(any(Expense.class))).thenReturn(result);

		presenter.save();

		verify(view, times(1)).showError(ValidationError.NAME);
	}
}