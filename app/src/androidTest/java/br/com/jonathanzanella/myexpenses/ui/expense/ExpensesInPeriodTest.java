package br.com.jonathanzanella.myexpenses.ui.expense;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import javax.inject.Inject;

import br.com.jonathanzanella.TestApp;
import br.com.jonathanzanella.myexpenses.App;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountDataSource;
import br.com.jonathanzanella.myexpenses.overview.WeeklyPagerAdapter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ExpensesInPeriodTest {
	@Inject
	AccountDataSource accountDataSource;
	@Inject
	br.com.jonathanzanella.myexpenses.expense.ExpenseDataSource expenseDataSource;
	private final DateTime firstDayOfJune = new DateTime(2016, 6, 1, 0, 0, 0, 0);
	private final DateTime lastDayOfJune = firstDayOfJune.dayOfMonth().withMaximumValue();
	private final DateTime firstDayOfJuly = firstDayOfJune.plusMonths(1);

	private Account account = new Account();

	@Before
	public void setUp() throws Exception {
		TestApp.Companion.getTestComponent().inject(this);
		App.Companion.resetDatabase();

		account.setName("Account");
		accountDataSource.save(account);
	}

	private br.com.jonathanzanella.myexpenses.expense.Expense newExpense(String name, DateTime date, int value) {
		br.com.jonathanzanella.myexpenses.expense.Expense expense = new br.com.jonathanzanella.myexpenses.expense.Expense();
		expense.setName(name);
		expense.setChargeable(account);
		expense.setDate(date);
		expense.setValue(value);
		expense.setRemoved(false);
		expense.setIgnoreInOverview(false);
		return expense;
	}

	@Test
	public void testExpensesInPeriod() {
		br.com.jonathanzanella.myexpenses.expense.Expense firstOfMonth = newExpense("First", firstDayOfJune, 1000);
		expenseDataSource.save(firstOfMonth);

		br.com.jonathanzanella.myexpenses.expense.Expense endOfMonth = newExpense("End", lastDayOfJune.withHourOfDay(23), 500);
		expenseDataSource.save(endOfMonth);

		br.com.jonathanzanella.myexpenses.expense.Expense firstOfJuly = newExpense("July", firstDayOfJuly, 200);
		expenseDataSource.save(firstOfJuly);

		WeeklyPagerAdapter.Period period = new WeeklyPagerAdapter.Period();
		period.setInit(firstDayOfJune);
		period.setEnd(lastDayOfJune);

		List<br.com.jonathanzanella.myexpenses.expense.Expense> expenses = expenseDataSource.expenses(period, null);
		assertThat(expenses.size(), is(2));
		assertThat(expenses.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(expenses.get(1).getUuid(), is(endOfMonth.getUuid()));
	}

	@Test
	public void testExpensesInPeriodWeekly() {
		br.com.jonathanzanella.myexpenses.expense.Expense firstOfMonth = newExpense("First", firstDayOfJune, 1000);
		expenseDataSource.save(firstOfMonth);

		br.com.jonathanzanella.myexpenses.expense.Expense sixOfMonth = newExpense("Six", firstDayOfJune.plusDays(6), 850);
		expenseDataSource.save(sixOfMonth);

		br.com.jonathanzanella.myexpenses.expense.Expense sevenOfMonth = newExpense("Seven", firstDayOfJune.plusDays(7), 900);
		expenseDataSource.save(sevenOfMonth);

		br.com.jonathanzanella.myexpenses.expense.Expense endOfMonth = newExpense("End", lastDayOfJune.withHourOfDay(23), 500);
		expenseDataSource.save(endOfMonth);

		br.com.jonathanzanella.myexpenses.expense.Expense firstOfJuly = newExpense("July", firstDayOfJuly, 200);
		expenseDataSource.save(firstOfJuly);

		WeeklyPagerAdapter.Period period = new WeeklyPagerAdapter.Period();
		period.setInit(firstDayOfJune);
		period.setEnd(firstDayOfJune.plusDays(6));

		List<br.com.jonathanzanella.myexpenses.expense.Expense> expenses = expenseDataSource.expenses(period, null);
		assertThat(expenses.size(), is(2));
		assertThat(expenses.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(expenses.get(1).getUuid(), is(sixOfMonth.getUuid()));
	}

	@Test
	public void testExpensesInMonth() {
		br.com.jonathanzanella.myexpenses.expense.Expense firstOfMonth = newExpense("First", firstDayOfJune, 1000);
		expenseDataSource.save(firstOfMonth);

		br.com.jonathanzanella.myexpenses.expense.Expense endOfMonth = newExpense("End", lastDayOfJune.withHourOfDay(23), 500);
		expenseDataSource.save(endOfMonth);

		br.com.jonathanzanella.myexpenses.expense.Expense firstOfJuly = newExpense("July", firstDayOfJuly, 200);
		expenseDataSource.save(firstOfJuly);

		List<br.com.jonathanzanella.myexpenses.expense.Expense> expenses = expenseDataSource.expensesForResumeScreen(firstDayOfJune);
		assertThat(expenses.size(), is(2));
		assertThat(expenses.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(expenses.get(1).getUuid(), is(endOfMonth.getUuid()));
	}
}