package br.com.jonathanzanella.myexpenses.account;

import android.content.Intent;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.typeTextIntoView;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class EditAccountTest {
	@Rule
	public ActivityTestRule<ShowAccountActivity> activityTestRule = new ActivityTestRule<>(ShowAccountActivity.class, true, false);

	private Account account;
	private AccountRepository repository;

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(getTargetContext()).recreateTables();

		account = new AccountBuilder().build();
		repository = new AccountRepository(new RepositoryImpl<Account>(getTargetContext()));
		assertTrue(repository.save(account).isValid());
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void edit_expense_correctly() throws Exception {
		Intent i = new Intent();
		i.putExtra(ShowAccountActivity.KEY_ACCOUNT_UUID, account.getUuid());
		activityTestRule.launchActivity(i);

		final String showAccountTitle = getTargetContext().getString(R.string.account) + " " + account.getName();
		matchToolbarTitle(showAccountTitle);

		clickIntoView(R.id.action_edit);

		final String editAccountTitle = getTargetContext().getString(R.string.edit_account_title);
		matchToolbarTitle(editAccountTitle);
		onView(withId(R.id.act_edit_account_name)).check(matches(withText(account.getName())));
		onView(withId(R.id.act_edit_account_show_in_resume)).check(matches(isChecked()));
		clickIntoView(R.id.act_edit_account_show_in_resume);
		typeTextIntoView(R.id.act_edit_account_name, " changed");

		clickIntoView(R.id.action_save);

		matchToolbarTitle(showAccountTitle + " changed");

		account = repository.find(account.getUuid());

		onView(withId(R.id.act_show_account_name)).check(matches(withText(account.getName())));
		assertThat(repository.all().size(), is(1));
		assertThat(account.showInResume(), is(false));
	}
}