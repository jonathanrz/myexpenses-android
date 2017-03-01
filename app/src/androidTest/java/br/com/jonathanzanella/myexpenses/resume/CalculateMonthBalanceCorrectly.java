package br.com.jonathanzanella.myexpenses.resume;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.bill.BillRepository;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.BillBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CalculateMonthBalanceCorrectly {
	private static final int EXPENSE_VALUE = 100;
	private static final int RECEIPT_INCOME = 200;
	private static final int BILL_AMOUNT = 25;

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Before
	public void setUp() throws Exception {
		Account a = new AccountBuilder().build();
		assertTrue(new AccountRepository(new Repository<Account>(getTargetContext())).save(a).isValid());

		Source s = new SourceBuilder().build();
		assertTrue(new SourceRepository(new Repository<Source>(getTargetContext())).save(s).isValid());

		DateTime now = DateTime.now().withDayOfMonth(1);
		Bill b = new BillBuilder()
				.initDate(now)
				.endDate(now.plusMonths(12))
				.amount(BILL_AMOUNT)
				.build();
		assertTrue(new BillRepository(new Repository<Bill>(getTargetContext()),
				new ExpenseRepository(new Repository<Expense>(getTargetContext()))).save(b).isValid());

		generateThreeMonthlyReceipts(a, s);
		generateThreeMonthlyExpenses(a);
	}

	@After
	public void tearDown() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	private void generateThreeMonthlyReceipts(Account a, Source s) {
		DateTime dateTime = DateTime.now();
		ReceiptRepository receiptRepository = new ReceiptRepository(new Repository<Receipt>(getTargetContext()));
		Receipt r = new ReceiptBuilder()
				.account(a)
				.source(s)
				.date(dateTime)
				.income(RECEIPT_INCOME)
				.build();
		assertTrue(receiptRepository.save(r).isValid());
		dateTime = dateTime.plusMonths(1);
		r = new ReceiptBuilder()
				.account(a)
				.source(s)
				.date(dateTime)
				.income(RECEIPT_INCOME)
				.build();
		assertTrue(receiptRepository.save(r).isValid());
		dateTime = dateTime.plusMonths(1);
		r = new ReceiptBuilder()
				.account(a)
				.source(s)
				.date(dateTime)
				.income(RECEIPT_INCOME)
				.build();
		assertTrue(receiptRepository.save(r).isValid());
	}

	private void generateThreeMonthlyExpenses(Account a) {
		DateTime dateTime = DateTime.now();
		ExpenseRepository expenseRepository = new ExpenseRepository(new Repository<Expense>(getTargetContext()));
		Expense r = new ExpenseBuilder()
				.chargeable(a)
				.date(dateTime)
				.value(EXPENSE_VALUE)
				.build();
		assertTrue(expenseRepository.save(r).isValid());
		dateTime = dateTime.plusMonths(1);
		r = new ExpenseBuilder()
				.chargeable(a)
				.date(dateTime)
				.value(EXPENSE_VALUE)
				.build();
		assertTrue(expenseRepository.save(r).isValid());
		dateTime = dateTime.plusMonths(1);
		r = new ExpenseBuilder()
				.chargeable(a)
				.date(dateTime)
				.value(EXPENSE_VALUE)
				.build();
		assertTrue(expenseRepository.save(r).isValid());
	}

	@Test
	public void verify_month_balance() throws Exception {
		activityTestRule.launchActivity(new Intent());
		MonthlyPagerAdapterHelper monthlyPagerAdapterHelper = new MonthlyPagerAdapterHelper();

		int balance = RECEIPT_INCOME - EXPENSE_VALUE - BILL_AMOUNT;
		String expectedBalance = NumberFormat.getCurrencyInstance().format((balance/100.0));

		onView(allOf(
				withId(R.id.view_monthly_resume_balance),
				isDisplayed()))
				.check(matches(withText(expectedBalance)));

		String twoMonthsFromNow = monthlyPagerAdapterHelper.formatMonthForView(DateTime.now().plusMonths(2));
		clickIntoView(twoMonthsFromNow);

		onView(allOf(
				withId(R.id.view_monthly_resume_balance),
				isDisplayed()))
				.check(matches(withText(expectedBalance)));
	}
}
