package br.com.jonathanzanella.myexpenses.expense;

import android.os.AsyncTask;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.card.CardRepository;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.ModelRepository;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.database.Where;
import br.com.jonathanzanella.myexpenses.helpers.DateHelper;
import br.com.jonathanzanella.myexpenses.overview.WeeklyPagerAdapter;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static br.com.jonathanzanella.myexpenses.chargeable.ChargeableType.ACCOUNT;
import static br.com.jonathanzanella.myexpenses.chargeable.ChargeableType.DEBIT_CARD;
import static br.com.jonathanzanella.myexpenses.helpers.DateHelper.firstDayOfMonth;
import static br.com.jonathanzanella.myexpenses.helpers.DateHelper.lastDayOfMonth;
import static br.com.jonathanzanella.myexpenses.log.Log.warning;

public class ExpenseRepository implements ModelRepository<Expense> {
	private Repository<Expense> repository;
	private CardRepository cardRepository;
	private ExpenseTable table = new ExpenseTable();

	public ExpenseRepository(Repository<Expense> repository) {
		this.repository = repository;
	}

	private CardRepository getCardRepository() {
		if(cardRepository == null)
			cardRepository = new CardRepository(new Repository<Card>(MyApplication.getContext()), this);
		return cardRepository;
	}

	@WorkerThread
	public Expense find(String uuid) {
		return repository.find(table, uuid);
	}

	@WorkerThread
	List<Expense> userExpenses() {
		return repository.query(table, new Where(Fields.USER_UUID).eq(Environment.CURRENT_USER_UUID).orderBy(Fields.DATE));
	}

	@WorkerThread
	public List<Expense> monthly(DateTime month) {
		DateTime lastMonth = month.minusMonths(1);
		DateTime initOfMonth = firstDayOfMonth(lastMonth);
		DateTime endOfMonth = lastDayOfMonth(lastMonth);

		List<Expense> expenses = repository.query(table, queryBetweenUserDataAndNotRemoved(initOfMonth, endOfMonth)
							.and(Fields.CHARGE_NEXT_MONTH).eq(true)
							.orderBy(Fields.DATE));

		initOfMonth = firstDayOfMonth(month);
		endOfMonth = lastDayOfMonth(month);

		expenses.addAll(repository.query(table, queryBetween(initOfMonth, endOfMonth)
				.and(Fields.REMOVED).eq(false)
				.and(Fields.CHARGE_NEXT_MONTH).eq(false)
				.and(Fields.USER_UUID).eq(Environment.CURRENT_USER_UUID)
				.orderBy(Fields.DATE)));

		return expenses;
	}
	public List<Expense> expenses(WeeklyPagerAdapter.Period period) {
		return expenses(period, null);
	}

	@WorkerThread
	public List<Expense> expenses(WeeklyPagerAdapter.Period period, Card card) {
		List<Expense> expenses = new ArrayList<>();

		if(period.init.getDayOfMonth() == 1) {
			DateTime date = DateHelper.firstDayOfMonth(period.init);
			DateTime initOfMonth = date.minusMonths(1);
			DateTime endOfMonth = DateHelper.lastDayOfMonth(initOfMonth);

			Where where = queryBetweenUserDataAndNotRemoved(initOfMonth, endOfMonth)
					.and(Fields.CHARGE_NEXT_MONTH).eq(true)
					.and(Fields.IGNORE_IN_OVERVIEW).eq(false)
					.orderBy(Fields.DATE);
			if(card != null)
				where = where.and(Fields.CHARGEABLE_UUID).eq(card.getUuid());
			expenses.addAll(repository.query(table, where));
		}

		DateTime init = DateHelper.firstMillisOfDay(period.init);
		DateTime end = DateHelper.lastMillisOfDay(period.end);

		Where where = queryBetweenUserDataAndNotRemoved(init, end)
				.and(Fields.CHARGE_NEXT_MONTH).eq(false)
				.and(Fields.IGNORE_IN_OVERVIEW).eq(false)
				.orderBy(Fields.DATE);
		if(card != null)
			where = where.and(Fields.CHARGEABLE_UUID).eq(card.getUuid());
		expenses.addAll(repository.query(table, where));

		return expenses;
	}

	@WorkerThread
	public List<Expense> expensesForResumeScreen(DateTime date) {
		DateTime lastMonth = date.minusMonths(1);
		DateTime initOfMonth = DateHelper.firstDayOfMonth(lastMonth);
		DateTime endOfMonth = DateHelper.lastDayOfMonth(lastMonth);

		List<Expense> expenses = repository.query(table, queryBetweenUserDataAndNotRemoved(initOfMonth, endOfMonth)
				.and(Fields.CHARGEABLE_TYPE).notEq(ChargeableType.CREDIT_CARD.name())
				.and(Fields.CHARGE_NEXT_MONTH).eq(true)
				.and(Fields.IGNORE_IN_OVERVIEW).eq(false)
				.orderBy(Fields.DATE));

		initOfMonth = DateHelper.firstDayOfMonth(date);
		endOfMonth = DateHelper.lastDayOfMonth(date);

		expenses.addAll(repository.query(table, queryBetweenUserDataAndNotRemoved(initOfMonth, endOfMonth)
				.and(Fields.CHARGEABLE_TYPE).notEq(ChargeableType.CREDIT_CARD.name())
				.and(Fields.CHARGE_NEXT_MONTH).eq(false)
				.and(Fields.IGNORE_IN_OVERVIEW).eq(false)
				.orderBy(Fields.DATE)));

		DateTime creditCardMonth = date.minusMonths(1);
		List<Card> cards = getCardRepository().creditCards();
		for (Card card : cards) {
			int total = getCardRepository().getInvoiceValue(card, creditCardMonth);
			if(total == 0)
				continue;

			Expense expense = new Expense();
			expense.setChargeable(card);
			expense.setName(MyApplication.getContext().getString(R.string.invoice));
			expense.setDate(creditCardMonth);
			expense.setValue(total);
			expense.setCreditCard(card);
			expenses.add(expense);
		}

		return expenses;
	}

	@WorkerThread
	public List<Expense> accountExpenses(Account account, DateTime month) {
		DateTime lastMonth = month.minusMonths(1);
		DateTime initOfMonth = DateHelper.firstDayOfMonth(lastMonth);
		DateTime endOfMonth = DateHelper.lastDayOfMonth(lastMonth);

		Card card = getCardRepository().accountDebitCard(account);

		List<Expense> expenses = repository.query(table, queryBetweenUserDataAndNotRemoved(initOfMonth, endOfMonth)
				.and(Fields.CHARGEABLE_TYPE).eq(ACCOUNT.name())
				.and(Fields.CHARGEABLE_UUID).eq(account.getUuid())
				.and(Fields.CHARGE_NEXT_MONTH).eq(true)
				.orderBy(Fields.DATE));

		if(card != null) {
			expenses.addAll(repository.query(table, queryBetweenUserDataAndNotRemoved(initOfMonth, endOfMonth)
					.and(Fields.CHARGEABLE_TYPE).eq(DEBIT_CARD.name())
					.and(Fields.CHARGEABLE_UUID).eq(card.getUuid())
					.and(Fields.CHARGE_NEXT_MONTH).eq(true)
					.orderBy(Fields.DATE)));
		}

		initOfMonth = firstDayOfMonth(month);
		endOfMonth = lastDayOfMonth(month);

		expenses.addAll(repository.query(table, queryBetweenUserDataAndNotRemoved(initOfMonth, endOfMonth)
				.and(Fields.CHARGEABLE_TYPE).eq(ACCOUNT.name())
				.and(Fields.CHARGEABLE_UUID).eq(account.getUuid())
				.and(Fields.CHARGE_NEXT_MONTH).eq(false)
				.orderBy(Fields.DATE)));

		if(card != null) {
			expenses.addAll(repository.query(table, queryBetweenUserDataAndNotRemoved(initOfMonth, endOfMonth)
					.and(Fields.CHARGEABLE_TYPE).eq(DEBIT_CARD.name())
					.and(Fields.CHARGEABLE_UUID).eq(card.getUuid())
					.and(Fields.CHARGE_NEXT_MONTH).eq(false)
					.orderBy(Fields.DATE)));
		}

		if(account.isAccountToPayCreditCard()) {
			DateTime creditCardMonth = month.minusMonths(1);
			List<Card> cards = getCardRepository().creditCards();
			for (Card creditCard : cards) {
				int total = getCardRepository().getInvoiceValue(card, creditCardMonth);
				if (total == 0)
					continue;

				Expense expense = new Expense();
				expense.setChargeable(card);
				expense.setName(MyApplication.getContext().getString(R.string.invoice) + " " + creditCard.getName());
				expense.setDate(creditCardMonth.plusMonths(1));
				expense.setValue(total);
				expense.setCreditCard(card);
				expenses.add(expense);
			}
		}

		Collections.sort(expenses, new Comparator<Expense>() {
			@Override
			public int compare(Expense lhs, Expense rhs) {
				if(lhs.getDate().isAfter(rhs.getDate()))
					return 1;
				return -1;
			}
		});

		return expenses;
	}

	@WorkerThread
	public long greaterUpdatedAt() {
		return repository.greaterUpdatedAt(table);
	}

	@WorkerThread
	public List<Expense> unsync() {
		return repository.unsync(table);
	}

	private Where queryBetween(DateTime init, DateTime end) {
		return new Where(Fields.DATE).greaterThanOrEq(init.getMillis())
				.and(Fields.DATE).lessThanOrEq(end.getMillis());
	}

	private Where queryBetweenUserDataAndNotRemoved(DateTime init, DateTime end) {
		return queryBetween(init, end)
				.and(Fields.REMOVED).eq(false)
				.and(Fields.USER_UUID).eq(Environment.CURRENT_USER_UUID);
	}

	@WorkerThread
	public OperationResult save(Expense expense) {
		OperationResult result = new OperationResult();
		if(StringUtils.isEmpty(expense.getName()))
			result.addError(ValidationError.NAME);
		if(expense.getValue() <= 0)
			result.addError(ValidationError.AMOUNT);
		if(expense.getDate() == null)
			result.addError(ValidationError.DATE);
		if(expense.getChargeable() == null)
			result.addError(ValidationError.CHARGEABLE);
		if(result.isValid()) {
			if(expense.getId() == 0 && expense.getUuid() == null)
				expense.setUuid(UUID.randomUUID().toString());
			if(expense.getId() == 0 && expense.getUserUuid() == null)
				expense.setUserUuid(Environment.CURRENT_USER_UUID);
			expense.setSync(false);
			repository.saveAtDatabase(table, expense);
		}
		return result;
	}

	@UiThread
	void saveAsync(final Expense expense) {
		new AsyncTask<Void, Void, OperationResult>() {

			@Override
			protected OperationResult doInBackground(Void... voids) {
				return save(expense);
			}

			@Override
			protected void onPostExecute(OperationResult operationResult) {
				super.onPostExecute(operationResult);
				if(!operationResult.isValid())
					throw new UnsupportedOperationException("Could not save expense " + expense.getUuid());
			}
		}.execute();
	}

	@WorkerThread
	@Override
	public void syncAndSave(final Expense unsyncExpense) {
		Expense expense = find(unsyncExpense.getUuid());
		if(expense != null && expense.getId() != unsyncExpense.getId()) {
			if(expense.getUpdatedAt() != unsyncExpense.getUpdatedAt())
				warning("Expense overwritten", unsyncExpense.getData());
			unsyncExpense.setId(expense.getId());
		}

		unsyncExpense.setSync(true);
		repository.saveAtDatabase(table, unsyncExpense);
	}
}