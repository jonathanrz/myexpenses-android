package br.com.jonathanzanella.myexpenses.receipt;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReceiptPresenterTest {
	private static final String UUID = "uuid";
	@Mock
	private ReceiptRepository repository;
	@Mock
	private SourceRepository sourceRepository;
	@Mock
	private AccountRepository accountRepository;
	@Mock
	private ReceiptContract.EditView view;

	private ReceiptPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		presenter = new ReceiptPresenter(repository, sourceRepository, accountRepository);
		presenter.attachView(view);
	}

	@Test(expected = ReceiptNotFoundException.class)
	public void load_empty_source_throws_not_found_exception() {
		when(repository.find(UUID)).thenReturn(null);

		presenter.loadReceipt(UUID);
	}

	@Test
	@Ignore //TODO: check how to mock and interact with the AsyncTask
	public void save_gets_data_from_screen_and_save_to_repository() {
		when(view.fillReceipt(any(Receipt.class))).thenReturn(new Receipt());
		when(repository.save(any(Receipt.class))).thenReturn(new OperationResult());

		presenter.save();

		verify(view, times(1)).fillReceipt(any(Receipt.class));
		verify(repository, times(1)).save(any(Receipt.class));
		verify(view, times(1)).finishView();
	}

	@Test
	@Ignore //TODO: check how to mock and interact with the AsyncTask
	public void call_view_with_errors() {
		OperationResult result = new OperationResult();
		result.addError(ValidationError.NAME);

		when(view.fillReceipt(any(Receipt.class))).thenReturn(new Receipt());
		when(repository.save(any(Receipt.class))).thenReturn(result);

		presenter.save();

		verify(view, times(1)).showError(ValidationError.NAME);
	}
}