package br.com.jonathanzanella.myexpenses.card;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.helpers.FlowManagerHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.CardBuilder;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by jzanella on 8/27/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CardRepositoryTest {
	private CardRepository repository = new CardRepository();
	private Account account;
	private AccountRepository accountRepository;

	@Before
	public void setUp() throws Exception {
		account = new Account();
		account.setName("test");
		accountRepository = new AccountRepository(new Repository<Account>(MyApplication.getContext()));
		accountRepository.save(account);
	}

	@After
	public void tearDown() throws Exception {
		FlowManagerHelper.reset(InstrumentationRegistry.getTargetContext());
	}

	@Test
	public void can_save_card() throws Exception {
		Card card = new CardBuilder().account(account).build(accountRepository);
		repository.save(card);

		assertThat(card.id, is(not(0L)));
		assertThat(card.getUuid(), is(not("")));
	}

	@Test
	public void can_load_saved_card() throws Exception {
		Card card = new CardBuilder().account(account).build(accountRepository);
		repository.save(card);

		Card loadCard = repository.find(card.getUuid());
		assertThat(loadCard.getUuid(), is(card.getUuid()));
	}

	@Test
	public void load_only_user_cards() throws Exception {
		Card correctCard = new CardBuilder().account(account).build(accountRepository);
		correctCard.setUserUuid(Environment.CURRENT_USER_UUID);
		repository.save(correctCard);

		Card wrongCard = new CardBuilder().name("wrongCard").account(account).build(accountRepository);
		wrongCard.setUserUuid("wrong");
		repository.save(wrongCard);

		List<Card> cards = repository.userCards();
		assertThat(cards.size(), is(1));
		assertThat(cards.get(0).getUuid(), is(correctCard.getUuid()));
		assertFalse(cards.contains(wrongCard));
	}
}