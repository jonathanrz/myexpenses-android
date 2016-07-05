package br.com.jonathanzanella.myexpenses;

import android.support.test.runner.AndroidJUnit4;

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
public class ApplicationTest {
	List<Expense> expensesToDelete = new ArrayList<>();
	DateTime firstDayOfJune = new DateTime(2016, 6, 1, 0, 0, 0, 0);
	DateTime lastDayOfJune = firstDayOfJune.dayOfMonth().withMaximumValue();
	DateTime firstDayOfJuly = firstDayOfJune.plusMonths(1);

	Account account = new Account();

	@Before
	public void setUp() throws Exception {
		account.setName("teste");
		account.save();
	}

	@After
	public void tearDown() throws Exception {
		account.delete();

		for (Expense expense : expensesToDelete) {
			expense.delete();
		}

	}

	@Test
	public void testExpensesInPeriod() {
		Expense firstOfMonth = new Expense();
		firstOfMonth.setName("First");
		firstOfMonth.setChargeable(account);
		firstOfMonth.setDate(firstDayOfJune);
		firstOfMonth.setValue(1000);
		firstOfMonth.save();
		expensesToDelete.add(firstOfMonth);

		Expense endOfMonth = new Expense();
		endOfMonth.setName("End");
		endOfMonth.setChargeable(account);
		endOfMonth.setDate(lastDayOfJune.withHourOfDay(23));
		endOfMonth.setValue(500);
		endOfMonth.save();
		expensesToDelete.add(endOfMonth);

		Expense firstOfJuly = new Expense();
		firstOfJuly.setName("July");
		firstOfJuly.setChargeable(account);
		firstOfJuly.setDate(firstDayOfJuly);
		firstOfJuly.setValue(200);
		firstOfJuly.save();
		expensesToDelete.add(firstOfJuly);

		WeeklyPagerAdapter.Period period = new WeeklyPagerAdapter.Period();
		period.init = firstDayOfJune;
		period.end = lastDayOfJune;

		List<Expense> expenses = Expense.expenses(period);
		assertThat(expenses.size(), is(2));
		assertThat(expenses.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(expenses.get(1).getUuid(), is(endOfMonth.getUuid()));
	}

	@Test
	public void testExpensesInPeriodWeekly() {
		Expense firstOfMonth = new Expense();
		firstOfMonth.setName("First");
		firstOfMonth.setChargeable(account);
		firstOfMonth.setDate(firstDayOfJune);
		firstOfMonth.setValue(1000);
		firstOfMonth.save();
		expensesToDelete.add(firstOfMonth);

		Expense sixOfMonth = new Expense();
		sixOfMonth.setName("Six");
		sixOfMonth.setChargeable(account);
		sixOfMonth.setDate(firstDayOfJune.plusDays(6));
		sixOfMonth.setValue(1000);
		sixOfMonth.save();
		expensesToDelete.add(sixOfMonth);

		Expense sevenOfMonth = new Expense();
		sevenOfMonth.setName("Seven");
		sevenOfMonth.setChargeable(account);
		sevenOfMonth.setDate(firstDayOfJune.plusDays(7));
		sevenOfMonth.setValue(1000);
		sevenOfMonth.save();
		expensesToDelete.add(sevenOfMonth);

		Expense endOfMonth = new Expense();
		endOfMonth.setName("End");
		endOfMonth.setChargeable(account);
		endOfMonth.setDate(lastDayOfJune.withHourOfDay(23));
		endOfMonth.setValue(500);
		endOfMonth.save();
		expensesToDelete.add(endOfMonth);

		Expense firstOfJuly = new Expense();
		firstOfJuly.setName("July");
		firstOfJuly.setChargeable(account);
		firstOfJuly.setDate(firstDayOfJuly);
		firstOfJuly.setValue(200);
		firstOfJuly.save();
		expensesToDelete.add(firstOfJuly);

		WeeklyPagerAdapter.Period period = new WeeklyPagerAdapter.Period();
		period.init = firstDayOfJune;
		period.end = firstDayOfJune.plus(6);

		List<Expense> expenses = Expense.expenses(period);
		assertThat(expenses.size(), is(2));
		assertThat(expenses.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(expenses.get(1).getUuid(), is(sixOfMonth.getUuid()));
	}

	@Test
	public void testExpensesInMonth() {
		Expense firstOfMonth = new Expense();
		firstOfMonth.setName("First");
		firstOfMonth.setChargeable(account);
		firstOfMonth.setDate(firstDayOfJune);
		firstOfMonth.setValue(1000);
		firstOfMonth.save();
		expensesToDelete.add(firstOfMonth);

		Expense endOfMonth = new Expense();
		endOfMonth.setName("End");
		endOfMonth.setChargeable(account);
		endOfMonth.setDate(lastDayOfJune.withHourOfDay(23));
		endOfMonth.setValue(500);
		endOfMonth.save();
		expensesToDelete.add(endOfMonth);

		Expense firstOfJuly = new Expense();
		firstOfJuly.setName("July");
		firstOfJuly.setChargeable(account);
		firstOfJuly.setDate(firstDayOfJuly);
		firstOfJuly.setValue(200);
		firstOfJuly.save();
		expensesToDelete.add(firstOfJuly);

		List<Expense> expenses = Expense.expenses(firstDayOfJune);
		assertThat(expenses.size(), is(2));
		assertThat(expenses.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(expenses.get(1).getUuid(), is(endOfMonth.getUuid()));
	}
}