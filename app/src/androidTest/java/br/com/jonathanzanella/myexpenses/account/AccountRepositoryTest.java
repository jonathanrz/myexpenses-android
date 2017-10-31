package br.com.jonathanzanella.myexpenses.account;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import br.com.jonathanzanella.TestApp;
import br.com.jonathanzanella.myexpenses.App;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AccountRepositoryTest {
	@Inject
	AccountDataSource dataSource;

	@Before
	public void setUp() throws Exception {
		TestApp.Companion.getTestComponent().inject(this);
		App.Companion.resetDatabase();
	}

	@Test
	public void can_save_account() throws Exception {
		Account account = new Account();
		account.setName("test");
		dataSource.save(account);

		assertThat(account.getId(), is(not(0L)));
		assertThat(account.getUuid(), is(not("")));
	}

	@Test
	public void can_load_saved_account() throws Exception {
		Account account = new Account();
		account.setName("test");
		dataSource.save(account);

		Account loadAccount = dataSource.find(account.getUuid()).blockingFirst();
		assertThat(loadAccount.getUuid(), is(account.getUuid()));
	}
}