package br.com.jonathanzanella.myexpenses.ui.receipt;

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

import br.com.jonathanzanella.myexpenses.App;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountDataSource;
import br.com.jonathanzanella.myexpenses.ui.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceDataSource;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.clearAndTypeTextIntoView;
import static br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.clickIntoView;
import static br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.matchErrorMessage;
import static br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.matchToolbarTitle;
import static br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.openMenuAndClickItem;
import static br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.setTimeInDatePicker;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddReceiptTest {
	@Rule
	public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);
	@Rule
	public ActivityTestRule<br.com.jonathanzanella.myexpenses.receipt.EditReceiptActivity> editReceiptActivityTestRule = new ActivityTestRule<>(br.com.jonathanzanella.myexpenses.receipt.EditReceiptActivity.class);

	private Account account;
	private Source source;

	@Before
	public void setUp() throws Exception {
		App.Companion.resetDatabase();
		AccountDataSource accountDataSource = App.Companion.getApp().appComponent.accountDataSource();
		SourceDataSource sourceDataSource = App.Companion.getApp().appComponent.sourceDataSource();

		UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
		if (!uiDevice.isScreenOn())
			uiDevice.wakeUp();

		account = new AccountBuilder().build();
		assertTrue(accountDataSource.save(account).blockingFirst().isValid());

		source = new SourceBuilder().build();
		assertTrue(sourceDataSource.save(source).isValid());
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void add_new_receipt() throws InterruptedException {
		mainActivityTestRule.launchActivity(new Intent());

		openMenuAndClickItem(R.string.receipts);

		final String receiptsTitle = getContext().getString(R.string.receipts);
		matchToolbarTitle(receiptsTitle);

		clickIntoView(R.id.view_receipts_fab);

		final String newReceiptTitle = getContext().getString(R.string.new_receipt_title);
		matchToolbarTitle(newReceiptTitle);

		final String receiptName = "Test";
		clearAndTypeTextIntoView(R.id.act_edit_receipt_name, receiptName);
		clearAndTypeTextIntoView(R.id.act_edit_receipt_income, "100");
		clickIntoView(R.id.act_edit_receipt_date);
		DateTime time = DateTime.now().plusMonths(1);
		setTimeInDatePicker(time.getYear(), time.getMonthOfYear(), time.getDayOfMonth());

		selectAccount();
		selectSource();
		matchToolbarTitle(newReceiptTitle);

		String formattedDate = Transaction.Companion.getSIMPLE_DATE_FORMAT().format(time.toDate());
		onView(withId(R.id.act_edit_receipt_date)).check(matches(withText(formattedDate)));

		clickIntoView(R.id.action_save);

		matchToolbarTitle(receiptsTitle);

		onView(withId(R.id.row_receipt_name)).check(matches(withText(receiptName)));
		onView(withId(R.id.row_receipt_date)).check(matches(withText(formattedDate)));
	}

	@Test
	public void add_new_receipt_shows_error_with_empty_name() {
		editReceiptActivityTestRule.launchActivity(new Intent());

		final String newReceiptTitle = getContext().getString(R.string.new_receipt_title);
		matchToolbarTitle(newReceiptTitle);

		clickIntoView(R.id.action_save);

		final String errorMessage = getContext().getString(R.string.error_message_name_not_informed);
		matchErrorMessage(R.id.act_edit_receipt_name, errorMessage);
	}

	@Test
	public void add_new_receipt_shows_error_with_empty_income() {
		editReceiptActivityTestRule.launchActivity(new Intent());

		final String newReceiptTitle = getContext().getString(R.string.new_receipt_title);
		matchToolbarTitle(newReceiptTitle);

		clickIntoView(R.id.action_save);

		final String errorMessage = getContext().getString(R.string.error_message_amount_zero);
		matchErrorMessage(R.id.act_edit_receipt_income, errorMessage);
	}

	@Test
	public void add_new_receipt_shows_error_with_empty_source() {
		editReceiptActivityTestRule.launchActivity(new Intent());

		final String newReceiptTitle = getContext().getString(R.string.new_receipt_title);
		matchToolbarTitle(newReceiptTitle);

		clickIntoView(R.id.action_save);

		final String errorMessage = getContext().getString(R.string.error_message_source_not_informed);
		matchErrorMessage(R.id.act_edit_receipt_source, errorMessage);
	}

	@Test
	public void add_new_receipt_shows_error_with_empty_account() {
		editReceiptActivityTestRule.launchActivity(new Intent());

		final String newReceiptTitle = getContext().getString(R.string.new_receipt_title);
		matchToolbarTitle(newReceiptTitle);

		clickIntoView(R.id.action_save);

		final String errorMessage = getContext().getString(R.string.error_message_account_not_informed);
		matchErrorMessage(R.id.act_edit_receipt_account, errorMessage);
	}

	private void selectSource() {
		final String selectSourceTitle = getContext().getString(R.string.select_source_title);
		clickIntoView(R.id.act_edit_receipt_source);
		matchToolbarTitle(selectSourceTitle);
		clickIntoView(source.getName());
	}

	private void selectAccount() {
		final String selectAccountTitle = getContext().getString(R.string.select_account_title);
		clickIntoView(R.id.act_edit_receipt_account);
		matchToolbarTitle(selectAccountTitle);
		clickIntoView(account.getName());
	}

	private Context getContext() {
		return getTargetContext();
	}
}