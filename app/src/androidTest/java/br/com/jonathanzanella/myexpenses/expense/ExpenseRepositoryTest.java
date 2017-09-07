package br.com.jonathanzanella.myexpenses.expense;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import javax.inject.Inject;

import br.com.jonathanzanella.TestApp;
import br.com.jonathanzanella.myexpenses.App;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountDataSource;
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.card.CardDataSource;
import br.com.jonathanzanella.myexpenses.card.CardType;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.CardBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ExpenseRepositoryTest {
	@Inject
	CardDataSource cardDataSource;
	@Inject
	AccountDataSource accountDataSource;
	@Inject
	ExpenseDataSource dataSource;

	private Account account;
	private Card debitCard;
	private Card creditCard;

	@Before
	public void setUp() throws Exception {
		TestApp.Companion.getTestComponent().inject(this);
		App.Companion.resetDatabase();

		account = new AccountBuilder()
				.accountToPayBills(true)
				.accountToPayCreditCard(true)
				.build();
		assertTrue(accountDataSource.save(account).isValid());
		creditCard = new CardBuilder().name("CreditCard").account(account).type(CardType.CREDIT).build(accountDataSource);
		debitCard = new CardBuilder().name("DebitCard").account(account).type(CardType.DEBIT).build(accountDataSource);
		assertTrue(cardDataSource.save(debitCard).isValid());
		assertTrue(cardDataSource.save(creditCard).isValid());
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void can_save_expense() throws Exception {
		Expense expense = new ExpenseBuilder().chargeable(account).build();
		assertTrue(dataSource.save(expense).isValid());

		assertThat(expense.getId(), is(not(0L)));
		assertThat(expense.getUuid(), is(not("")));
	}

	@Test
	public void can_load_saved_expense() throws Exception {
		Expense expense = new ExpenseBuilder()
				.chargeable(account)
				.build();
		assertTrue(dataSource.save(expense).isValid());

		Expense loadExpense = dataSource.find(expense.getUuid());
		assertThat(loadExpense.getUuid(), is(expense.getUuid()));
	}

	@Test
	public void load_expenses_ordered_by_date() throws Exception {
		DateTime date = DateTime.now();
		Expense expenseA = new ExpenseBuilder()
				.name("a")
				.chargeable(account)
				.date(date)
				.build();
		assertTrue(dataSource.save(expenseA).isValid());

		Expense expenseB = new ExpenseBuilder()
				.name("b")
				.chargeable(account)
				.date(date.minusDays(1))
				.build();
		assertTrue(dataSource.save(expenseB).isValid());

		List<Expense> sources = dataSource.all();
		assertThat(sources.get(0).getUuid(), is(expenseB.getUuid()));
		assertThat(sources.get(1).getUuid(), is(expenseA.getUuid()));
	}

	@Test
	public void load_monthly_expenses_without_credit_card_expenses() throws Exception {
		Expense creditCardExpense = new ExpenseBuilder()
				.name("CreditCardExpense")
				.chargeable(creditCard)
				.build();
		assertTrue(dataSource.save(creditCardExpense).isValid());
		Expense accountExpense = new ExpenseBuilder()
				.name("AccountExpense")
				.chargeable(account)
				.build();
		assertTrue(dataSource.save(accountExpense).isValid());

		List<Expense> expenses = dataSource.expensesForResumeScreen(creditCardExpense.getDate());
		assertThat(expenses.size(), is(1));
		assertThat(expenses.get(0).getUuid(), is(accountExpense.getUuid()));
	}

	@Test
	public void load_account_expenses_with_debit_card_expenses() throws Exception {
		DateTime dateTime = DateTime.now();
		Expense debitCardExpense = new ExpenseBuilder()
				.name("DebitCardExpense")
				.chargeable(debitCard)
				.date(dateTime)
				.build();
		assertTrue(dataSource.save(debitCardExpense).isValid());
		Expense accountExpense = new ExpenseBuilder()
				.name("AccountExpense")
				.chargeable(account)
				.date(dateTime)
				.build();
		assertTrue(dataSource.save(accountExpense).isValid());

		List<Expense> expenses = dataSource.accountExpenses(account, dateTime);
		assertThat(expenses.size(), is(2));
		assertThat(expenses.get(0).getUuid(), is(debitCardExpense.getUuid()));
		assertThat(expenses.get(1).getUuid(), is(accountExpense.getUuid()));
	}

	@Test
	public void load_unsync_expenses() throws Exception {
		Expense expenseUnsync = new ExpenseBuilder()
				.sync(false)
				.chargeable(account)
				.build();
		assertTrue(dataSource.save(expenseUnsync).isValid());
		Expense expenseSync = new ExpenseBuilder()
				.sync(true)
				.chargeable(account)
				.build();
		assertTrue(dataSource.save(expenseSync).isValid());
		dataSource.syncAndSave(expenseSync);

		List<Expense> expenses = dataSource.unsync();
		assertThat(expenses.size(), is(1));
		assertThat(expenses.get(0).getUuid(), is(expenseUnsync.getUuid()));
	}

	@Test
	public void unpaid_card_expenses_only_show_credit_card_expenses() throws Exception {
		DateTime dateTime = DateTime.now();
		Expense debitCardExpense = new ExpenseBuilder()
				.name("DebitCardExpense")
				.chargeable(debitCard)
				.date(dateTime)
				.build();
		assertTrue(dataSource.save(debitCardExpense).isValid());
		Expense creditCardExpense = new ExpenseBuilder()
				.name("CreditCardExpense")
				.chargeable(creditCard)
				.date(dateTime)
				.build();
		assertTrue(dataSource.save(creditCardExpense).isValid());
		Expense accountExpense = new ExpenseBuilder()
				.name("AccountExpense")
				.chargeable(account)
				.date(dateTime)
				.build();
		assertTrue(dataSource.save(accountExpense).isValid());

		List<Expense> expenses = dataSource.unpaidCardExpenses(dateTime, creditCard);
		assertThat(expenses.size(), is(1));
		assertThat(expenses.get(0).getUuid(), is(creditCardExpense.getUuid()));
	}
}