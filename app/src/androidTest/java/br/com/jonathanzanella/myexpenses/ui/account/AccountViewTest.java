package br.com.jonathanzanella.myexpenses.ui.account;

import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import br.com.jonathanzanella.TestApp;
import br.com.jonathanzanella.myexpenses.App;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.ui.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.openMenuAndClickItem;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

public class AccountViewTest {
	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
	@Inject
	br.com.jonathanzanella.myexpenses.account.AccountDataSource dataSource;

	private br.com.jonathanzanella.myexpenses.account.Account accountToShowInResume;
	private br.com.jonathanzanella.myexpenses.account.Account accountToHideInResume;

	@Before
	public void setUp() throws Exception {
		TestApp.Companion.getTestComponent().inject(this);
		App.Companion.resetDatabase();

		accountToShowInResume = new AccountBuilder().name("accountToShowInResume").showInResume(true).build();
		accountToHideInResume = new AccountBuilder().name("accountToHideInResume").showInResume(false).build();

		assertTrue(dataSource.save(accountToShowInResume).blockingFirst().isValid());
		assertTrue(dataSource.save(accountToHideInResume).blockingFirst().isValid());
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void list_all_accounts() throws Exception {
		activityTestRule.launchActivity(new Intent());

		openMenuAndClickItem(R.string.accounts);

		Thread.sleep(500);

		accountNameView(accountToShowInResume).check(matches(withText(accountToShowInResume.getName())));
		accountNameView(accountToHideInResume).check(matches(withText(accountToHideInResume.getName())));
	}

	private ViewInteraction accountNameView(br.com.jonathanzanella.myexpenses.account.Account account) {
		return onView(allOf(
				withId(R.id.row_account_name),
				isDescendantOfA(withTagValue(is(account.getUuid())))));
	}
}
