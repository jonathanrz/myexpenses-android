package br.com.jonathanzanella.myexpenses.card;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
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
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.CardBuilder;
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
public class ShowCardActivityTest {
	@Rule
	public ActivityTestRule<ShowCardActivity> activityTestRule = new ActivityTestRule<>(ShowCardActivity.class, true, false);

	private final ExpenseRepository expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(MyApplication.Companion.getContext()));
	private final CardRepository repository = new CardRepository(new RepositoryImpl<Card>(MyApplication.Companion.getContext()), expenseRepository);
	private final AccountRepository accountRepository = new AccountRepository(new RepositoryImpl<Account>(MyApplication.Companion.getContext()));

	private Card card;
	private Account account;

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();

		account = new AccountBuilder().build();
		accountRepository.save(account);

		card = new CardBuilder().account(account).type(CardType.CREDIT).build(accountRepository);
		repository.save(card);
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void shows_account_correctly() throws Exception {
		callActivity();

		final String editCardTitle = getTargetContext().getString(R.string.card) + " " + card.getName();
		matchToolbarTitle(editCardTitle);

		onView(withId(R.id.act_show_card_name)).check(matches(withText(card.getName())));
		onView(withId(R.id.act_show_card_account)).check(matches(withText(account.getName())));
	}

	private void callActivity() {
		Intent i = new Intent();
		i.putExtra(ShowCardActivity.Companion.getKEY_CREDIT_CARD_UUID(), card.getUuid());
		activityTestRule.launchActivity(i);
	}

	@Test
	public void generate_and_pay_credit_card_bill() throws Exception {
		DateTime date = DateTime.now().minusMonths(1);
		Expense expense1 = new ExpenseBuilder().chargeable(card).date(date).build();
		assertTrue(expenseRepository.save(expense1).isValid());
		Expense expense2 = new ExpenseBuilder().chargeable(card).date(date).build();
		assertTrue(expenseRepository.save(expense2).isValid());

		callActivity();

		clickIntoView(R.id.act_show_card_pay_credit_card_bill);

		String editExpenseTitle = getTargetContext().getString(R.string.edit_expense_title);
		matchToolbarTitle(editExpenseTitle);

		String cardBillName = getTargetContext().getString(R.string.invoice) + " " + card.getName();
		onView(withId(R.id.act_edit_expense_name)).check(matches(withText(cardBillName)));
		String cardBillValue = CurrencyHelper.INSTANCE.format(expense1.getValue() + expense2.getValue());
		onView(withId(R.id.act_edit_expense_value)).check(matches(withText(cardBillValue)));
		onView(withId(R.id.act_edit_expense_chargeable)).check(matches(withText(card.getAccount().getName())));

		expense1 = expenseRepository.find(expense1.getUuid());
		assertTrue(expense1.isCharged());
		expense2 = expenseRepository.find(expense1.getUuid());
		assertTrue(expense2.isCharged());
	}
}