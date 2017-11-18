package br.com.jonathanzanella.myexpenses.unit.expense;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ExpenseAdapterPresenterTest {
	@Mock
	private br.com.jonathanzanella.myexpenses.expense.ExpenseDataSource dataSource;

	private br.com.jonathanzanella.myexpenses.expense.ExpenseAdapterPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);

		presenter = new br.com.jonathanzanella.myexpenses.expense.ExpenseAdapterPresenter(dataSource);
	}

	@Test(expected = UnsupportedOperationException.class)
	@Ignore("fix when convert tests to java")
	public void get_sources_return_unmodifiable_list() {
		DateTime dateTime = new DateTime(2016, 9, 26, 0, 0, 0, DateTimeZone.UTC);
		when(dataSource.monthly(dateTime)).thenReturn(new ArrayList<br.com.jonathanzanella.myexpenses.expense.Expense>());

		List<br.com.jonathanzanella.myexpenses.expense.Expense> expenses = presenter.getExpenses(true, dateTime);
		expenses.add(new br.com.jonathanzanella.myexpenses.expense.Expense());
	}
}