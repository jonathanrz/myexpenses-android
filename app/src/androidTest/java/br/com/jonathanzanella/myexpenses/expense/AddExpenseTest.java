package br.com.jonathanzanella.myexpenses.expense;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

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
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.BillBuilder;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clearAndTypeTextIntoView;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchErrorMessage;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.openMenuAndClickItem;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.setTimeInDatePicker;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.typeTextIntoView;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddExpenseTest {
	@Rule
	public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);
	@Rule
	public ActivityTestRule<EditExpenseActivity> editExpenseActivityTestRule = new ActivityTestRule<>(EditExpenseActivity.class);

	private Account account;

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();

		UiDevice uiDevice = UiDevice.getInstance(getInstrumentation());
		if (!uiDevice.isScreenOn())
			uiDevice.wakeUp();

		account = new AccountBuilder().build();
		new AccountRepository(new RepositoryImpl<Account>(getTargetContext())).save(account);
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void add_new_expense() throws InterruptedException {
		mainActivityTestRule.launchActivity(new Intent());

		openMenuAndClickItem(R.string.expenses);

		final String expensesTitle = getContext().getString(R.string.expenses);
		matchToolbarTitle(expensesTitle);

		clickIntoView(R.id.view_expenses_fab);

		final String newExpenseTitle = getContext().getString(R.string.new_expense_title);
		matchToolbarTitle(newExpenseTitle);

		final String expenseName = "Test";
		typeTextIntoView(R.id.act_edit_expense_name, expenseName);
		typeTextIntoView(R.id.act_edit_expense_value, "100");
		clickIntoView(R.id.act_edit_expense_value_to_show_in_overview);
		clickIntoView(R.id.act_edit_expense_date);
		DateTime time = DateTime.now().plusMonths(1);
		setTimeInDatePicker(time.getYear(), time.getMonthOfYear(), time.getDayOfMonth());
		selectChargeable();

		onView(withId(R.id.act_edit_expense_date))
				.check(matches(withText(Transaction.Companion.getSIMPLE_DATE_FORMAT().format(time.toDate()))));

		clickIntoView(R.id.action_save);

		matchToolbarTitle(expensesTitle);

		onView(withId(R.id.name)).check(matches(withText(expenseName)));
		onView(withId(R.id.billLayout)).check(matches(not(isDisplayed())));
		onView(withId(R.id.date)).check(matches(withText(Transaction.Companion.getSIMPLE_DATE_FORMAT().format(time.toDate()))));
	}

	@Test
	public void add_new_expense_shows_error_with_empty_name() {
		editExpenseActivityTestRule.launchActivity(new Intent());

		final String newExpenseTitle = getContext().getString(R.string.new_expense_title);
		matchToolbarTitle(newExpenseTitle);

		clickIntoView(R.id.action_save);

		final String errorMessage = getContext().getString(R.string.error_message_name_not_informed);
		matchErrorMessage(R.id.act_edit_expense_name, errorMessage);
	}

	@Test
	public void add_new_expense_shows_error_without_value() {
		editExpenseActivityTestRule.launchActivity(new Intent());

		final String newExpenseTitle = getContext().getString(R.string.new_expense_title);
		matchToolbarTitle(newExpenseTitle);

		clickIntoView(R.id.action_save);

		final String errorMessage = getContext().getString(R.string.error_message_amount_zero);
		matchErrorMessage(R.id.act_edit_expense_value, errorMessage);
	}

	@Test
	public void add_new_expense_shows_error_with_empty_chargeable() {
		editExpenseActivityTestRule.launchActivity(new Intent());

		final String newExpenseTitle = getContext().getString(R.string.new_expense_title);
		matchToolbarTitle(newExpenseTitle);

		clickIntoView(R.id.action_save);

		final String errorMessage = getContext().getString(R.string.error_message_chargeable_not_informed);
		matchErrorMessage(R.id.act_edit_expense_chargeable, errorMessage);
	}

	@Test
	public void add_new_expense_with_bill() throws Exception {
		Bill bill = new BillBuilder().build();
		ExpenseRepository expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(getTargetContext()));
		new BillRepository(new RepositoryImpl<Bill>(getTargetContext()), expenseRepository).save(bill);

		mainActivityTestRule.launchActivity(new Intent());

		openMenuAndClickItem(R.string.expenses);

		final String expensesTitle = getContext().getString(R.string.expenses);
		matchToolbarTitle(expensesTitle);

		clickIntoView(R.id.view_expenses_fab);

		final String newExpenseTitle = getContext().getString(R.string.new_expense_title);
		matchToolbarTitle(newExpenseTitle);

		selectBill(bill);
		selectChargeable();

		clickIntoView(R.id.action_save);

		matchToolbarTitle(expensesTitle);

		onView(withId(R.id.name)).check(matches(withText(bill.getName())));
		onView(withId(R.id.billLayout)).check(matches(isDisplayed()));
		onView(withId(R.id.bill)).check(matches(withText(bill.getName())));
	}

	@Test
	public void add_new_expense_with_reimburse() throws InterruptedException {
		mainActivityTestRule.launchActivity(new Intent());

		openMenuAndClickItem(R.string.expenses);

		final String expensesTitle = getContext().getString(R.string.expenses);
		matchToolbarTitle(expensesTitle);

		clickIntoView(R.id.view_expenses_fab);

		final String newExpenseTitle = getContext().getString(R.string.new_expense_title);
		matchToolbarTitle(newExpenseTitle);

		final String expenseName = "Test";
		int value = 100;
		clearAndTypeTextIntoView(R.id.act_edit_expense_name, expenseName);
		clearAndTypeTextIntoView(R.id.act_edit_expense_value, String.valueOf(value));
		clickIntoView(R.id.act_edit_expense_repayment);
		selectChargeable();

		clickIntoView(R.id.action_save);

		matchToolbarTitle(expensesTitle);

		onView(withId(R.id.name)).check(matches(withText(expenseName)));
		String expectedValue = CurrencyHelper.format(value * -1);
		onView(withId(R.id.value)).check(matches(withText(expectedValue)));
	}

	private void selectChargeable() {
		final String selectChargeableTitle = getContext().getString(R.string.select_chargeable_title);
		clickIntoView(R.id.act_edit_expense_chargeable);
		matchToolbarTitle(selectChargeableTitle);
		clickIntoView(account.getName());
	}

	private void selectBill(Bill bill) {
		final String title = getContext().getString(R.string.select_bill_title);
		clickIntoView(R.id.act_edit_expense_bill);
		matchToolbarTitle(title);
		clickIntoView(bill.getName());
	}

	private Context getContext() {
		return InstrumentationRegistry.getTargetContext();
	}
}
