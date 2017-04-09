package br.com.jonathanzanella.myexpenses.source;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.validations.ValidationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SourcePresenterTest {
	private static final String UUID = "uuid";
	@Mock
	private SourceRepository repository;
	@Mock
	private SourceContract.EditView view;

	private SourcePresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		presenter = new SourcePresenter(repository);
		presenter.attachView(view);
	}

	@Test
	public void save_gets_data_from_screen_and_save_to_repository() {
		when(repository.save(any(Source.class))).thenReturn(new ValidationResult());

		presenter.save();

		verify(view, times(1)).fillSource(any(Source.class));
		verify(repository, times(1)).save(any(Source.class));
		verify(view, times(1)).finishView();
	}

	@Test
	public void call_view_with_errors() {
		ValidationResult result = new ValidationResult();
		result.addError(ValidationError.NAME);

		when(repository.save(any(Source.class))).thenReturn(result);

		presenter.save();

		verify(view, times(1)).showError(ValidationError.NAME);
	}
}