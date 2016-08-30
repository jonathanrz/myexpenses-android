package br.com.jonathanzanella.myexpenses.bill;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.helpers.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.BillBuilder;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by jzanella on 8/27/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class BillRepositoryTest {
	private BillRepository repository = new BillRepository();

	@After
	public void tearDown() throws Exception {
		DatabaseHelper.reset(InstrumentationRegistry.getTargetContext());
	}

	@Test
	public void can_save_account() throws Exception {
		Bill bill = new BillBuilder().build();
		repository.save(bill);

		assertThat(bill.id, is(not(0L)));
		assertThat(bill.getUuid(), is(not("")));
	}

	@Test
	public void can_load_saved_account() throws Exception {
		Bill account = new BillBuilder().build();
		repository.save(account);

		Bill loadBill = repository.find(account.getUuid());
		assertThat(loadBill, is(account));
	}

	@Test
	public void load_only_user_accounts() throws Exception {
		Bill correctBill = new BillBuilder().build();
		correctBill.setUserUuid(Environment.CURRENT_USER_UUID);
		repository.save(correctBill);

		Bill wrongBill = new BillBuilder().name("test").build();
		wrongBill.setUserUuid("wrong");
		repository.save(wrongBill);

		List<Bill> accounts = repository.userBills();
		assertThat(accounts.size(), is(1));
		assertTrue(accounts.contains(correctBill));
		assertFalse(accounts.contains(wrongBill));
	}
}