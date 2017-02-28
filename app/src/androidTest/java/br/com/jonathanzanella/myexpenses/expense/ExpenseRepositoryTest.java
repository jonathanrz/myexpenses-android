package br.com.jonathanzanella.myexpenses.expense;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.card.CardRepository;
import br.com.jonathanzanella.myexpenses.card.CardType;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.CardBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExpenseRepositoryTest {
	private ExpenseRepository repository;

	private Account account;
	private Card creditCard;

	@Before
	public void setUp() throws Exception {
		account = new AccountBuilder().build();
		AccountRepository accountRepository = new AccountRepository(new Repository<Account>(getTargetContext()));
		accountRepository.save(account);
		creditCard = new CardBuilder().account(account).type(CardType.CREDIT).build(accountRepository);
		repository = new ExpenseRepository(new Repository<Expense>(getTargetContext()));
		new CardRepository(new Repository<Card>(getTargetContext()), repository).save(creditCard);
	}

	@After
	public void tearDown() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void can_save_expense() throws Exception {
		Expense expense = new ExpenseBuilder().chargeable(account).build();
		repository.save(expense);

		assertThat(expense.getId(), is(not(0L)));
		assertThat(expense.getUuid(), is(not("")));
	}

	@Test
	public void can_load_saved_expense() throws Exception {
		Expense expense = new ExpenseBuilder()
				.chargeable(account)
				.build();
		repository.save(expense);

		Expense loadExpense = repository.find(expense.getUuid());
		assertThat(loadExpense, is(expense));
	}

	@Test
	public void load_expenses_ordered_by_date() throws Exception {
		DateTime date = DateTime.now();
		Expense expenseA = new ExpenseBuilder()
				.name("a")
				.chargeable(account)
				.date(date)
				.build();
		repository.save(expenseA);

		Expense expenseB = new ExpenseBuilder()
				.name("b")
				.chargeable(account)
				.date(date.minusDays(1))
				.build();
		repository.save(expenseB);

		List<Expense> sources = repository.userExpenses();
		assertThat(sources.get(0), is(expenseB));
		assertThat(sources.get(1), is(expenseA));
	}

	@Test
	public void load_monthly_expenses_without_credit_card_expenses() throws Exception {
		Expense creditCardExpense = new ExpenseBuilder()
				.name("CreditCardExpense")
				.chargeable(creditCard)
				.build();
		repository.save(creditCardExpense);
		Expense accountExpense = new ExpenseBuilder()
				.name("AccountExpense")
				.chargeable(account)
				.build();
		repository.save(accountExpense);

		List<Expense> expenses = repository.expensesForResumeScreen(creditCardExpense.getDate());
		assertThat(expenses.size(), is(1));
		assertThat(expenses.get(0).getUuid(), is(accountExpense.getUuid()));
	}

	@Test
	public void load_unsync_expenses() throws Exception {
		Expense expenseUnsync = new ExpenseBuilder()
				.sync(false)
				.chargeable(account)
				.build();
		assertTrue(repository.save(expenseUnsync).isValid());
		Expense expenseSync = new ExpenseBuilder()
				.sync(true)
				.chargeable(account)
				.build();
		assertTrue(repository.save(expenseSync).isValid());
		repository.syncAndSave(expenseSync);

		List<Expense> expenses = repository.unsync();
		assertThat(expenses.size(), is(1));
		assertThat(expenses.get(0).getUuid(), is(expenseUnsync.getUuid()));
	}
}