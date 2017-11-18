package br.com.jonathanzanella.myexpenses.ui.resume;

import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import br.com.jonathanzanella.myexpenses.App;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountDataSource;
import br.com.jonathanzanella.myexpenses.ui.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.Is.is;

public class ShowAccountInResumeTest {
	@Rule
	public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);
	AccountDataSource accountDataSource;

	@Before
	public void setUp() throws Exception {
		App.Companion.resetDatabase();

		accountDataSource = App.Companion.getApp().appComponent.accountDataSource();
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void show_only_account_marked_to_show() {
		Account accountToShow = new AccountBuilder().name("accountToShow").showInResume(true).build();
		assertTrue(accountDataSource.save(accountToShow).blockingFirst().isValid());
		Account accountToHide = new AccountBuilder().name("accountToHide").showInResume(false).build();
		assertTrue(accountDataSource.save(accountToHide).blockingFirst().isValid());

		mainActivityTestRule.launchActivity(new Intent());

		getAccountNameView(accountToShow).check(matches(isDisplayed()));
		getAccountNameView(accountToHide).check(doesNotExist());
	}

	private ViewInteraction getAccountNameView(Account account) {
		return onView(allOf(
				withId(R.id.row_account_name),
				allOf(
					isDescendantOfA(withTagValue(is((Object)account.getUuid())))),
					allOf(
						withText(account.getName()),
						isDisplayed())));
	}
}