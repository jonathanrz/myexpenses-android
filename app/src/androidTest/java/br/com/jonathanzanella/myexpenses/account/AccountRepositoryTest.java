package br.com.jonathanzanella.myexpenses.account;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import br.com.jonathanzanella.myexpenses.App;
import br.com.jonathanzanella.myexpenses.injection.DaggerTestComponent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AccountRepositoryTest {
	@Inject
	AccountRepository repository;

	@Before
	public void setUp() throws Exception {
		DaggerTestComponent.builder().build().inject(this);
		App.Companion.resetDatabase();
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