package br.com.jonathanzanella.myexpenses.account;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
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
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.card.CardRepository;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.CardBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ShowAccountActivityTest {
	@Rule
	public ActivityTestRule<ShowAccountActivity> activityTestRule = new ActivityTestRule<>(ShowAccountActivity.class, true, false);

	private Account account;
	private AccountRepository repository;
	private ExpenseRepository expenseRepository;
	private CardRepository cardRepository;

	@Before
	public void setUp() throws Exception {
		repository = new AccountRepository(new Repository<Account>(InstrumentationRegistry.getTargetContext()));
		expenseRepository = new ExpenseRepository(new Repository<Expense>(InstrumentationRegistry.getTargetContext()));
		cardRepository = new CardRepository(new Repository<Card>(InstrumentationRegistry.getTargetContext()), expenseRepository);

		account = new Account();
		account.setName("test");
		account.setBalance(115);
		account.setAccountToPayCreditCard(true);
		repository.save(account);
	}

	@After
	public void tearDown() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void shows_account_correctly() throws Exception {
		Intent i = new Intent();
		i.putExtra(ShowAccountActivity.KEY_ACCOUNT_UUID, account.getUuid());
		activityTestRule.launchActivity(i);

		final String editAccountTitle = getTargetContext().getString(R.string.account) + " " + account.getName();
		matchToolbarTitle(editAccountTitle);

		String balanceAsCurrency = NumberFormat.getCurrencyInstance().format(account.getBalance() / 100.0);
		onView(withId(R.id.act_show_account_name)).check(matches(withText(account.getName())));
		onView(withId(R.id.act_show_account_balance)).check(matches(withText(balanceAsCurrency)));
	}

	@Test
	public void show_credit_card_bill_in_account_show_activity() {
		Card card = new CardBuilder().account(account).build(repository);
		assertTrue(cardRepository.save(card).isValid());
		Expense expense = new ExpenseBuilder().chargeable(card).build();
		assertTrue(expenseRepository.save(expense).isValid());

		Intent i = new Intent();
		i.putExtra(ShowAccountActivity.KEY_ACCOUNT_UUID, account.getUuid());
		activityTestRule.launchActivity(i);

		String billName = InstrumentationRegistry.getTargetContext().getString(R.string.invoice) + " " + card.getName();
		String value = NumberFormat.getCurrencyInstance().format((expense.getAmount() / 100.0));

		onView(withId(R.id.act_show_account_name)).check(matches(withText(account.getName())));
		onView(withId(R.id.row_transaction_name)).check(matches(withText(billName)));
		onView(withId(R.id.row_transaction_value)).check(matches(withText(value)));
	}
}