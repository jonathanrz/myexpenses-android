package br.com.jonathanzanella.myexpenses.expense;

import android.content.Intent;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import br.com.jonathanzanella.TestApp;
import br.com.jonathanzanella.myexpenses.App;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountDataSource;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clearAndTypeTextIntoView;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class EditExpenseTest {
	@Rule
	public ActivityTestRule<ShowExpenseActivity> activityTestRule = new ActivityTestRule<>(ShowExpenseActivity.class, true, false);

	private Expense expense;
	@Inject
	ExpenseDataSource dataSource;
	@Inject
	AccountDataSource accountDataSource;

	@Before
	public void setUp() throws Exception {
		TestApp.Companion.getTestComponent().inject(this);
		App.Companion.resetDatabase();

		Account a = new AccountBuilder().build();
		accountDataSource.save(a);

		expense = new ExpenseBuilder()
				.date(DateTime.now().minusDays(1))
				.chargeable(a)
				.build();
		assertTrue(dataSource.save(expense).isValid());
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void edit_expense_correctly() throws Exception {
		Intent i = new Intent();
		i.putExtra(ShowExpenseActivity.Companion.getKEY_EXPENSE_UUID(), expense.getUuid());
		activityTestRule.launchActivity(i);

		final String showExpenseTitle = getTargetContext().getString(R.string.expense) + " " + expense.getName();
		matchToolbarTitle(showExpenseTitle);

		clickIntoView(R.id.action_edit);

		final String editExpenseTitle = getTargetContext().getString(R.string.edit_expense_title);
		matchToolbarTitle(editExpenseTitle);
		onView(withId(R.id.act_edit_expense_name)).check(matches(withText(expense.getName())));
		String expectedDate = Transaction.Companion.getSIMPLE_DATE_FORMAT().format(expense.getDate().toDate());
		onView(withId(R.id.act_edit_expense_date)).check(matches(withText(expectedDate)));
		clearAndTypeTextIntoView(R.id.act_edit_expense_name, expense.getName() + " changed");

		clickIntoView(R.id.action_save);

		matchToolbarTitle(showExpenseTitle + " changed");

		expense = dataSource.find(expense.getUuid());

		onView(withId(R.id.act_show_expense_name)).check(matches(withText(expense.getName())));
		assertThat(dataSource.all().size(), is(1));
	}
}