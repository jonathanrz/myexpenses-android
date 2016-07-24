package br.com.jonathanzanella.myexpenses.accounts;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.UIHelper;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
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

	@After
	public void tearDown() throws Exception {
		MyApplication.getApplication().resetDatabase();
	}

	@Test
	public void addNewAccount() {
		UIHelper.openMenu();

		onView(allOf(withId(R.id.design_menu_item_text), withText(R.string.accounts))).perform(click());

		final String accountsTitle = InstrumentationRegistry.getTargetContext().getString(R.string.accounts);
		UIHelper.matchToolbarTitle(accountsTitle);

		onView(withId(R.id.view_accounts_fab)).perform(click());

		final String newAccountTitle = InstrumentationRegistry.getTargetContext().getString(R.string.new_account_title);
		UIHelper.matchToolbarTitle(newAccountTitle);

		final String accountTitle = "Test";
		onView(withId(R.id.act_edit_account_name)).perform(typeText(accountTitle));
		onView(withId(R.id.act_edit_account_balance)).perform(typeText("100"));
		onView(withId(R.id.action_save)).perform(click());

		UIHelper.matchToolbarTitle(accountsTitle);

		onView(withId(R.id.row_account_name)).check(matches(withText(accountTitle)));
	}
}
