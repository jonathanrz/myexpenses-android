package br.com.jonathanzanella.myexpenses.account;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.Repository;

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
public class AccountRepositoryTest {
	private AccountRepository repository = new AccountRepository(new Repository<Account>(MyApplication.getContext()));

	@After
	public void tearDown() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();
	}

	@Test
	public void can_save_account() throws Exception {
		Account account = new Account();
		account.setName("test");
		repository.save(account);

		assertThat(account.id, is(not(0L)));
		assertThat(account.getUuid(), is(not("")));
	}

	@Test
	public void can_load_saved_account() throws Exception {
		Account account = new Account();
		account.setName("test");
		repository.save(account);

		Account loadAccount = repository.find(account.getUuid());
		assertThat(loadAccount, is(account));
	}

	@Test
	public void load_only_user_accounts() throws Exception {
		Account correctAccount = new Account();
		correctAccount.setName("test");
		correctAccount.setUserUuid(Environment.CURRENT_USER_UUID);
		repository.save(correctAccount);

		Account wrongAccount = new Account();
		wrongAccount.setName("test2");
		wrongAccount.setUserUuid("wrong");
		repository.save(wrongAccount);

		List<Account> accounts = repository.userAccounts();
		assertThat(accounts.size(), is(1));
		assertTrue(accounts.contains(correctAccount));
		assertFalse(accounts.contains(wrongAccount));
	}
}