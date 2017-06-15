package br.com.jonathanzanella.myexpenses.account;

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

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.card.CardRepository;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.CardBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class ShowAccountActivityTest {
	private static final int ACCOUNT_BALANCE = 115;
	private static final int EXPENSE_VALUE = 25;
	private static final int RECEIPT_INCOME = 35;
	@Rule
	public ActivityTestRule<ShowAccountActivity> activityTestRule = new ActivityTestRule<>(ShowAccountActivity.class, true, false);

	private Account account;
	private AccountRepository repository;
	private ExpenseRepository expenseRepository;
	private ReceiptRepository receiptRepository;
	private SourceRepository sourceRepository;
	private CardRepository cardRepository;

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(getTargetContext()).recreateTables();

		repository = new AccountRepository(new RepositoryImpl<Account>(getTargetContext()));
		receiptRepository = new ReceiptRepository(new RepositoryImpl<Receipt>(getTargetContext()));
		expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(getTargetContext()));
		cardRepository = new CardRepository(new RepositoryImpl<Card>(getTargetContext()), expenseRepository);
		sourceRepository = new SourceRepository(new RepositoryImpl<Source>(getTargetContext()));

		account = new Account();
		account.setName("test");
		account.setBalance(ACCOUNT_BALANCE);
		account.setAccountToPayCreditCard(true);
		repository.save(account);
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void shows_account_correctly() throws Exception {
		launchActivity();

		final String editAccountTitle = getTargetContext().getString(R.string.account) + " " + account.getName();
		matchToolbarTitle(editAccountTitle);

		String balanceAsCurrency = CurrencyHelper.format(account.getBalance());
		onView(withId(R.id.act_show_account_name)).check(matches(withText(account.getName())));
		onView(withId(R.id.act_show_account_balance)).check(matches(withText(balanceAsCurrency)));
	}

	@Test
	public void show_credit_card_bill_in_account_show_activity() {
		Card card = new CardBuilder().account(account).build(repository);
		assertTrue(cardRepository.save(card).isValid());
		Expense expense = new ExpenseBuilder().chargeable(card).build();
		assertTrue(expenseRepository.save(expense).isValid());

		launchActivity();

		String billName = getTargetContext().getString(R.string.invoice) + " " + card.getName();
		String value = CurrencyHelper.format(expense.getAmount());

		onView(withId(R.id.act_show_account_name)).check(matches(withText(account.getName())));
		onView(withId(R.id.name)).check(matches(withText(billName)));
		onView(withId(R.id.value)).check(matches(withText(value)));
	}

	@Test
	public void calculate_account_balance_correctly() throws Exception {
		generateTwoMonthsExpenses();
		generateTwoMonthsReceipts();

		launchActivity();

		int expectedBalance = ACCOUNT_BALANCE + RECEIPT_INCOME - EXPENSE_VALUE;
		String expectedValue = CurrencyHelper.format(expectedBalance);
		onView(allOf(
				withId(R.id.balance),
				isDescendantOfA(withId(R.id.thisMonth))))
				.check(matches(withText(expectedValue)));

		expectedBalance = expectedBalance + RECEIPT_INCOME - EXPENSE_VALUE;
		expectedValue = CurrencyHelper.format(expectedBalance);
		onView(allOf(
				withId(R.id.balance),
				isDescendantOfA(withId(R.id.nextMonth))))
				.check(matches(withText(expectedValue)));
	}

	private void launchActivity() {
		Intent i = new Intent();
		i.putExtra(ShowAccountActivity.Companion.getKEY_ACCOUNT_UUID(), account.getUuid());
		activityTestRule.launchActivity(i);
	}

	private void generateTwoMonthsReceipts() {
		Source s = new SourceBuilder().build();
		sourceRepository.save(s);

		Receipt receipt = new ReceiptBuilder()
				.income(RECEIPT_INCOME)
				.date(DateTime.now())
				.account(account)
				.source(s)
				.build();
		assertTrue(receiptRepository.save(receipt).isValid());
		receipt = new ReceiptBuilder()
				.income(RECEIPT_INCOME)
				.date(DateTime.now().plusMonths(1))
				.account(account)
				.source(s)
				.build();
		assertTrue(receiptRepository.save(receipt).isValid());
	}

	private void generateTwoMonthsExpenses() {
		Expense expense = new ExpenseBuilder()
				.value(EXPENSE_VALUE)
				.date(DateTime.now())
				.chargeable(account)
				.build();
		assertTrue(expenseRepository.save(expense).isValid());
		expense = new ExpenseBuilder()
				.value(EXPENSE_VALUE)
				.date(DateTime.now().plusMonths(1))
				.chargeable(account)
				.build();
		assertTrue(expenseRepository.save(expense).isValid());
	}
}