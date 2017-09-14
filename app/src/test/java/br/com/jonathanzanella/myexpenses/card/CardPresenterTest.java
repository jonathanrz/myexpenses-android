package br.com.jonathanzanella.myexpenses.card;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountDataSource;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helper.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helper.builder.CardBuilder;
import br.com.jonathanzanella.myexpenses.helper.builder.ExpenseBuilder;
import br.com.jonathanzanella.myexpenses.helpers.ResourcesHelper;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;
import io.reactivex.Maybe;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CardPresenterTest {
	private static final String UUID = "uuid";
	@Mock
	private CardDataSource dataSource;
	@Mock
	private AccountDataSource accountDataSource;
	@Mock
	private ExpenseRepository expenseRepository;
	@Mock
	private CardContract.EditView view;
	@Mock
	private ResourcesHelper resourcesHelper;

	private CardPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		DateTimeZone.setDefault(DateTimeZone.UTC);
		presenter = new CardPresenter(accountDataSource, dataSource, expenseRepository, resourcesHelper);
		presenter.attachView(view);
	}

	@Test(expected = CardNotFoundException.class)
	@Ignore("fix when convert tests to kotlin")
	public void load_empty_card_throws_not_found_exception() {
		when(dataSource.find(UUID)).thenReturn(null);

		presenter.loadCard(UUID);
	}

	@Test
	@Ignore("fix when convert tests to kotlin")
	public void save_gets_data_from_screen_and_save_to_repository() {
		when(dataSource.save(any(Card.class))).thenReturn(new ValidationResult());
		when(view.fillCard(any(Card.class))).thenReturn(new Card(accountDataSource));

		presenter.save();

		verify(view, times(1)).fillCard(any(Card.class));
		verify(dataSource, times(1)).save(any(Card.class));
		verify(view, times(1)).finishView();
	}

	@Test
	@Ignore("fix when convert tests to kotlin")
	public void call_view_with_errors() {
		ValidationResult result = new ValidationResult();
		result.addError(ValidationError.NAME);

		when(view.fillCard(any(Card.class))).thenReturn(new Card(accountDataSource));
		when(dataSource.save(any(Card.class))).thenReturn(result);

		presenter.save();

		verify(view, times(1)).showError(ValidationError.NAME);
	}

	@Test
	@Ignore("fix when convert tests to kotlin")
	public void generate_card_bill_value_correctly() throws Exception {
		final String uuid = "uuid";
		final int value = 100;
		Account account = new AccountBuilder().build();
		Card card = new CardBuilder().account(account).build(accountDataSource);
		when(dataSource.find(uuid)).thenReturn(card);
		when(accountDataSource.find(anyString())).thenReturn(Maybe.just(account));
		when(expenseRepository.save(any(Expense.class))).thenReturn(new ValidationResult());
		List<Expense> expenseList = new ArrayList<>();
		expenseList.add(new ExpenseBuilder().value(value).build());
		expenseList.add(new ExpenseBuilder().value(value).build());
		when(expenseRepository.creditCardBills(any(Card.class), any(DateTime.class))).thenReturn(expenseList);

		String invoice = "Fatura";
		when(resourcesHelper.getString(R.string.invoice)).thenReturn(invoice);

		presenter.loadCard(uuid);
		Expense expense = presenter.generateCreditCardBill(new DateTime(2016, 9, 26, 0, 0, 0, DateTimeZone.UTC));

		assertThat(expense.getName(), is(invoice + " " + card.getName()));
		assertThat(expense.getValue(), is(value * expenseList.size()));
		assertThat(expense.getChargeableFromCache().getName(), is(account.getName()));
		assertTrue(expenseList.get(0).getCharged());
		assertTrue(expenseList.get(1).getCharged());
	}

	@Test
	@Ignore("fix when convert tests to kotlin")
	public void not_generate_card_bill_when_there_are_no_expenses() throws Exception {
		final String uuid = "uuid";
		Account account = new AccountBuilder().build();
		Card card = new CardBuilder().account(account).build(accountDataSource);
		when(dataSource.find(uuid)).thenReturn(card);
		List<Expense> expenseList = new ArrayList<>();
		when(expenseRepository.creditCardBills(any(Card.class), any(DateTime.class))).thenReturn(expenseList);

		String invoice = "Fatura";
		when(resourcesHelper.getString(R.string.invoice)).thenReturn(invoice);

		presenter.loadCard(uuid);
		Expense expense = presenter.generateCreditCardBill(new DateTime(2016, 9, 26, 0, 0, 0, DateTimeZone.UTC));

		assertNull(expense);
	}
}