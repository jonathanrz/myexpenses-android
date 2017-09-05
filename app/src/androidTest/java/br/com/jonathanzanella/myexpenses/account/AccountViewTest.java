package br.com.jonathanzanella.myexpenses.account;

import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import br.com.jonathanzanella.myexpenses.App;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.injection.DaggerTestComponent;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.openMenuAndClickItem;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

public class AccountViewTest {
	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
	@Inject
	AccountRepository repository;

	private Account accountToShowInResume;
	private Account accountToHideInResume;

	@Before
	public void setUp() throws Exception {
		DaggerTestComponent.builder().build().inject(this);
		App.Companion.resetDatabase();

		accountToShowInResume = new AccountBuilder().name("accountToShowInResume").showInResume(true).build();
		accountToHideInResume = new AccountBuilder().name("accountToHideInResume").showInResume(false).build();

		assertTrue(repository.save(accountToShowInResume).isValid());
		assertTrue(repository.save(accountToHideInResume).isValid());
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void list_all_accounts() throws Exception {
		activityTestRule.launchActivity(new Intent());

		openMenuAndClickItem(R.string.accounts);

		accountNameView(accountToShowInResume).check(matches(withText(accountToShowInResume.getName())));
		accountNameView(accountToHideInResume).check(matches(withText(accountToHideInResume.getName())));
	}

	private ViewInteraction accountNameView(Account account) {
		return onView(allOf(
				withId(R.id.row_account_name),
				isDescendantOfA(withTagValue(is((Object)account.getUuid())))));
	}
}
