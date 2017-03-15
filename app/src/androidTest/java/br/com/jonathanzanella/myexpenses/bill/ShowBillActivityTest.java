package br.com.jonathanzanella.myexpenses.bill;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.BillBuilder;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.TestUtils.waitForIdling;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class ShowBillActivityTest {
	@Rule
	public ActivityTestRule<ShowBillActivity> activityTestRule = new ActivityTestRule<>(ShowBillActivity.class, true, false);

	private Bill bill;
	private ExpenseRepository expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(getTargetContext()));
	private BillRepository repository = new BillRepository(new RepositoryImpl<Bill>(getTargetContext()), expenseRepository);

	@Before
	public void setUp() throws Exception {
		bill = new BillBuilder().build();
		repository.save(bill);
	}

	@After
	public void tearDown() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void shows_account_correctly() throws Exception {
		Intent i = new Intent();
		i.putExtra(ShowBillActivity.KEY_BILL_UUID, bill.getUuid());
		activityTestRule.launchActivity(i);

		waitForIdling();

		final String editBillTitle = getTargetContext().getString(R.string.bill) + " " + bill.getName();
		matchToolbarTitle(editBillTitle);

		String balanceAsCurrency = NumberFormat.getCurrencyInstance().format(bill.getAmount() / 100.0);
		onView(withId(R.id.act_show_bill_name)).check(matches(withText(bill.getName())));
		onView(withId(R.id.act_show_bill_amount)).check(matches(withText(balanceAsCurrency)));
	}
}