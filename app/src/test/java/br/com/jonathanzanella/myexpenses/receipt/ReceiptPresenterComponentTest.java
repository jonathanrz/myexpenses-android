package br.com.jonathanzanella.myexpenses.receipt;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.helper.builder.ReceiptBuilder;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReceiptPresenterComponentTest {
	private ReceiptRepository repository;
	private ReceiptPresenter presenter;

	@Mock
	private ReceiptContract.EditView view;
	@Mock
	private AccountRepository accountRepository;
	@Mock
	private SourceRepository sourceRepository;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		repository = new ReceiptRepository();
		presenter = new ReceiptPresenter(repository, sourceRepository, accountRepository);
		presenter.attachView(view);
	}

	@Test
	public void generate_expenses_with_installments() throws Exception {
		DateTime date = new DateTime(2016, 9, 26, 0, 0, 0, DateTimeZone.UTC);
		int income = 30000;
		String name = "expense installment";
		Receipt receipt = new ReceiptBuilder()
				.name(name)
				.income(income)
				.date(date)
				.installments(3)
				.build();
		when(view.fillReceipt(any(Receipt.class))).thenReturn(receipt);
		presenter.save();

		List<Receipt> receipts = repository.all();
		assertThat(receipts.size(), is(3));
		assertThat(receipts.get(0).getName(), is(name + " 01/03"));
		assertThat(receipts.get(0).getDate().getMonthOfYear(), is(date.getMonthOfYear()));
		assertThat(receipts.get(0).getIncome(), is(10000));
		assertThat(receipts.get(1).getName(), is(name + " 02/03"));
		assertThat(receipts.get(1).getDate().getMonthOfYear(), is(date.getMonthOfYear() + 1));
		assertThat(receipts.get(1).getIncome(), is(10000));
		assertThat(receipts.get(2).getName(), is(name + " 03/03"));
		assertThat(receipts.get(2).getDate().getMonthOfYear(), is(date.getMonthOfYear() + 2));
		assertThat(receipts.get(2).getIncome(), is(10000));
	}

	@Test
	public void generate_expenses_with_repetition() throws Exception {
		DateTime date = new DateTime(2016, 9, 26, 0, 0, 0, DateTimeZone.UTC);
		int income = 30000;
		String name = "expense repetition";
		Receipt receipt = new ReceiptBuilder()
				.name(name)
				.income(income)
				.date(date)
				.repetition(3)
				.build();
		when(view.fillReceipt(any(Receipt.class))).thenReturn(receipt);
		presenter.save();

		List<Receipt> receipts = repository.all();
		assertThat(receipts.size(), is(3));
		assertThat(receipts.get(0).getName(), is(name));
		assertThat(receipts.get(0).getDate().getMonthOfYear(), is(date.getMonthOfYear()));
		assertThat(receipts.get(0).getIncome(), is(income));
		assertThat(receipts.get(1).getName(), is(name));
		assertThat(receipts.get(1).getDate().getMonthOfYear(), is(date.getMonthOfYear() + 1));
		assertThat(receipts.get(1).getIncome(), is(income));
		assertThat(receipts.get(2).getName(), is(name));
		assertThat(receipts.get(2).getDate().getMonthOfYear(), is(date.getMonthOfYear() + 2));
		assertThat(receipts.get(2).getIncome(), is(income));
	}
}
