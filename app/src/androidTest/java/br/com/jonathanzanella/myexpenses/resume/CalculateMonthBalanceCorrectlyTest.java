package br.com.jonathanzanella.myexpenses.resume;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.bill.BillRepository;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
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
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class CalculateMonthBalanceCorrectlyTest {
	private static final int EXPENSE_VALUE = 100;
	private static final int RECEIPT_INCOME = 200;
	private static final int BILL_AMOUNT = 25;

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	private final MonthlyPagerAdapterHelper monthlyPagerAdapterHelper = new MonthlyPagerAdapterHelper();

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();

		Account a = new AccountBuilder().build();
		assertTrue(new AccountRepository().save(a).isValid());

		Source s = new SourceBuilder().build();
		assertTrue(new SourceRepository().save(s).isValid());

		DateTime now = DateTime.now().withDayOfMonth(1);
		Bill b = new BillBuilder()
				.initDate(now)
				.endDate(now.plusMonths(12))
				.amount(BILL_AMOUNT)
				.build();
		assertTrue(new BillRepository(new ExpenseRepository(new RepositoryImpl<>(getTargetContext()))).save(b).isValid());

		generateThreeMonthlyReceipts(a, s);
		generateThreeMonthlyExpenses(a);
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	private void generateThreeMonthlyReceipts(Account a, Source s) {
		DateTime dateTime = DateTime.now();
		ReceiptRepository receiptRepository = new ReceiptRepository(new RepositoryImpl<>(getTargetContext()));
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
				.income(RECEIPT_INCOME * 2)
				.build();
		assertTrue(receiptRepository.save(r).isValid());
		dateTime = dateTime.plusMonths(1);
		r = new ReceiptBuilder()
				.account(a)
				.source(s)
				.date(dateTime)
				.income(RECEIPT_INCOME * 3)
				.build();
		assertTrue(receiptRepository.save(r).isValid());
	}

	private void generateThreeMonthlyExpenses(Account a) {
		DateTime dateTime = DateTime.now();
		ExpenseRepository expenseRepository = new ExpenseRepository(new RepositoryImpl<>(getTargetContext()));
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
				.value(EXPENSE_VALUE * 2)
				.build();
		assertTrue(expenseRepository.save(r).isValid());
		dateTime = dateTime.plusMonths(1);
		r = new ExpenseBuilder()
				.chargeable(a)
				.date(dateTime)
				.value(EXPENSE_VALUE * 3)
				.build();
		assertTrue(expenseRepository.save(r).isValid());
	}

	@Test
	public void verify_month_balance() throws Exception {
		activityTestRule.launchActivity(new Intent());

		int balance = RECEIPT_INCOME - EXPENSE_VALUE - BILL_AMOUNT;
		int twoMonthsBalance = RECEIPT_INCOME * 3 - EXPENSE_VALUE * 3 - BILL_AMOUNT;
		String expectedBalance = CurrencyHelper.INSTANCE.format(balance);
		String twoMonthsExpectedBalance = CurrencyHelper.INSTANCE.format(twoMonthsBalance);

		validateExpectedBalance(expectedBalance);

		scrollToMonth(DateTime.now().plusMonths(2));
		validateExpectedBalance(twoMonthsExpectedBalance);

		scrollToMonth(DateTime.now());
		validateExpectedBalance(expectedBalance);
	}

	private void validateExpectedBalance(String expectedBalance) {
		onView(allOf(
				withId(R.id.balance),
				isDescendantOfA(allOf(
						withId(R.id.view_monthly_resume),
						isDisplayed()))))
				.check(matches(withText(expectedBalance)));
	}

	private void scrollToMonth(DateTime dateTime) {
		String twoMonthsFromNow = monthlyPagerAdapterHelper.formatMonthForView(dateTime);
		clickIntoView(twoMonthsFromNow);
	}
}
