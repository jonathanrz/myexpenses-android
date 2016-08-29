package br.com.jonathanzanella.myexpenses.account;

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

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.helpers.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

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
public class AddAccountTest {
	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
	@Rule
	public ActivityTestRule<EditAccountActivity> editAccountActivityTestRule = new ActivityTestRule<>(EditAccountActivity.class);

	@Before
	public void setUp() throws Exception {
		UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
		if (!uiDevice.isScreenOn())
			uiDevice.wakeUp();
	}

	@After
	public void tearDown() throws Exception {
		DatabaseHelper.reset(getContext());
	}

	@Test
	public void add_new_account() {
		activityTestRule.launchActivity(new Intent());

		openMenuAndClickItem(R.string.accounts);

		final String accountsTitle = getContext().getString(R.string.accounts);
		matchToolbarTitle(accountsTitle);

		clickIntoView(R.id.view_accounts_fab);

		final String newAccountTitle = getContext().getString(R.string.new_account_title);
		matchToolbarTitle(newAccountTitle);

		final String accountTitle = "Test";
		typeTextIntoView(R.id.act_edit_account_name, accountTitle);
		clickIntoView(R.id.action_save);

		matchToolbarTitle(accountsTitle);

		onView(withId(R.id.row_account_name)).check(matches(withText(accountTitle)));
		String balance = NumberFormat.getCurrencyInstance().format(0);
		onView(withId(R.id.row_account_balance)).check(matches(withText(balance)));
	}

	@Test
	public void add_new_account_shows_error_with_empty_name() {
		editAccountActivityTestRule.launchActivity(new Intent());

		final String newAccountTitle = getContext().getString(R.string.new_account_title);
		matchToolbarTitle(newAccountTitle);

		clickIntoView(R.id.action_save);

		final String errorMessage = getContext().getString(R.string.error_message_name_not_informed);
		matchErrorMessage(R.id.act_edit_account_name, errorMessage);
	}

	private Context getContext() {
		return InstrumentationRegistry.getTargetContext();
	}
}
