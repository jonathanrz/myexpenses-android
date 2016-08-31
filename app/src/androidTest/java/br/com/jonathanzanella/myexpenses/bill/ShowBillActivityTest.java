package br.com.jonathanzanella.myexpenses.bill;

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
import br.com.jonathanzanella.myexpenses.helpers.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.BillBuilder;

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
public class ShowBillActivityTest {
	@Rule
	public ActivityTestRule<ShowBillActivity> activityTestRule = new ActivityTestRule<>(ShowBillActivity.class, true, false);

	private Bill bill;
	private BillRepository repository = new BillRepository();

	@Before
	public void setUp() throws Exception {
		bill = new BillBuilder().build();
		repository.save(bill);
	}

	@After
	public void tearDown() throws Exception {
		DatabaseHelper.reset(getTargetContext());
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void shows_account_correctly() throws Exception {
		Intent i = new Intent();
		i.putExtra(ShowBillActivity.KEY_BILL_UUID, bill.getUuid());
		activityTestRule.launchActivity(i);

		final String editBillTitle = getTargetContext().getString(R.string.bill) + " " + bill.getName();
		matchToolbarTitle(editBillTitle);

		String balanceAsCurrency = NumberFormat.getCurrencyInstance().format(bill.getAmount() / 100.0);
		onView(withId(R.id.act_show_bill_name)).check(matches(withText(bill.getName())));
		onView(withId(R.id.act_show_bill_amount)).check(matches(withText(balanceAsCurrency)));
	}
}