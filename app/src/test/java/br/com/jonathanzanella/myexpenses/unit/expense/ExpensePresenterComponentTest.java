package br.com.jonathanzanella.myexpenses.unit.expense;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import br.com.jonathanzanella.myexpenses.bill.BillDataSource;
import br.com.jonathanzanella.myexpenses.card.CardDataSource;
import br.com.jonathanzanella.myexpenses.unit.helper.builder.ExpenseBuilder;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@Ignore
public class ExpensePresenterComponentTest {
	private br.com.jonathanzanella.myexpenses.expense.ExpenseRepository repository;
	private br.com.jonathanzanella.myexpenses.expense.ExpensePresenter presenter;

	@Mock
	private br.com.jonathanzanella.myexpenses.expense.ExpenseContract.EditView view;
	@Mock
	private BillDataSource billDataSource;
	@Mock
	private CardDataSource cardDataSource;
	@Mock
	private br.com.jonathanzanella.myexpenses.expense.ExpenseDao dao;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		repository = new br.com.jonathanzanella.myexpenses.expense.ExpenseRepository(dao, cardDataSource);
		presenter = new br.com.jonathanzanella.myexpenses.expense.ExpensePresenter(repository, billDataSource);
		presenter.attachView(view);
	}

	@Test
	public void generate_expenses_with_installments() throws Exception {
		DateTime date = new DateTime(2016, 9, 26, 0, 0, 0, DateTimeZone.UTC);
		int value = 30000;
		String name = "expense installment";
		br.com.jonathanzanella.myexpenses.expense.Expense expense = new ExpenseBuilder()
				.name(name)
				.value(value)
				.date(date)
				.installments(3)
				.build();
		when(view.fillExpense(any(br.com.jonathanzanella.myexpenses.expense.Expense.class))).thenReturn(expense);
		presenter.save();

		List<br.com.jonathanzanella.myexpenses.expense.Expense> expenses = repository.all();
		assertThat(expenses.size(), is(3));
		assertThat(expenses.get(0).getName(), is(name + " 01/03"));
		assertThat(expenses.get(0).getDate().getMonthOfYear(), is(date.getMonthOfYear()));
		assertThat(expenses.get(0).getValue(), is(10000));
		assertThat(expenses.get(1).getName(), is(name + " 02/03"));
		assertThat(expenses.get(1).getDate().getMonthOfYear(), is(date.getMonthOfYear() + 1));
		assertThat(expenses.get(1).getValue(), is(10000));
		assertThat(expenses.get(2).getName(), is(name + " 03/03"));
		assertThat(expenses.get(2).getDate().getMonthOfYear(), is(date.getMonthOfYear() + 2));
		assertThat(expenses.get(2).getValue(), is(10000));
	}

	@Test
	public void generate_expenses_with_repetition() throws Exception {
		DateTime date = new DateTime(2016, 9, 26, 0, 0, 0, DateTimeZone.UTC);
		int value = 30000;
		String name = "expense repetition";
		br.com.jonathanzanella.myexpenses.expense.Expense expense = new ExpenseBuilder()
				.name(name)
				.value(value)
				.date(date)
				.repetition(3)
				.build();
		when(view.fillExpense(any(br.com.jonathanzanella.myexpenses.expense.Expense.class))).thenReturn(expense);
		presenter.save();

		List<br.com.jonathanzanella.myexpenses.expense.Expense> expenses = repository.all();
		assertThat(expenses.size(), is(3));
		assertThat(expenses.get(0).getName(), is(name));
		assertThat(expenses.get(0).getDate().getMonthOfYear(), is(date.getMonthOfYear()));
		assertThat(expenses.get(0).getValue(), is(value));
		assertThat(expenses.get(1).getName(), is(name));
		assertThat(expenses.get(1).getDate().getMonthOfYear(), is(date.getMonthOfYear() + 1));
		assertThat(expenses.get(1).getValue(), is(value));
		assertThat(expenses.get(2).getName(), is(name));
		assertThat(expenses.get(2).getDate().getMonthOfYear(), is(date.getMonthOfYear() + 2));
		assertThat(expenses.get(2).getValue(), is(value));
	}
}
