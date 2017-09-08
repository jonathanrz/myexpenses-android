package br.com.jonathanzanella.myexpenses.resume;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import br.com.jonathanzanella.TestApp;
import br.com.jonathanzanella.myexpenses.App;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.Is.is;

public class ShowDetailScreenTest {
	@Rule
	public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);
	@Inject
	AccountRepository accountRepository;
	@Inject
	ReceiptRepository receiptRepository;
	@Inject
	SourceRepository sourceRepository;
	@Inject
	ExpenseRepository expenseRepository;

	private Account account;

	@Before
	public void setUp() throws Exception {
		TestApp.Companion.getTestComponent().inject(this);
		App.Companion.resetDatabase();

		account = new AccountBuilder().build();
		assertTrue(accountRepository.save(account).isValid());
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void open_receipt_screen_when_selecting_receipt() {
		Source source = new SourceBuilder().build();
		assertTrue(sourceRepository.save(source).isValid());
		Receipt receipt = new ReceiptBuilder().account(account).source(source).build();
		assertTrue(receiptRepository.save(receipt).isValid());

		mainActivityTestRule.launchActivity(new Intent());

		onView(allOf(withId(R.id.name),
					isDescendantOfA(withTagValue(is(receipt.getUuid())))))
				.perform(scrollTo()).perform(click());

		final String showReceiptTitle = getTargetContext().getString(R.string.receipt) + " " + receipt.getName();
		matchToolbarTitle(showReceiptTitle);

		onView(withId(R.id.act_show_receipt_name)).check(matches(withText(receipt.getName())));
		onView(withId(R.id.act_show_receipt_account)).check(matches(withText(account.getName())));
	}

	@Test
	public void open_expense_screen_when_selecting_expense() {
		Expense expense = new ExpenseBuilder().chargeable(account).build();
		assertTrue(expenseRepository.save(expense).isValid());

		mainActivityTestRule.launchActivity(new Intent());

		onView(allOf(withId(R.id.name),
					isDescendantOfA(withTagValue(is(expense.getUuid())))))
				.perform(scrollTo()).perform(click());

		final String showExpenseTitle = getTargetContext().getString(R.string.expense) + " " + expense.getName();
		matchToolbarTitle(showExpenseTitle);

		onView(withId(R.id.act_show_expense_name)).check(matches(withText(expense.getName())));
		onView(withId(R.id.act_show_expense_chargeable)).check(matches(withText(account.getName())));
	}
}

