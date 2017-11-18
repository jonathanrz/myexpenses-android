package br.com.jonathanzanella.myexpenses.ui.receipt;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import javax.inject.Inject;

import br.com.jonathanzanella.TestApp;
import br.com.jonathanzanella.myexpenses.App;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountDataSource;
import br.com.jonathanzanella.myexpenses.ui.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ReceiptRepositoryTest {
	@Inject
	br.com.jonathanzanella.myexpenses.receipt.ReceiptDataSource dataSource;
	@Inject
	AccountDataSource accountDataSource;
	@Inject
	SourceRepository sourceRepository;

	private Source source;
	private Account account;

	@Before
	public void setUp() throws Exception {
		TestApp.Companion.getTestComponent().inject(this);
		App.Companion.resetDatabase();

		account = new AccountBuilder().build();
		accountDataSource.save(account);
		source = new SourceBuilder().build();
		sourceRepository.save(source);
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void can_save_receipt() throws Exception {
		br.com.jonathanzanella.myexpenses.receipt.Receipt receipt = new ReceiptBuilder().source(source).account(account).build();
		dataSource.save(receipt);

		assertThat(receipt.getId(), is(not(0L)));
		assertThat(receipt.getUuid(), is(not("")));
	}

	@Test
	public void can_load_saved_receipt() throws Exception {
		br.com.jonathanzanella.myexpenses.receipt.Receipt receipt = new ReceiptBuilder()
				.source(source)
				.account(account)
				.build();
		dataSource.save(receipt);

		br.com.jonathanzanella.myexpenses.receipt.Receipt loadReceipt = dataSource.find(receipt.getUuid());
		assertThat(loadReceipt.getUuid(), is(receipt.getUuid()));
	}

	@Test
	public void load_receipts_ordered_by_date() throws Exception {
		DateTime date = DateTime.now();
		br.com.jonathanzanella.myexpenses.receipt.Receipt receiptA = new ReceiptBuilder()
				.name("a")
				.source(source)
				.account(account)
				.date(date)
				.build();
		dataSource.save(receiptA);

		br.com.jonathanzanella.myexpenses.receipt.Receipt receiptB = new ReceiptBuilder()
				.name("b")
				.source(source)
				.account(account)
				.date(date.minusDays(1))
				.build();
		dataSource.save(receiptB);

		List<br.com.jonathanzanella.myexpenses.receipt.Receipt> sources = dataSource.all();
		assertThat(sources.get(0).getUuid(), is(receiptB.getUuid()));
		assertThat(sources.get(1).getUuid(), is(receiptA.getUuid()));
	}

	@Test
	public void load_only_not_removed_receipts() throws Exception {
		br.com.jonathanzanella.myexpenses.receipt.Receipt receiptSaved = new ReceiptBuilder()
				.name("a")
				.source(source)
				.account(account)
				.removed(false)
				.build();
		dataSource.save(receiptSaved);

		br.com.jonathanzanella.myexpenses.receipt.Receipt receiptRemoved = new ReceiptBuilder()
				.name("b")
				.source(source)
				.account(account)
				.removed(true)
				.build();
		dataSource.save(receiptRemoved);

		List<br.com.jonathanzanella.myexpenses.receipt.Receipt> sources = dataSource.all();
		assertThat(sources.size(), is(1));
		assertThat(sources.get(0).getUuid(), is(receiptSaved.getUuid()));
	}
}