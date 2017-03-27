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

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.typeTextIntoView;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class EditExpenseTest {
	@Rule
	public ActivityTestRule<ShowExpenseActivity> activityTestRule = new ActivityTestRule<>(ShowExpenseActivity.class, true, false);

	private Expense expense;
	private ExpenseRepository repository;

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();

		Account a = new AccountBuilder().build();
		new AccountRepository(new RepositoryImpl<Account>(MyApplication.getContext())).save(a);

		expense = new ExpenseBuilder().chargeable(a).build();
		repository = new ExpenseRepository(new RepositoryImpl<Expense>(getTargetContext()));
		repository.save(expense);
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void edit_expense_correctly() throws Exception {
		Intent i = new Intent();
		i.putExtra(ShowExpenseActivity.KEY_EXPENSE_UUID, expense.getUuid());
		activityTestRule.launchActivity(i);

		final String showExpenseTitle = getTargetContext().getString(R.string.expense) + " " + expense.getName();
		matchToolbarTitle(showExpenseTitle);

		clickIntoView(R.id.action_edit);

		final String editExpenseTitle = getTargetContext().getString(R.string.edit_expense_title);
		matchToolbarTitle(editExpenseTitle);
		onView(withId(R.id.act_edit_expense_name)).check(matches(withText(expense.getName())));
		typeTextIntoView(R.id.act_edit_expense_name, " changed");

		clickIntoView(R.id.action_save);

		matchToolbarTitle(showExpenseTitle + " changed");

		expense = repository.find(expense.getUuid());

		onView(withId(R.id.act_show_expense_name)).check(matches(withText(expense.getName())));
		assertThat(repository.userExpenses().size(), is(1));
	}
}