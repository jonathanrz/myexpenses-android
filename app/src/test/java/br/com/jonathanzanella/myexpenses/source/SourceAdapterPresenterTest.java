package br.com.jonathanzanella.myexpenses.source;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SourceAdapterPresenterTest {
	@Mock
	private SourceDataSource dataSource;

	private SourceAdapterPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);

		presenter = new SourceAdapterPresenter(dataSource);
	}

	@Test(expected = UnsupportedOperationException.class)
	@Ignore("fix when convert  to kotlin")
	public void get_sources_return_unmodifiable_list() {
		when(dataSource.all()).thenReturn(new ArrayList<>());

		List<Source> sources = presenter.getSources(false);
		sources.add(new Source());
	}
}