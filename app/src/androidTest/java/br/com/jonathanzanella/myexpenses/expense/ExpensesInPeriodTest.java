package br.com.jonathanzanella.myexpenses.expense;

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
	ExpenseDataSource expenseDataSource;
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

	private Expense newExpense(String name, DateTime date, int value) {
		Expense expense = new Expense();
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
		Expense firstOfMonth = newExpense("First", firstDayOfJune, 1000);
		expenseDataSource.save(firstOfMonth);

		Expense endOfMonth = newExpense("End", lastDayOfJune.withHourOfDay(23), 500);
		expenseDataSource.save(endOfMonth);

		Expense firstOfJuly = newExpense("July", firstDayOfJuly, 200);
		expenseDataSource.save(firstOfJuly);

		WeeklyPagerAdapter.Period period = new WeeklyPagerAdapter.Period();
		period.setInit(firstDayOfJune);
		period.setEnd(lastDayOfJune);

		List<Expense> expenses = expenseDataSource.expenses(period, null);
		assertThat(expenses.size(), is(2));
		assertThat(expenses.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(expenses.get(1).getUuid(), is(endOfMonth.getUuid()));
	}

	@Test
	public void testExpensesInPeriodWeekly() {
		Expense firstOfMonth = newExpense("First", firstDayOfJune, 1000);
		expenseDataSource.save(firstOfMonth);

		Expense sixOfMonth = newExpense("Six", firstDayOfJune.plusDays(6), 850);
		expenseDataSource.save(sixOfMonth);

		Expense sevenOfMonth = newExpense("Seven", firstDayOfJune.plusDays(7), 900);
		expenseDataSource.save(sevenOfMonth);

		Expense endOfMonth = newExpense("End", lastDayOfJune.withHourOfDay(23), 500);
		expenseDataSource.save(endOfMonth);

		Expense firstOfJuly = newExpense("July", firstDayOfJuly, 200);
		expenseDataSource.save(firstOfJuly);

		WeeklyPagerAdapter.Period period = new WeeklyPagerAdapter.Period();
		period.setInit(firstDayOfJune);
		period.setEnd(firstDayOfJune.plusDays(6));

		List<Expense> expenses = expenseDataSource.expenses(period, null);
		assertThat(expenses.size(), is(2));
		assertThat(expenses.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(expenses.get(1).getUuid(), is(sixOfMonth.getUuid()));
	}

	@Test
	public void testExpensesInMonth() {
		Expense firstOfMonth = newExpense("First", firstDayOfJune, 1000);
		expenseDataSource.save(firstOfMonth);

		Expense endOfMonth = newExpense("End", lastDayOfJune.withHourOfDay(23), 500);
		expenseDataSource.save(endOfMonth);

		Expense firstOfJuly = newExpense("July", firstDayOfJuly, 200);
		expenseDataSource.save(firstOfJuly);

		List<Expense> expenses = expenseDataSource.expensesForResumeScreen(firstDayOfJune);
		assertThat(expenses.size(), is(2));
		assertThat(expenses.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(expenses.get(1).getUuid(), is(endOfMonth.getUuid()));
	}
}