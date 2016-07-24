package br.com.jonathanzanella.myexpenses.accounts;

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

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.helpers.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.helpers.UIHelper;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by jzanella on 7/24/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddAccountTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

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
	public void addNewAccount() {
		activityTestRule.launchActivity(new Intent());

		UIHelper.openMenu();

		onView(allOf(withId(R.id.design_menu_item_text), withText(R.string.accounts))).perform(click());

		final String accountsTitle = getContext().getString(R.string.accounts);
		UIHelper.matchToolbarTitle(accountsTitle);

		UIHelper.clickIntoView(R.id.view_accounts_fab);

		final String newAccountTitle = getContext().getString(R.string.new_account_title);
		UIHelper.matchToolbarTitle(newAccountTitle);

		final String accountTitle = "Test";
		UIHelper.typeTextIntoView(R.id.act_edit_account_name, accountTitle);
		UIHelper.typeTextIntoView(R.id.act_edit_account_balance, "100");
		UIHelper.clickIntoView(R.id.action_save);

		UIHelper.matchToolbarTitle(accountsTitle);

		onView(withId(R.id.row_account_name)).check(matches(withText(accountTitle)));
	}

	private Context getContext() {
		return InstrumentationRegistry.getTargetContext();
	}
}
