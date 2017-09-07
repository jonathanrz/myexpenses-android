package br.com.jonathanzanella.myexpenses.bill;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BillAdapterPresenterTest {
	@Mock
	private BillDataSource dataSource;

	private BillAdapterPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);

		when(dataSource.all()).thenReturn(new ArrayList<>());

		presenter = new BillAdapterPresenter(dataSource);
	}

	@Test(expected = UnsupportedOperationException.class)
	@Ignore("fix when convert tests to kotlin")
	public void get_sources_return_unmodifiable_list() {
		List<Bill> bills = presenter.getBills(false);
		bills.add(new Bill());
	}
}