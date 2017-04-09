package br.com.jonathanzanella.myexpenses.bill;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BillAdapterPresenterTest {
	@Mock
	private BillAdapter adapter;
	@Mock
	private BillRepository repository;

	private BillAdapterPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);

		presenter = new BillAdapterPresenter(repository);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void get_sources_return_unmodifiable_list() {
		when(repository.all()).thenReturn(new ArrayList<Bill>());

		List<Bill> bills = presenter.getBills(false);
		bills.add(new Bill());
	}
}