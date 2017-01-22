package br.com.jonathanzanella.myexpenses.expense;

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

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.FlowManagerHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;

/**
 * Created by jzanella on 8/28/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ShowExpenseActivityTest {
	@Rule
	public ActivityTestRule<ShowExpenseActivity> activityTestRule = new ActivityTestRule<>(ShowExpenseActivity.class, true, false);

	private Expense expense;
	private ExpenseRepository repository = new ExpenseRepository();

	@Before
	public void setUp() throws Exception {
		Account a = new AccountBuilder().build();
		new AccountRepository(new Repository<Account>(MyApplication.getContext())).save(a);

		expense = new ExpenseBuilder().chargeable(a).build();
		repository.save(expense);
	}

	@After
	public void tearDown() throws Exception {
		FlowManagerHelper.reset(getTargetContext());
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void shows_expense_correctly() throws Exception {
		Intent i = new Intent();
		i.putExtra(ShowExpenseActivity.KEY_EXPENSE_UUID, expense.getUuid());
		activityTestRule.launchActivity(i);

		final String editExpenseTitle = getTargetContext().getString(R.string.expense) + " " + expense.getName();
		matchToolbarTitle(editExpenseTitle);

		String incomeAsCurrency = NumberFormat.getCurrencyInstance().format(expense.getValue() / 100.0);
		onView(withId(R.id.act_show_expense_name)).check(matches(withText(expense.getName())));
		onView(withId(R.id.act_show_expense_value)).check(matches(withText(incomeAsCurrency)));
		Chargeable chargeable = expense.getChargeable();
		onView(withId(R.id.act_show_expense_chargeable)).check(matches(withText(chargeable.getName())));
	}

	@Test
	public void calls_edit_expense_activity() throws Exception {
		Intent i = new Intent();
		i.putExtra(ShowExpenseActivity.KEY_EXPENSE_UUID, expense.getUuid());
		activityTestRule.launchActivity(i);

		final String showExpenseTitle = getTargetContext().getString(R.string.expense) + " " + expense.getName();
		matchToolbarTitle(showExpenseTitle);

		clickIntoView(R.id.action_edit);

		final String editExpenseTitle = getTargetContext().getString(R.string.edit_expense_title);
		matchToolbarTitle(editExpenseTitle);
	}
}