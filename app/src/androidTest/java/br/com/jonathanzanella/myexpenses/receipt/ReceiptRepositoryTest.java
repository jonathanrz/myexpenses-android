package br.com.jonathanzanella.myexpenses.receipt;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ReceiptRepositoryTest {
	private ReceiptRepository repository;

	private Source source;
	private Account account;

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(getTargetContext()).recreateTables();

		account = new AccountBuilder().build();
		new AccountRepository(new RepositoryImpl<Account>(getTargetContext())).save(account);
		source = new SourceBuilder().build();
		new SourceRepository(new RepositoryImpl<Source>(getTargetContext())).save(source);
		repository = new ReceiptRepository(new RepositoryImpl<Receipt>(getTargetContext()));
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void can_save_receipt() throws Exception {
		Receipt receipt = new ReceiptBuilder().source(source).account(account).build();
		repository.save(receipt);

		assertThat(receipt.getId(), is(not(0L)));
		assertThat(receipt.getUuid(), is(not("")));
	}

	@Test
	public void can_load_saved_receipt() throws Exception {
		Receipt receipt = new ReceiptBuilder()
				.source(source)
				.account(account)
				.build();
		repository.save(receipt);

		Receipt loadReceipt = repository.find(receipt.getUuid());
		assertThat(loadReceipt.getUuid(), is(receipt.getUuid()));
	}

	@Test
	public void load_receipts_ordered_by_date() throws Exception {
		DateTime date = DateTime.now();
		Receipt receiptA = new ReceiptBuilder()
				.name("a")
				.source(source)
				.account(account)
				.date(date)
				.build();
		repository.save(receiptA);

		Receipt receiptB = new ReceiptBuilder()
				.name("b")
				.source(source)
				.account(account)
				.date(date.minusDays(1))
				.build();
		repository.save(receiptB);

		List<Receipt> sources = repository.all();
		assertThat(sources.get(0).getUuid(), is(receiptB.getUuid()));
		assertThat(sources.get(1).getUuid(), is(receiptA.getUuid()));
	}
}