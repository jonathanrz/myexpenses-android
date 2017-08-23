package br.com.jonathanzanella.myexpenses.resume;

import android.content.Intent;
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
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.Is.is;

public class UpdateAccountBalanceTest {
	@Rule
	public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

	private Account account;
	private AccountRepository accountRepository;

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(getTargetContext()).recreateTables();

		accountRepository = new AccountRepository();

		account = new AccountBuilder().build();
		assertTrue(accountRepository.save(account).isValid());
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void confirm_receipt_should_increase_account_balance() {
		Receipt receipt = new ReceiptBuilder().account(account).income(1000).build();
		assertTrue(new ReceiptRepository(new RepositoryImpl<>(getTargetContext())).save(receipt).isValid());

		mainActivityTestRule.launchActivity(new Intent());

		onView(allOf(
				withId(R.id.income),
				isDescendantOfA(withTagValue(is(receipt.getUuid())))))
				.perform(scrollTo()).perform(click());
		clickIntoView(getTargetContext().getString(android.R.string.yes));

		account = accountRepository.find(account.getUuid());
		assertThat(account.getBalance(), is(receipt.getIncome()));
	}

	@Test
	public void confirm_expense_should_decrease_account_balance() {
		Expense expense = new ExpenseBuilder().chargeable(account).value(1000).build();
		assertTrue(new ExpenseRepository(new RepositoryImpl<>(getTargetContext())).save(expense).isValid());

		mainActivityTestRule.launchActivity(new Intent());

		onView(allOf(
				withId(R.id.income),
				isDescendantOfA(withTagValue(is((Object)expense.getUuid())))))
				.perform(scrollTo()).perform(click());
		clickIntoView(getTargetContext().getString(android.R.string.yes));

		account = accountRepository.find(account.getUuid());
		assertThat(account.getBalance(), is(expense.getValue() * -1));
	}

	@Test
	public void confirm_expense_and_receipt_should_update_account_balance() {
		Expense expense = new ExpenseBuilder().chargeable(account).value(100).build();
		assertTrue(new ExpenseRepository(new RepositoryImpl<Expense>(getTargetContext())).save(expense).isValid());

		Receipt receipt = new ReceiptBuilder().account(account).income(1000).build();
		assertTrue(new ReceiptRepository(new RepositoryImpl<Receipt>(getTargetContext())).save(receipt).isValid());

		mainActivityTestRule.launchActivity(new Intent());

		onView(allOf(
				withId(R.id.income),
				isDescendantOfA(withTagValue(is((Object)receipt.getUuid())))))
				.perform(scrollTo()).perform(click());
		clickIntoView(getTargetContext().getString(android.R.string.yes));

		onView(allOf(
				withId(R.id.income),
				isDescendantOfA(withTagValue(is((Object)expense.getUuid())))))
				.perform(scrollTo()).perform(click());
		clickIntoView(getTargetContext().getString(android.R.string.yes));

		account = accountRepository.find(account.getUuid());
		assertThat(account.getBalance(), is(receipt.getIncome() - expense.getValue()));
	}
}
