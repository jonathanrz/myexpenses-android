package br.com.jonathanzanella.myexpenses.receipt;

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
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;
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
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.typeTextIntoView;

/**
 * Created by jzanella on 7/24/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddReceiptTest {
	@Rule
	public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);
	@Rule
	public ActivityTestRule<EditReceiptActivity> editReceiptActivityTestRule = new ActivityTestRule<>(EditReceiptActivity.class);

	private Account account;
	private Source source;
	private SourceRepository sourceRepository = new SourceRepository(new Repository<Source>(getContext()));

	@Before
	public void setUp() throws Exception {
		UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
		if (!uiDevice.isScreenOn())
			uiDevice.wakeUp();

		account = new AccountBuilder().build();
		new AccountRepository(new Repository<Account>(MyApplication.getContext())).save(account);

		source = new SourceBuilder().build();
		sourceRepository.save(source);
	}

	@After
	public void tearDown() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void add_new_receipt() {
		mainActivityTestRule.launchActivity(new Intent());

		openMenuAndClickItem(R.string.receipts);

		final String receiptsTitle = getContext().getString(R.string.receipts);
		matchToolbarTitle(receiptsTitle);

		clickIntoView(R.id.view_receipts_fab);

		final String newReceiptTitle = getContext().getString(R.string.new_receipt_title);
		matchToolbarTitle(newReceiptTitle);

		final String receiptName = "Test";
		typeTextIntoView(R.id.act_edit_receipt_name, receiptName);
		typeTextIntoView(R.id.act_edit_receipt_income, "100");

		selectAccount();
		selectSource();
		matchToolbarTitle(newReceiptTitle);

		clickIntoView(R.id.action_save);

		matchToolbarTitle(receiptsTitle);

		onView(withId(R.id.row_receipt_name)).check(matches(withText(receiptName)));
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
		return InstrumentationRegistry.getTargetContext();
	}
}
