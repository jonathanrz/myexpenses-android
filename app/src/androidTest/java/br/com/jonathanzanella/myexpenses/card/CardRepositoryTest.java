package br.com.jonathanzanella.myexpenses.card;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import br.com.jonathanzanella.TestApp;
import br.com.jonathanzanella.myexpenses.App;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountDataSource;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.CardBuilder;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class CardRepositoryTest {
	@Inject
	CardDataSource subject;
	@Inject
	AccountDataSource accountDataSource;
	private Account account;

	@Before
	public void setUp() throws Exception {
		TestApp.Companion.getTestComponent().inject(this);
		App.Companion.resetDatabase();

		account = new AccountBuilder().build();
		assertTrue(accountDataSource.save(account).blockingFirst().isValid());
	}

	@Test
	public void can_save_card() throws Exception {
		Card card = new CardBuilder().account(account).build(accountDataSource);
		subject.save(card);

		assertThat(card.getId(), is(not(0L)));
		assertThat(card.getUuid(), is(not("")));
	}

	@Test
	public void can_load_saved_card() throws Exception {
		Card card = new CardBuilder().account(account).build(accountDataSource);
		subject.save(card);

		Card loadCard = subject.find(card.getUuid());
		assertThat(loadCard.getUuid(), is(card.getUuid()));
	}

	@Test
	public void load_account_debit_card() throws Exception {
		Card debitCard = new CardBuilder().account(account).type(CardType.DEBIT).build(accountDataSource);
		assertTrue(subject.save(debitCard).isValid());

		Card loadedCard = subject.accountDebitCard(account);
		assertThat(loadedCard.getUuid(), is(debitCard.getUuid()));
	}
}