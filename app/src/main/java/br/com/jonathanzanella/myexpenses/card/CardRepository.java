package br.com.jonathanzanella.myexpenses.card;

import android.support.annotation.WorkerThread;

import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.database.Where;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.Expense_Table;
import br.com.jonathanzanella.myexpenses.helpers.DateHelper;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static br.com.jonathanzanella.myexpenses.log.Log.warning;

/**
 * Created by jzanella on 8/27/16.
 */

public class CardRepository {
	private Repository<Card> repository;
	private CardTable table = new CardTable();

	public CardRepository(Repository<Card> repository) {
		this.repository = repository;
	}

	@WorkerThread
	public Card find(String uuid) {
		return repository.find(table, uuid);
	}

	@WorkerThread
	List<Card> userCards() {
		return repository.userData(table);
	}

	@WorkerThread
	public List<Card> creditCards() {
		return repository.query(table, new Where(Fields.TYPE).eq(CardType.CREDIT.getValue()));
	}

	@WorkerThread
	public Card accountDebitCard(Account account) {
		return repository.querySingle(table,
				new Where(Fields.ACCOUNT_UUID).eq(account.getUuid())
				.and(Fields.TYPE).eq(CardType.DEBIT.getValue()));
	}

	@WorkerThread
	public List<Card> unsync() {
		return repository.unsync(table);
	}

	@WorkerThread
	public long greaterUpdatedAt() {
		return repository.greaterUpdatedAt(table);
	}

	@WorkerThread
	public OperationResult save(Card card) {
		OperationResult result = new OperationResult();
		if(StringUtils.isEmpty(card.getName()))
			result.addError(ValidationError.NAME);
		if(card.getType() == null)
			result.addError(ValidationError.CARD_TYPE);
		if(card.getAccount() == null)
			result.addError(ValidationError.ACCOUNT);
		if(result.isValid()) {
			if(card.getId() == 0 && card.getUuid() == null)
				card.setUuid(UUID.randomUUID().toString());
			if(card.getId() == 0 && card.getUserUuid() == null)
				card.setUserUuid(Environment.CURRENT_USER_UUID);
			card.setSync(false);
			repository.saveAtDatabase(table, card);
		}
		return result;
	}

	@WorkerThread
	public void syncAndSave(final Card unsyncCard) {
		Card card = find(unsyncCard.getUuid());
		if(card != null && card.id != unsyncCard.getId()) {
			if(card.getUpdatedAt() != unsyncCard.getUpdatedAt())
				warning("Card overwritten", unsyncCard.getData());
			unsyncCard.setId(card.id);
		}

		unsyncCard.setSync(true);
		repository.saveAtDatabase(table, unsyncCard);
	}

	private static From<Expense> initExpenseQuery() {
		return SQLite.select().from(Expense.class);
	}

	public List<Expense> creditCardBills(Card card, DateTime month) {
		DateTime lastMonth = month.minusMonths(1);
		DateTime initOfMonth = DateHelper.firstDayOfMonth(lastMonth);
		DateTime endOfMonth = DateHelper.lastDayOfMonth(lastMonth);

		List<Expense> expenses = initExpenseQuery()
				.where(Expense_Table.chargeableUuid.eq(card.getUuid()))
				.and(Expense_Table.chargeableType.eq(ChargeableType.CREDIT_CARD))
				.and(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeNextMonth.eq(true))
				.and(Expense_Table.charged.eq(false))
				.and(Expense_Table.userUuid.is(Environment.CURRENT_USER_UUID))
				.orderBy(Expense_Table.date, true)
				.queryList();

		initOfMonth = DateHelper.firstDayOfMonth(month);
		endOfMonth = DateHelper.lastDayOfMonth(month);

		expenses.addAll(initExpenseQuery()
				.where(Expense_Table.chargeableUuid.eq(card.getUuid()))
				.and(Expense_Table.chargeableType.eq(ChargeableType.CREDIT_CARD))
				.and(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeNextMonth.eq(false))
				.and(Expense_Table.charged.eq(false))
				.and(Expense_Table.userUuid.is(Environment.CURRENT_USER_UUID))
				.orderBy(Expense_Table.date, true)
				.queryList());

		return expenses;
	}

	public int getInvoiceValue(Card card, DateTime month) {
		int total = 0;
		for (Expense expense : creditCardBills(card, month))
			total += expense.getValue();

		return total;
	}
}