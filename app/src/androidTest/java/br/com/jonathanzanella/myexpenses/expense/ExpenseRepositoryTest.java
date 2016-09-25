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
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by jzanella on 8/27/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExpenseRepositoryTest {
	private ExpenseRepository repository = new ExpenseRepository();

	private Account account;

	@Before
	public void setUp() throws Exception {
		account = new AccountBuilder().build();
		new AccountRepository().save(account);
	}

	@After
	public void tearDown() throws Exception {
		DatabaseHelper.reset(InstrumentationRegistry.getTargetContext());
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void can_save_receipt() throws Exception {
		Expense receipt = new ExpenseBuilder().chargeable(account).build();
		repository.save(receipt);

		assertThat(receipt.id, is(not(0L)));
		assertThat(receipt.getUuid(), is(not("")));
	}

	@Test
	public void can_load_saved_receipt() throws Exception {
		Expense receipt = new ExpenseBuilder()
				.chargeable(account)
				.build();
		repository.save(receipt);

		Expense loadExpense = repository.find(receipt.getUuid());
		assertThat(loadExpense, is(receipt));
	}

	@Test
	public void load_receipts_ordered_by_date() throws Exception {
		DateTime date = DateTime.now();
		Expense receiptA = new ExpenseBuilder()
				.name("a")
				.chargeable(account)
				.date(date)
				.build();
		repository.save(receiptA);

		Expense receiptB = new ExpenseBuilder()
				.name("b")
				.chargeable(account)
				.date(date.minusDays(1))
				.build();
		repository.save(receiptB);

		List<Expense> sources = repository.userExpenses();
		assertThat(sources.get(0), is(receiptB));
		assertThat(sources.get(1), is(receiptA));
	}
}