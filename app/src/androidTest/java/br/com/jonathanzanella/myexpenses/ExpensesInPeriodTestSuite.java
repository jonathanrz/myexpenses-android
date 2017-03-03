package br.com.jonathanzanella.myexpenses;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.overview.WeeklyPagerAdapter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ExpensesInPeriodTestSuite {
	private AccountRepository accountRepository = new AccountRepository(new RepositoryImpl<Account>(MyApplication.getContext()));
	private ExpenseRepository expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(MyApplication.getContext()));
	private DateTime firstDayOfJune = new DateTime(2016, 6, 1, 0, 0, 0, 0);
	private DateTime lastDayOfJune = firstDayOfJune.dayOfMonth().withMaximumValue();
	private DateTime firstDayOfJuly = firstDayOfJune.plusMonths(1);

	private Account account = new Account();

	@Before
	public void setUp() throws Exception {
		account.setName("Account");
		account.setUserUuid(Environment.CURRENT_USER_UUID);
		accountRepository.save(account);
	}

	@After
	public void tearDown() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();
	}

	private Expense newExpense(String name, DateTime date, int value) {
		Expense expense = new Expense();
		expense.setUserUuid(Environment.CURRENT_USER_UUID);
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
		expenseRepository.save(firstOfMonth);

		Expense endOfMonth = newExpense("End", lastDayOfJune.withHourOfDay(23), 500);
		expenseRepository.save(endOfMonth);

		Expense firstOfJuly = newExpense("July", firstDayOfJuly, 200);
		expenseRepository.save(firstOfJuly);

		WeeklyPagerAdapter.Period period = new WeeklyPagerAdapter.Period();
		period.init = firstDayOfJune;
		period.end = lastDayOfJune;

		List<Expense> expenses = expenseRepository.expenses(period);
		assertThat(expenses.size(), is(2));
		assertThat(expenses.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(expenses.get(1).getUuid(), is(endOfMonth.getUuid()));
	}

	@Test
	public void testExpensesInPeriodWeekly() {
		Expense firstOfMonth = newExpense("First", firstDayOfJune, 1000);
		expenseRepository.save(firstOfMonth);

		Expense sixOfMonth = newExpense("Six", firstDayOfJune.plusDays(6), 850);
		expenseRepository.save(sixOfMonth);

		Expense sevenOfMonth = newExpense("Seven", firstDayOfJune.plusDays(7), 900);
		expenseRepository.save(sevenOfMonth);

		Expense endOfMonth = newExpense("End", lastDayOfJune.withHourOfDay(23), 500);
		expenseRepository.save(endOfMonth);

		Expense firstOfJuly = newExpense("July", firstDayOfJuly, 200);
		expenseRepository.save(firstOfJuly);

		WeeklyPagerAdapter.Period period = new WeeklyPagerAdapter.Period();
		period.init = firstDayOfJune;
		period.end = firstDayOfJune.plusDays(6);

		List<Expense> expenses = expenseRepository.expenses(period);
		assertThat(expenses.size(), is(2));
		assertThat(expenses.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(expenses.get(1).getUuid(), is(sixOfMonth.getUuid()));
	}

	@Test
	public void testExpensesInMonth() {
		Expense firstOfMonth = newExpense("First", firstDayOfJune, 1000);
		expenseRepository.save(firstOfMonth);

		Expense endOfMonth = newExpense("End", lastDayOfJune.withHourOfDay(23), 500);
		expenseRepository.save(endOfMonth);

		Expense firstOfJuly = newExpense("July", firstDayOfJuly, 200);
		expenseRepository.save(firstOfJuly);

		List<Expense> expenses = expenseRepository.expensesForResumeScreen(firstDayOfJune);
		assertThat(expenses.size(), is(2));
		assertThat(expenses.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(expenses.get(1).getUuid(), is(endOfMonth.getUuid()));
	}
}