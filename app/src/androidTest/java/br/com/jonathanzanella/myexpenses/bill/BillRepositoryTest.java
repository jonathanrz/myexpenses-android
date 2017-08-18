package br.com.jonathanzanella.myexpenses.bill;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.card.CardRepository;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.BillBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.CardBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static br.com.jonathanzanella.myexpenses.helpers.TestUtils.waitForIdling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class BillRepositoryTest {
	private final DateTime firstDayOfJune = new DateTime(2016, 6, 1, 0, 0, 0, 0);
	private final ExpenseRepository expenseRepository = new ExpenseRepository(new RepositoryImpl<>(getTargetContext()));
	private final BillRepository billRepository = new BillRepository(expenseRepository);

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();
	}

	@Test
	public void can_save_bill() throws Exception {
		Bill bill = new BillBuilder().build();
		billRepository.save(bill);

		assertThat(bill.getId(), is(not(0L)));
		assertThat(bill.getUuid(), is(not("")));
	}

	@Test
	public void can_load_saved_bill() throws Exception {
		Bill savedBill = new BillBuilder().build();
		billRepository.save(savedBill);

		Bill bill = billRepository.find(savedBill.getUuid());
		assertThat(bill.getUuid(), is(savedBill.getUuid()));
		assertThat(bill.getInitDate(), is(savedBill.getInitDate()));
	}

	@Test
	public void bill_is_paid_when_paid_with_credit_card() {
		Bill bill = new BillBuilder()
				.initDate(firstDayOfJune)
				.endDate(firstDayOfJune)
				.build();
		billRepository.save(bill);

		Account account = new AccountBuilder().build();
		AccountRepository accountRepository = new AccountRepository(new RepositoryImpl<>(MyApplication.Companion.getContext()));
		accountRepository.save(account);

		Card card = new CardBuilder().account(account).build(accountRepository);
		new CardRepository(new RepositoryImpl<>(MyApplication.Companion.getContext()), expenseRepository).save(card);

		Expense expense = new ExpenseBuilder()
				.date(firstDayOfJune)
				.bill(bill)
				.chargeable(card)
				.build();

		List<Bill> bills = billRepository.monthly(firstDayOfJune);
		assertThat(bills.size(), Is.is(1));

		expenseRepository.save(expense);

		bills = billRepository.monthly(firstDayOfJune);
		assertThat(bills.size(), Is.is(0));
	}

	@Test
	public void bill_greater_updated_at_returns_greater_updated_at() throws Exception {
		Bill bill = new BillBuilder().name("bill100").updatedAt(100L).build();
		billRepository.save(bill);
		bill = new BillBuilder().name("bill99").updatedAt(99L).build();
		billRepository.save(bill);

		assertThat(billRepository.greaterUpdatedAt(), is(100L));
	}

	@Test
	public void bill_unsync_returns_only_not_synced() throws Exception {
		Bill billUnsync = new BillBuilder().name("billUnsync").build();
		billUnsync.setSync(false);
		billRepository.save(billUnsync);

		Bill billSync = new BillBuilder().name("billSync").build();
		billRepository.save(billSync);
		billRepository.syncAndSave(billSync);

		waitForIdling();

		List<Bill> bills = billRepository.unsync();
		assertThat(bills.size(), is(1));
		assertThat(bills.get(0).getUuid(), is(billUnsync.getUuid()));
	}

	@Test
	public void load_user_bills_in_alphabetical_order() throws Exception {
		Bill billB = new BillBuilder().name("b").build();
		billRepository.save(billB);

		Bill billA = new BillBuilder().name("a").build();
		billRepository.save(billA);

		List<Bill> bills = billRepository.all();
		assertThat(bills.get(0).getUuid(), is(billA.getUuid()));
		assertThat(bills.get(1).getUuid(), is(billB.getUuid()));
	}
}