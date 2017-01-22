package br.com.jonathanzanella.myexpenses.bill;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.card.CardRepository;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helpers.FlowManagerHelper;
import br.com.jonathanzanella.myexpenses.helpers.Subscriber;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.BillBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.CardBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ExpenseBuilder;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static br.com.jonathanzanella.myexpenses.helpers.TestUtils.waitForIdling;
import static junit.framework.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by jzanella on 8/27/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class BillRepositoryTest {
	private DateTime firstDayOfJune = new DateTime(2016, 6, 1, 0, 0, 0, 0);
	private BillRepository billRepository = new BillRepository(new Repository<Bill>(getTargetContext()));

	@After
	public void tearDown() throws Exception {
		FlowManagerHelper.reset(getTargetContext());
	}

	@Test
	public void can_save_account() throws Exception {
		Bill bill = new BillBuilder().build();
		billRepository.save(bill);

		assertThat(bill.id, is(not(0L)));
		assertThat(bill.getUuid(), is(not("")));
	}

	@Test
	public void can_load_saved_account() throws Exception {
		Bill bill = new BillBuilder().build();
		billRepository.save(bill);

		billRepository.find(bill.getUuid()).subscribe(new Subscriber<Bill>("BillRepositoryTest.can_load_saved_account") {
			@Override
			public void onNext(Bill bill) {
				assertThat(bill.getUuid(), is(bill.getUuid()));
				assertThat(bill.getInitDate(), is(bill.getInitDate()));
			}
		});
	}

	@Test
	public void load_only_user_accounts() throws Exception {
		Bill correctBill = new BillBuilder().build();
		correctBill.setUserUuid(Environment.CURRENT_USER_UUID);
		billRepository.save(correctBill);

		Bill wrongBill = new BillBuilder().name("test").build();
		wrongBill.setUserUuid("wrong");
		billRepository.save(wrongBill);

		List<Bill> bills = billRepository.userBills();
		assertThat(bills.size(), is(1));
		assertThat(bills.get(0).getUuid(), is(correctBill.getUuid()));
		assertFalse(bills.contains(wrongBill));
	}

	@Test
	public void bill_is_paid_when_paid_with_credit_card() {
		Bill bill = new BillBuilder()
				.initDate(firstDayOfJune)
				.endDate(firstDayOfJune)
				.build();
		billRepository.save(bill);

		Account account = new AccountBuilder().build();
		new AccountRepository(new Repository<Account>(MyApplication.getContext())).save(account);

		Card card = new CardBuilder().account(account).build();
		new CardRepository().save(card);

		Expense expense = new ExpenseBuilder()
				.date(firstDayOfJune)
				.bill(bill)
				.chargeable(card)
				.build();

		billRepository.monthly(firstDayOfJune).subscribe(new Subscriber<List<Bill>>("BillRepositoryTest") {
			@Override
			public void onNext(List<Bill> bills) {
				assertThat(bills.size(), Is.is(1));
			}
		});

		new ExpenseRepository().save(expense);

		billRepository.monthly(firstDayOfJune).subscribe(new Subscriber<List<Bill>>("BillRepositoryTest") {
			@Override
			public void onNext(List<Bill> bills) {
				assertThat(bills.size(), Is.is(0));
			}
		});
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
		billUnsync.sync = false;
		billRepository.save(billUnsync);

		Bill billSync = new BillBuilder().name("billSync").build();
		billRepository.save(billSync);
		billRepository.syncAndSave(billSync);

		waitForIdling();

		List<Bill> bills = billRepository.unsync();
		assertThat(bills.size(), is(1));
		assertThat(bills.get(0), is(billUnsync));
	}

	@Test
	public void load_user_bills_in_alphabetical_order() throws Exception {
		Bill billB = new BillBuilder().name("b").build();
		billRepository.save(billB);

		Bill billA = new BillBuilder().name("a").build();
		billRepository.save(billA);

		List<Bill> bills = billRepository.userBills();
		assertThat(bills.get(0), is(billA));
		assertThat(bills.get(1), is(billB));
	}
}