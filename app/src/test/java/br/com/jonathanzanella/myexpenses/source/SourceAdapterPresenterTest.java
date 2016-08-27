package br.com.jonathanzanella.myexpenses.source;

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
public class SourceAdapterPresenterTest {
	@Mock
	private SourceAdapter adapter;
	@Mock
	private SourceRepository repository;

	SourceAdapterPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);

		presenter = new SourceAdapterPresenter(adapter, repository);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void get_sources_return_unmodifiable_list() {
		when(repository.userSources()).thenReturn(new ArrayList<Source>());

		List<Source> sources = presenter.getSources(false);
		sources.add(new Source());
	}
}