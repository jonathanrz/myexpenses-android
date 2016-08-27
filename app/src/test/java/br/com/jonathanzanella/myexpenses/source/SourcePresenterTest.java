package br.com.jonathanzanella.myexpenses.source;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by jzanella on 8/27/16.
 */
public class SourcePresenterTest {
	private static final String UUID = "uuid";
	@Mock
	SourceRepository repository;
	@Mock
	SourceContract.View view;

	SourcePresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		presenter = new SourcePresenter(view, repository);
	}

	@Test(expected = SourceNotFoundException.class)
	public void load_empty_source_throws_not_found_exception() throws Exception {
		when(repository.find(UUID)).thenReturn(null);

		presenter.loadSource(UUID);
	}

	@Test
	public void save_gets_data_from_screen_and_save_to_repository() throws Exception {
		presenter.save();

		verify(view, times(1)).fillSource(any(Source.class));
		verify(repository, times(1)).save(any(Source.class));
		verify(view, times(1)).finishView();
	}
}