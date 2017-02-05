package br.com.jonathanzanella.myexpenses.receipt;

import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.FlowManagerHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;

import static android.support.test.InstrumentationRegistry.getContext;
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
@Ignore("until dbflow are removed")
public class ShowReceiptActivityTest {
	@Rule
	public ActivityTestRule<ShowReceiptActivity> activityTestRule = new ActivityTestRule<>(ShowReceiptActivity.class, true, false);

	private Receipt receipt;
	private ReceiptRepository repository = new ReceiptRepository();

	@Before
	public void setUp() throws Exception {
		Source s = new SourceBuilder().build();
		new SourceRepository(new Repository<Source>(getContext())).save(s);

		Account a = new AccountBuilder().build();
		new AccountRepository(new Repository<Account>(getContext())).save(a);

		receipt = new ReceiptBuilder().source(s).account(a).build();
		repository.save(receipt);
	}

	@After
	public void tearDown() throws Exception {
		FlowManagerHelper.reset(getTargetContext());
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void shows_receipt_correctly() throws Exception {
		Intent i = new Intent();
		i.putExtra(ShowReceiptActivity.KEY_RECEIPT_UUID, receipt.getUuid());
		activityTestRule.launchActivity(i);

		final String editReceiptTitle = getTargetContext().getString(R.string.receipt) + " " + receipt.getName();
		matchToolbarTitle(editReceiptTitle);

		String incomeAsCurrency = NumberFormat.getCurrencyInstance().format(receipt.getIncome() / 100.0);
		onView(withId(R.id.act_show_receipt_name)).check(matches(withText(receipt.getName())));
		onView(withId(R.id.act_show_receipt_income)).check(matches(withText(incomeAsCurrency)));
		Account account = receipt.getAccount();
		onView(withId(R.id.act_show_receipt_account)).check(matches(withText(account.getName())));

		Source source = receipt.getSource();
		onView(withId(R.id.act_show_receipt_source)).check(matches(withText(source.getName())));
	}
}