package br.com.jonathanzanella.myexpenses.ui.account;

import android.content.Intent;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import br.com.jonathanzanella.TestApp;
import br.com.jonathanzanella.myexpenses.App;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.ui.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.clearAndTypeTextIntoView;
import static br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.clickIntoView;
import static br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.matchToolbarTitle;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class EditAccountTest {
	@Rule
	public ActivityTestRule<br.com.jonathanzanella.myexpenses.account.ShowAccountActivity> activityTestRule = new ActivityTestRule<>(br.com.jonathanzanella.myexpenses.account.ShowAccountActivity.class, true, false);

	@Inject
	br.com.jonathanzanella.myexpenses.account.AccountDataSource dataSource;

	private br.com.jonathanzanella.myexpenses.account.Account account;

	@Before
	public void setUp() throws Exception {
		TestApp.Companion.getTestComponent().inject(this);
		App.Companion.resetDatabase();

		account = new AccountBuilder().build();
		assertTrue(dataSource.save(account).blockingFirst().isValid());
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void edit_account_correctly() throws Exception {
		Intent i = new Intent();
		i.putExtra(br.com.jonathanzanella.myexpenses.account.ShowAccountActivity.Companion.getKEY_ACCOUNT_UUID(), account.getUuid());
		activityTestRule.launchActivity(i);

		final String showAccountTitle = getTargetContext().getString(R.string.account) + " " + account.getName();
		matchToolbarTitle(showAccountTitle);

		clickIntoView(R.id.action_edit);

		final String editAccountTitle = getTargetContext().getString(R.string.edit_account_title);
		matchToolbarTitle(editAccountTitle);
		onView(withId(R.id.act_edit_account_name)).check(matches(withText(account.getName())));
		onView(withId(R.id.act_edit_account_show_in_resume)).check(matches(isChecked()));
		clickIntoView(R.id.act_edit_account_show_in_resume);
		clearAndTypeTextIntoView(R.id.act_edit_account_name, account.getName() + " changed");

		clickIntoView(R.id.action_save);

		matchToolbarTitle(showAccountTitle + " changed");

		account = dataSource.find(account.getUuid()).blockingFirst();

		onView(withId(R.id.act_show_account_name)).check(matches(withText(account.getName())));
		assertThat(dataSource.all().blockingFirst().size(), is(1));
		assertThat(account.getShowInResume(), is(false));
	}
}