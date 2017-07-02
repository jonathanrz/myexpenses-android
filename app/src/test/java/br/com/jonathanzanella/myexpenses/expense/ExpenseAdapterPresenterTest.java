package br.com.jonathanzanella.myexpenses.expense;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ExpenseAdapterPresenterTest {
	@Mock
	private ExpenseRepository repository;

	private ExpenseAdapterPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);

		presenter = new ExpenseAdapterPresenter(repository);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void get_sources_return_unmodifiable_list() {
		DateTime dateTime = new DateTime(2016, 9, 26, 0, 0, 0, DateTimeZone.UTC);
		when(repository.monthly(dateTime)).thenReturn(new ArrayList<Expense>());

		List<Expense> expenses = presenter.getExpenses(true, dateTime);
		expenses.add(new Expense());
	}
}