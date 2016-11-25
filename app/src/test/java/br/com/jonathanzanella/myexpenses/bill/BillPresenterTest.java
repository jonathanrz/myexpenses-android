package br.com.jonathanzanella.myexpenses.bill;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.Callable;

import br.com.jonathanzanella.myexpenses.helpers.CountingIdlingResource;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by jzanella on 8/27/16.
 */
public class BillPresenterTest {
	private static final String UUID = "uuid";
	@Mock
	private BillRepository repository;
	@Mock
	private BillContract.EditView view;
	@Mock
	private CountingIdlingResource idlingResource;

	private BillPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		presenter = new BillPresenter(repository, idlingResource);
		presenter.attachView(view);
	}

	@Test
	public void save_gets_data_from_screen_and_save_to_repository() {
		when(view.fillBill(any(Bill.class))).thenReturn(new Bill());
		when(repository.save(any(Bill.class))).thenReturn(new OperationResult());

		presenter.save();

		verify(view, times(1)).fillBill(any(Bill.class));
		verify(repository, times(1)).save(any(Bill.class));
		verify(view, times(1)).finishView();
	}

	@Test
	public void call_view_with_errors() {
		OperationResult result = new OperationResult();
		result.addError(ValidationError.NAME);

		when(view.fillBill(any(Bill.class))).thenReturn(new Bill());
		when(repository.save(any(Bill.class))).thenReturn(result);

		presenter.save();

		verify(view, times(1)).showError(ValidationError.NAME);
	}

	@Test
	public void empty_bill_does_not_not_call_show_bill() throws Exception {
		when(repository.find(UUID)).thenReturn(Observable.fromCallable(new Callable<Bill>() {
			@Override
			public Bill call() throws Exception {
				return null;
			}
		}));

		presenter.loadBill(UUID);
		verify(view, times(0)).showBill(any(Bill.class));
	}
}