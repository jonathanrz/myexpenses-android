package br.com.jonathanzanella.myexpenses.account;

import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.FlowManagerHelper;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;

/**
 * Created by jzanella on 8/28/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ShowAccountActivityTest {
	@Rule
	public ActivityTestRule<ShowAccountActivity> activityTestRule = new ActivityTestRule<>(ShowAccountActivity.class, true, false);

	private Account account;
	private AccountRepository repository = new AccountRepository();

	@Before
	public void setUp() throws Exception {
		account = new Account();
		account.setName("test");
		account.setBalance(115);
		repository.save(account);
	}

	@After
	public void tearDown() throws Exception {
		FlowManagerHelper.reset(getTargetContext());
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void shows_account_correctly() throws Exception {
		Intent i = new Intent();
		i.putExtra(ShowAccountActivity.KEY_ACCOUNT_UUID, account.getUuid());
		activityTestRule.launchActivity(i);

		final String editAccountTitle = getTargetContext().getString(R.string.account) + " " + account.getName();
		matchToolbarTitle(editAccountTitle);

		String balanceAsCurrency = NumberFormat.getCurrencyInstance().format(account.getBalance() / 100.0);
		onView(withId(R.id.act_show_account_name)).check(matches(withText(account.getName())));
		onView(withId(R.id.act_show_account_balance)).check(matches(withText(balanceAsCurrency)));
	}
}