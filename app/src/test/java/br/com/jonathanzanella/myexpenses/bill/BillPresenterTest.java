package br.com.jonathanzanella.myexpenses.bill;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;

import static io.reactivex.Observable.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BillPresenterTest {
	private static final String UUID = "uuid";
	@Mock
	private BillDataSource dataSource;
	@Mock
	private BillContract.EditView view;

	private BillPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		presenter = new BillPresenter(dataSource);
		presenter.attachView(view);
	}

	@Test
	@Ignore("fix when convert test to kotlin")
	public void save_gets_data_from_screen_and_save_to_repository() {
		when(view.fillBill(any(Bill.class))).thenReturn(new Bill());
		when(dataSource.save(any(Bill.class))).thenReturn(just(new ValidationResult()));

		presenter.save();

		verify(view, times(1)).fillBill(any(Bill.class));
		verify(dataSource, times(1)).save(any(Bill.class));
		verify(view, times(1)).finishView();
	}

	@Test
	@Ignore("fix when convert test to kotlin")
	public void call_view_with_errors() {
		ValidationResult result = new ValidationResult();
		result.addError(ValidationError.NAME);

		when(view.fillBill(any(Bill.class))).thenReturn(new Bill());
		when(dataSource.save(any(Bill.class))).thenReturn(just(result));

		presenter.save();

		verify(view, times(1)).showError(ValidationError.NAME);
	}

	@Test
	@Ignore("fix when convert test to kotlin")
	public void empty_bill_does_not_not_call_show_bill() throws Exception {
		when(dataSource.find(UUID)).thenReturn(null);

		presenter.loadBill(UUID);
		verify(view, times(0)).showBill(any(Bill.class));
	}
}