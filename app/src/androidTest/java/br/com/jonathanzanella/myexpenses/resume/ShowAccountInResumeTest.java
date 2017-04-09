package br.com.jonathanzanella.myexpenses.resume;

import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
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

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(getTargetContext()).recreateTables();
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void show_only_account_marked_to_show() {
		AccountRepository accountRepository = new AccountRepository(new RepositoryImpl<Account>(getTargetContext()));

		Account accountToShow = new AccountBuilder().name("accountToShow").showInResume(true).build();
		assertTrue(accountRepository.save(accountToShow).isValid());
		Account accountToHide = new AccountBuilder().name("accountToHide").showInResume(false).build();
		assertTrue(accountRepository.save(accountToHide).isValid());

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