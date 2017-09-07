package br.com.jonathanzanella.myexpenses.receipt;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReceiptAdapterPresenterTest {
	@Mock
	private ReceiptDataSource dataSource;

	private ReceiptAdapterPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);

		presenter = new ReceiptAdapterPresenter(dataSource);
	}

	@Test(expected = UnsupportedOperationException.class)
	@Ignore("fix when convert to kotlin")
	public void get_sources_return_unmodifiable_list() {
		DateTime dateTime = new DateTime(2016, 9, 26, 0, 0, 0, DateTimeZone.UTC);
		when(dataSource.monthly(dateTime)).thenReturn(new ArrayList<>());

		List<Receipt> receipts = presenter.getReceipts(true, dateTime);
		receipts.add(new Receipt());
	}
}