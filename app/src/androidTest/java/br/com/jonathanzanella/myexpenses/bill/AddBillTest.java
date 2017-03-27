package br.com.jonathanzanella.myexpenses.bill;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
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
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchErrorMessage;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.openMenuAndClickItem;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.setTimeInDatePicker;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.typeTextIntoView;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class AddBillTest {
	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
	@Rule
	public ActivityTestRule<EditBillActivity> editBillActivityTestRule = new ActivityTestRule<>(EditBillActivity.class);

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();

		UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
		if (!uiDevice.isScreenOn())
			uiDevice.wakeUp();
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void add_new_bill() throws InterruptedException {
		activityTestRule.launchActivity(new Intent());

		openMenuAndClickItem(R.string.bills);

		final String billsTitle = getContext().getString(R.string.bills);
		matchToolbarTitle(billsTitle);

		clickIntoView(R.id.view_bills_fab);

		final String newBillTitle = getContext().getString(R.string.new_bill_title);
		matchToolbarTitle(newBillTitle);

		final String billTitle = "Test";
		typeTextIntoView(R.id.act_edit_bill_name, billTitle);
		typeTextIntoView(R.id.act_edit_bill_amount, "100");
		typeTextIntoView(R.id.act_edit_bill_due_date, "1");

		clickIntoView(R.id.act_edit_bill_init_date);
		DateTime initDate = new DateTime(2016, 10, 2, 0, 0);
		setTimeInDatePicker(initDate.getYear(), initDate.getMonthOfYear(), initDate.getDayOfMonth());
		clickIntoView(R.id.act_edit_bill_end_date);
		DateTime endDate = new DateTime(2016, 10, 27, 0, 0);
		setTimeInDatePicker(endDate.getYear(), endDate.getMonthOfYear(), endDate.getDayOfMonth());

		clickIntoView(R.id.action_save);

		matchToolbarTitle(billsTitle);

		onView(withId(R.id.row_bill_name)).check(matches(withText(billTitle)));
		String initDateText = Bill.SIMPLE_DATE_FORMAT.format(initDate.toDate());
		onView(withId(R.id.row_bill_init_date)).check(matches(withText(initDateText)));
		String endDateText = Bill.SIMPLE_DATE_FORMAT.format(endDate.toDate());
		onView(withId(R.id.row_bill_end_date)).check(matches(withText(endDateText)));
	}

	@Test
	public void add_new_bill_shows_error_with_empty_data() {
		editBillActivityTestRule.launchActivity(new Intent());

		final String newBillTitle = getContext().getString(R.string.new_bill_title);
		matchToolbarTitle(newBillTitle);

		clickIntoView(R.id.action_save);

		String errorMessage = getContext().getString(R.string.error_message_name_not_informed);
		matchErrorMessage(R.id.act_edit_bill_name, errorMessage);
		errorMessage = getContext().getString(R.string.error_message_amount_zero);
		matchErrorMessage(R.id.act_edit_bill_amount, errorMessage);
		errorMessage = getContext().getString(R.string.error_message_due_date_not_informed);
		matchErrorMessage(R.id.act_edit_bill_due_date, errorMessage);
		errorMessage = getContext().getString(R.string.error_message_init_date_not_informed);
		matchErrorMessage(R.id.act_edit_bill_init_date, errorMessage);
		errorMessage = getContext().getString(R.string.error_message_end_date_not_informed);
		matchErrorMessage(R.id.act_edit_bill_end_date, errorMessage);
	}

	@Test
	public void add_new_bill_shows_error_with_init_date_greater_than_end_date() {
		editBillActivityTestRule.launchActivity(new Intent());

		final String newBillTitle = getContext().getString(R.string.new_bill_title);
		matchToolbarTitle(newBillTitle);

		clickIntoView(R.id.act_edit_bill_init_date);
		DateTime initDate = new DateTime(2016, 10, 3, 0, 0);
		setTimeInDatePicker(initDate.getYear(), initDate.getMonthOfYear(), initDate.getDayOfMonth());
		clickIntoView(R.id.act_edit_bill_end_date);
		DateTime endDate = new DateTime(2016, 10, 2, 0, 0);
		setTimeInDatePicker(endDate.getYear(), endDate.getMonthOfYear(), endDate.getDayOfMonth());

		clickIntoView(R.id.action_save);

		String errorMessage = getContext().getString(R.string.error_message_init_date_greater_than_end_date);
		matchErrorMessage(R.id.act_edit_bill_init_date, errorMessage);
	}

	private Context getContext() {
		return InstrumentationRegistry.getTargetContext();
	}
}
