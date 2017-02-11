package br.com.jonathanzanella.myexpenses.account;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AccountPresenterTest {
	@Mock
	private AccountRepository repository;
	@Mock
	private AccountContract.EditView view;

	private AccountPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		presenter = new AccountPresenter(repository);
		presenter.attachView(view);
	}

	@Test
	public void save_gets_data_from_screen_and_save_to_repository() {
		when(repository.save(any(Account.class))).thenReturn(new OperationResult());

		presenter.save();

		verify(view, times(1)).fillAccount(any(Account.class));
		verify(repository, times(1)).save(any(Account.class));
		verify(view, times(1)).finishView();
	}

	@Test
	public void call_view_with_errors() {
		OperationResult result = new OperationResult();
		result.addError(ValidationError.NAME);

		when(repository.save(any(Account.class))).thenReturn(result);

		presenter.save();

		verify(view, times(1)).showError(ValidationError.NAME);
	}
}