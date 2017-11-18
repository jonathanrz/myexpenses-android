package br.com.jonathanzanella.myexpenses.ui.receipt;

import android.content.Context;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import javax.inject.Inject;

import br.com.jonathanzanella.TestApp;
import br.com.jonathanzanella.myexpenses.App;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountDataSource;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ReceiptsInPeriodTest {
	private final DateTime firstDayOfJune = new DateTime(2016, 6, 1, 0, 0, 0, 0);
	private final DateTime lastDayOfJune = firstDayOfJune.dayOfMonth().withMaximumValue();
	private final DateTime firstDayOfJuly = firstDayOfJune.plusMonths(1);

	private final Account account = new Account();
	private final Source source = new Source();

	@Inject
	SourceRepository sourceRepository;
	@Inject
	AccountDataSource accountDataSource;
	@Inject
	br.com.jonathanzanella.myexpenses.receipt.ReceiptDataSource receiptDataSource;

	@Before
	public void setUp() throws Exception {
		TestApp.Companion.getTestComponent().inject(this);
		App.Companion.resetDatabase();

		account.setName("Account");
		accountDataSource.save(account);

		source.setName("Source");
		sourceRepository.save(source);
	}

	private br.com.jonathanzanella.myexpenses.receipt.Receipt newReceipt(String name, DateTime date, int value) {
		br.com.jonathanzanella.myexpenses.receipt.Receipt receipt = new br.com.jonathanzanella.myexpenses.receipt.Receipt();
		receipt.setName(name);
		receipt.setSource(source);
		receipt.setAccount(account);
		receipt.setDate(date);
		receipt.setIncome(value);
		return receipt;
	}

	@Test
	public void testReceiptsInMonthly() {
		br.com.jonathanzanella.myexpenses.receipt.Receipt firstOfMonth = newReceipt("First", firstDayOfJune, 1000);
		receiptDataSource.save(firstOfMonth);

		br.com.jonathanzanella.myexpenses.receipt.Receipt endOfMonth = newReceipt("End", lastDayOfJune.withHourOfDay(23), 500);
		receiptDataSource.save(endOfMonth);

		br.com.jonathanzanella.myexpenses.receipt.Receipt firstOfJuly = newReceipt("July", firstDayOfJuly, 200);
		receiptDataSource.save(firstOfJuly);

		List<br.com.jonathanzanella.myexpenses.receipt.Receipt> receipts = receiptDataSource.monthly(firstDayOfJune);
		assertThat(receipts.size(), is(2));
		assertThat(receipts.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(receipts.get(1).getUuid(), is(endOfMonth.getUuid()));
	}

	private Context getContext() {
		return getTargetContext();
	}
}