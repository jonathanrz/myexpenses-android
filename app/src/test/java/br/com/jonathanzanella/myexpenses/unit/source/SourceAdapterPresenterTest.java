package br.com.jonathanzanella.myexpenses.unit.source;

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
	private br.com.jonathanzanella.myexpenses.source.SourceDataSource dataSource;

	private br.com.jonathanzanella.myexpenses.source.SourceAdapterPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);

		presenter = new br.com.jonathanzanella.myexpenses.source.SourceAdapterPresenter(dataSource);
	}

	@Test(expected = UnsupportedOperationException.class)
	@Ignore("fix when convert  to kotlin")
	public void get_sources_return_unmodifiable_list() {
		when(dataSource.all()).thenReturn(new ArrayList<>());

		List<br.com.jonathanzanella.myexpenses.source.Source> sources = presenter.getSources(false);
		sources.add(new br.com.jonathanzanella.myexpenses.source.Source());
	}
}