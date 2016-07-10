package br.com.jonathanzanella.myexpenses;

import android.support.test.runner.AndroidJUnit4;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.overview.WeeklyPagerAdapter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ExpensesInPeriodTestSuite {
	List<BaseModel> modelsToDestroy = new ArrayList<>();
	DateTime firstDayOfJune = new DateTime(2016, 6, 1, 0, 0, 0, 0);
	DateTime lastDayOfJune = firstDayOfJune.dayOfMonth().withMaximumValue();
	DateTime firstDayOfJuly = firstDayOfJune.plusMonths(1);

	Account account = new Account();

	@Before
	public void setUp() throws Exception {
		account.setName("Account");
		account.setUserUuid(Environment.CURRENT_USER_UUID);
		account.save();
		modelsToDestroy.add(account);
	}

	@After
	public void tearDown() throws Exception {
		for (BaseModel model : modelsToDestroy)
			model.delete();
	}

	private Expense newExpense(String name, DateTime date, int value) {
		Expense expense = new Expense();
		expense.setUserUuid(Environment.CURRENT_USER_UUID);
		expense.setName(name);
		expense.setChargeable(account);
		expense.setDate(date);
		expense.setValue(value);
		return expense;
	}

	@Test
	public void testExpensesInPeriod() {
		Expense firstOfMonth = newExpense("First", firstDayOfJune, 1000);
		firstOfMonth.save();
		modelsToDestroy.add(firstOfMonth);

		Expense endOfMonth = newExpense("End", lastDayOfJune.withHourOfDay(23), 500);
		endOfMonth.save();
		modelsToDestroy.add(endOfMonth);

		Expense firstOfJuly = newExpense("July", firstDayOfJuly, 200);
		firstOfJuly.save();
		modelsToDestroy.add(firstOfJuly);

		WeeklyPagerAdapter.Period period = new WeeklyPagerAdapter.Period();
		period.init = firstDayOfJune;
		period.end = lastDayOfJune;

		List<Expense> expenses = Expense.expenses(period);
		assertThat(expenses.size(), is(1));
		assertThat(expenses.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(expenses.get(1).getUuid(), is(endOfMonth.getUuid()));
	}

	@Test
	public void testExpensesInPeriodWeekly() {
		Expense firstOfMonth = newExpense("First", firstDayOfJune, 1000);
		firstOfMonth.save();
		modelsToDestroy.add(firstOfMonth);

		Expense sixOfMonth = newExpense("Six", firstDayOfJune.plusDays(6), 850);
		sixOfMonth.save();
		modelsToDestroy.add(sixOfMonth);

		Expense sevenOfMonth = newExpense("Seven", firstDayOfJune.plusDays(7), 900);
		sevenOfMonth.save();
		modelsToDestroy.add(sevenOfMonth);

		Expense endOfMonth = newExpense("End", lastDayOfJune.withHourOfDay(23), 500);
		endOfMonth.save();
		modelsToDestroy.add(endOfMonth);

		Expense firstOfJuly = newExpense("July", firstDayOfJuly, 200);
		firstOfJuly.save();
		modelsToDestroy.add(firstOfJuly);

		WeeklyPagerAdapter.Period period = new WeeklyPagerAdapter.Period();
		period.init = firstDayOfJune;
		period.end = firstDayOfJune.plusDays(6);

		List<Expense> expenses = Expense.expenses(period);
		assertThat(expenses.size(), is(2));
		assertThat(expenses.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(expenses.get(1).getUuid(), is(sixOfMonth.getUuid()));
	}

	@Test
	public void testExpensesInMonth() {
		Expense firstOfMonth = newExpense("First", firstDayOfJune, 1000);
		firstOfMonth.save();
		modelsToDestroy.add(firstOfMonth);

		Expense endOfMonth = newExpense("End", lastDayOfJune.withHourOfDay(23), 500);
		endOfMonth.save();
		modelsToDestroy.add(endOfMonth);

		Expense firstOfJuly = newExpense("July", firstDayOfJuly, 200);
		firstOfJuly.save();
		modelsToDestroy.add(firstOfJuly);

		List<Expense> expenses = Expense.expenses(firstDayOfJune);
		assertThat(expenses.size(), is(2));
		assertThat(expenses.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(expenses.get(1).getUuid(), is(endOfMonth.getUuid()));
	}
}