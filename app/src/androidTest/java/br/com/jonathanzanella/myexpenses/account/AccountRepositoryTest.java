package br.com.jonathanzanella.myexpenses.account;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AccountRepositoryTest {
	private final AccountRepository repository = new AccountRepository(new RepositoryImpl<Account>(MyApplication.getContext()));

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();
	}

	@Test
	public void can_save_account() throws Exception {
		Account account = new Account();
		account.setName("test");
		repository.save(account);

		assertThat(account.getId(), is(not(0L)));
		assertThat(account.getUuid(), is(not("")));
	}

	@Test
	public void can_load_saved_account() throws Exception {
		Account account = new Account();
		account.setName("test");
		repository.save(account);

		Account loadAccount = repository.find(account.getUuid());
		assertThat(loadAccount.getUuid(), is(account.getUuid()));
	}
}