package br.com.jonathanzanella.myexpenses.receipt;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by jzanella on 8/27/16.
 */
public class ReceiptAdapterPresenterTest {
	@Mock
	private ReceiptAdapter adapter;
	@Mock
	private ReceiptRepository repository;

	ReceiptAdapterPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);

		presenter = new ReceiptAdapterPresenter(adapter, repository);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void get_sources_return_unmodifiable_list() {
		DateTime dateTime = new DateTime(2016, 9, 26, 0, 0, 0, DateTimeZone.UTC);
		when(repository.monthly(dateTime)).thenReturn(new ArrayList<Receipt>());

		List<Receipt> receipts = presenter.getReceipts(true, dateTime);
		receipts.add(new Receipt());
	}
}