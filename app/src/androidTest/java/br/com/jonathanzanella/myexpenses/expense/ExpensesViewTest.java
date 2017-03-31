package br.com.jonathanzanella.myexpenses.expense;

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
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder;
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
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.openMenuAndClickItem;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.typeTextIntoView;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

public class ExpensesViewTest {
	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	private Expense expense1;
	private Expense expense2;

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(getTargetContext()).recreateTables();

		Account account = new AccountBuilder().build();
		new AccountRepository(new RepositoryImpl<Account>(getTargetContext())).save(account);

		ExpenseRepository repository = new ExpenseRepository(new RepositoryImpl<Expense>(getTargetContext()));
		expense1 = new ExpenseBuilder().chargeable(account).name("Expense1").build();
		assertTrue(repository.save(expense1).isValid());
		expense2 = new ExpenseBuilder().chargeable(account).name("Expense2").build();
		assertTrue(repository.save(expense2).isValid());
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void filter_just_show_expense1() throws Exception {
		activityTestRule.launchActivity(new Intent());

		openMenuAndClickItem(R.string.expenses);

		final String expensesTitle = getTargetContext().getString(R.string.expenses);
		matchToolbarTitle(expensesTitle);

		clickIntoView(R.id.search);
		typeTextIntoView(R.id.search_src_text, expense1.getName());

		onViewExpenseName(expense1).check(matches(isDisplayed()));
		onViewExpenseName(expense2).check(doesNotExist());
	}

	private ViewInteraction onViewExpenseName(Expense expense) {
		return onView(allOf(
				withId(R.id.row_expense_name),
				allOf(
					isDescendantOfA(withTagValue(is((Object)expense.getUuid())))),
					withText(expense.getName())));
	}
}