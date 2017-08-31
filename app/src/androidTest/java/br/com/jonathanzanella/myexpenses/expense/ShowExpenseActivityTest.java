package br.com.jonathanzanella.myexpenses.expense;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
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
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class ShowExpenseActivityTest {
	@Rule
	public ActivityTestRule<ShowExpenseActivity> activityTestRule = new ActivityTestRule<>(ShowExpenseActivity.class, true, false);

	private final ExpenseRepository repository = new ExpenseRepository();
	private Expense expense;

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();

		Account a = new AccountBuilder().build();
		new AccountRepository().save(a);

		expense = new ExpenseBuilder().chargeable(a).build();
		assertTrue(repository.save(expense).isValid());
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void shows_expense_correctly() throws Exception {
		Intent i = new Intent();
		i.putExtra(ShowExpenseActivity.Companion.getKEY_EXPENSE_UUID(), expense.getUuid());
		activityTestRule.launchActivity(i);

		final String editExpenseTitle = getTargetContext().getString(R.string.expense) + " " + expense.getName();
		matchToolbarTitle(editExpenseTitle);

		String incomeAsCurrency = CurrencyHelper.INSTANCE.format(expense.getValue());
		onView(withId(R.id.act_show_expense_name)).check(matches(withText(expense.getName())));
		onView(withId(R.id.act_show_expense_value)).check(matches(withText(incomeAsCurrency)));
		Chargeable chargeable = expense.getChargeableFromCache();
		onView(withId(R.id.act_show_expense_chargeable)).check(matches(withText(chargeable.getName())));
	}

	@Test
	public void calls_edit_expense_activity() throws Exception {
		Intent i = new Intent();
		i.putExtra(ShowExpenseActivity.Companion.getKEY_EXPENSE_UUID(), expense.getUuid());
		activityTestRule.launchActivity(i);

		final String showExpenseTitle = getTargetContext().getString(R.string.expense) + " " + expense.getName();
		matchToolbarTitle(showExpenseTitle);

		clickIntoView(R.id.action_edit);

		final String editExpenseTitle = getTargetContext().getString(R.string.edit_expense_title);
		matchToolbarTitle(editExpenseTitle);
	}
}