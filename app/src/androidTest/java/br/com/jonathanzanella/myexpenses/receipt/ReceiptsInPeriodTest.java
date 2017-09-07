package br.com.jonathanzanella.myexpenses.receipt;

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
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
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
	AccountRepository accountRepository;
	@Inject
	ReceiptRepository receiptRepository;

	@Before
	public void setUp() throws Exception {
		TestApp.Companion.getTestComponent().inject(this);
		App.Companion.resetDatabase();

		account.setName("Account");
		accountRepository.save(account);

		source.setName("Source");
		sourceRepository.save(source);
	}

	private Receipt newReceipt(String name, DateTime date, int value) {
		Receipt receipt = new Receipt();
		receipt.setName(name);
		receipt.setSource(source);
		receipt.setAccount(account);
		receipt.setDate(date);
		receipt.setIncome(value);
		return receipt;
	}

	@Test
	public void testReceiptsInMonthly() {
		Receipt firstOfMonth = newReceipt("First", firstDayOfJune, 1000);
		receiptRepository.save(firstOfMonth);

		Receipt endOfMonth = newReceipt("End", lastDayOfJune.withHourOfDay(23), 500);
		receiptRepository.save(endOfMonth);

		Receipt firstOfJuly = newReceipt("July", firstDayOfJuly, 200);
		receiptRepository.save(firstOfJuly);

		List<Receipt> receipts = receiptRepository.monthly(firstDayOfJune);
		assertThat(receipts.size(), is(2));
		assertThat(receipts.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(receipts.get(1).getUuid(), is(endOfMonth.getUuid()));
	}

	private Context getContext() {
		return getTargetContext();
	}
}