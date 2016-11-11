package br.com.jonathanzanella.myexpenses;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.helpers.FlowManagerHelper;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ReceiptsInPeriodTestSuite {
	DateTime firstDayOfJune = new DateTime(2016, 6, 1, 0, 0, 0, 0);
	DateTime lastDayOfJune = firstDayOfJune.dayOfMonth().withMaximumValue();
	DateTime firstDayOfJuly = firstDayOfJune.plusMonths(1);

	Account account = new Account();
	Source source = new Source();

	SourceRepository sourceRepository = new SourceRepository(new Repository<Source>(getContext()));

	@Before
	public void setUp() throws Exception {
		account.setName("Account");
		account.setUserUuid(Environment.CURRENT_USER_UUID);
		account.save();

		source.setName("Account");
		source.setUserUuid(Environment.CURRENT_USER_UUID);
		sourceRepository.save(source);
	}

	@After
	public void tearDown() throws Exception {
		FlowManagerHelper.reset(getContext());
	}

	private Receipt newReceipt(String name, DateTime date, int value) {
		Receipt receipt = new Receipt();
		receipt.setUserUuid(Environment.CURRENT_USER_UUID);
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
		firstOfMonth.save();

		Receipt endOfMonth = newReceipt("End", lastDayOfJune.withHourOfDay(23), 500);
		endOfMonth.save();

		Receipt firstOfJuly = newReceipt("July", firstDayOfJuly, 200);
		firstOfJuly.save();

		List<Receipt> receipts = Receipt.monthly(firstDayOfJune);
		assertThat(receipts.size(), is(2));
		assertThat(receipts.get(0).getUuid(), is(firstOfMonth.getUuid()));
		assertThat(receipts.get(1).getUuid(), is(endOfMonth.getUuid()));
	}

	private Context getContext() {
		return InstrumentationRegistry.getTargetContext();
	}
}