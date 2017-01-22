package br.com.jonathanzanella.myexpenses.expense;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.bill.BillRepository;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.FlowManagerHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.BillBuilder;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchErrorMessage;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.openMenuAndClickItem;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.typeTextIntoView;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by jzanella on 7/24/16.
 */
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
		UiDevice uiDevice = UiDevice.getInstance(getInstrumentation());
		if (!uiDevice.isScreenOn())
			uiDevice.wakeUp();

		account = new AccountBuilder().build();
		new AccountRepository(new Repository<Account>(MyApplication.getContext())).save(account);
	}

	@After
	public void tearDown() throws Exception {
		FlowManagerHelper.reset(getContext());
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void add_new_expense() {
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
		selectChargeable();

		clickIntoView(R.id.action_save);

		matchToolbarTitle(expensesTitle);

		onView(withId(R.id.row_expense_name)).check(matches(withText(expenseName)));
		onView(withId(R.id.row_expense_bill_layout)).check(matches(not(isDisplayed())));
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
		new BillRepository(new Repository<Bill>(getTargetContext())).save(bill);

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

		onView(withId(R.id.row_expense_name)).check(matches(withText(bill.getName())));
		onView(withId(R.id.row_expense_bill_layout)).check(matches(isDisplayed()));
		onView(withId(R.id.row_expense_bill)).check(matches(withText(bill.getName())));
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
