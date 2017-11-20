package br.com.jonathanzanella.myexpenses.unit.receipt;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.account.AccountDataSource;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReceiptPresenterTest {
	private static final String UUID = "uuid";
	@Mock
	private br.com.jonathanzanella.myexpenses.receipt.ReceiptDataSource dataSource;
	@Mock
	private SourceRepository sourceRepository;
	@Mock
	private AccountDataSource accountDataSource;
	@Mock
	private br.com.jonathanzanella.myexpenses.receipt.ReceiptContract.EditView view;

	private br.com.jonathanzanella.myexpenses.receipt.ReceiptPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		presenter = new br.com.jonathanzanella.myexpenses.receipt.ReceiptPresenter(dataSource, sourceRepository, accountDataSource);
		presenter.attachView(view);
	}

	@Test(expected = br.com.jonathanzanella.myexpenses.receipt.ReceiptNotFoundException.class)
	@Ignore("fix when convert to kotlin")
	public void load_empty_source_throws_not_found_exception() {
		when(dataSource.find(UUID)).thenReturn(null);

		presenter.loadReceipt(UUID);
	}

	@Test
	@Ignore("fix when convert to kotlin")
	public void save_gets_data_from_screen_and_save_to_repository() {
		when(view.fillReceipt(any(br.com.jonathanzanella.myexpenses.receipt.Receipt.class))).thenReturn(new br.com.jonathanzanella.myexpenses.receipt.Receipt());
		when(dataSource.save(any(br.com.jonathanzanella.myexpenses.receipt.Receipt.class))).thenReturn(new ValidationResult());

		presenter.save();

		verify(view, times(1)).fillReceipt(any(br.com.jonathanzanella.myexpenses.receipt.Receipt.class));
		verify(dataSource, times(1)).save(any(br.com.jonathanzanella.myexpenses.receipt.Receipt.class));
		verify(view, times(1)).finishView();
	}

	@Test
	@Ignore("fix when convert to kotlin")
	public void call_view_with_errors() {
		ValidationResult result = new ValidationResult();
		result.addError(ValidationError.NAME);

		when(view.fillReceipt(any(br.com.jonathanzanella.myexpenses.receipt.Receipt.class))).thenReturn(new br.com.jonathanzanella.myexpenses.receipt.Receipt());
		when(dataSource.save(any(br.com.jonathanzanella.myexpenses.receipt.Receipt.class))).thenReturn(result);

		presenter.save();

		verify(view, times(1)).showError(ValidationError.NAME);
	}
}