package br.com.jonathanzanella.myexpenses.card;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by jzanella on 8/27/16.
 */
public class CardPresenterTest {
	private static final String UUID = "uuid";
	@Mock
	private CardRepository repository;
	@Mock
	private AccountRepository accountRepository;
	@Mock
	private CardContract.EditView view;

	private CardPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		presenter = new CardPresenter(repository, accountRepository);
		presenter.attachView(view);
	}

	@Test(expected = CardNotFoundException.class)
	public void load_empty_card_throws_not_found_exception() {
		when(repository.find(UUID)).thenReturn(null);

		presenter.loadCard(UUID);
	}

	@Test
	public void save_gets_data_from_screen_and_save_to_repository() {
		when(repository.save(any(Card.class))).thenReturn(new OperationResult());
		when(view.fillCard(any(Card.class))).thenReturn(new Card());

		presenter.save();

		verify(view, times(1)).fillCard(any(Card.class));
		verify(repository, times(1)).save(any(Card.class));
		verify(view, times(1)).finishView();
	}

	@Test
	public void call_view_with_errors() {
		OperationResult result = new OperationResult();
		result.addError(ValidationError.NAME);

		when(view.fillCard(any(Card.class))).thenReturn(new Card());
		when(repository.save(any(Card.class))).thenReturn(result);

		presenter.save();

		verify(view, times(1)).showError(ValidationError.NAME);
	}
}