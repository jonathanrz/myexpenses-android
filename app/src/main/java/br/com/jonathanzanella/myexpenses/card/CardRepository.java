package br.com.jonathanzanella.myexpenses.card;

import android.support.annotation.WorkerThread;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.ModelRepository;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.database.Where;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static br.com.jonathanzanella.myexpenses.log.Log.warning;

public class CardRepository implements ModelRepository<Card> {
	private final Repository<Card> repository;
	private final ExpenseRepository expenseRepository;
	private final CardTable table = new CardTable();

	public CardRepository(Repository<Card> repository, ExpenseRepository expenseRepository) {
		this.repository = repository;
		this.expenseRepository = expenseRepository;
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
	@Override
	public void syncAndSave(final Card unsyncCard) {
		Card card = find(unsyncCard.getUuid());
		if(card != null && card.getId() != unsyncCard.getId()) {
			if(card.getUpdatedAt() != unsyncCard.getUpdatedAt())
				warning("Card overwritten", unsyncCard.getData());
			unsyncCard.setId(card.getId());
		}

		unsyncCard.setSync(true);
		repository.saveAtDatabase(table, unsyncCard);
	}

	@WorkerThread
	public List<Expense> creditCardBills(Card card, DateTime month) {
		return expenseRepository.alreadyPaidCardExpenses(month, card);
	}

	@WorkerThread
	public int getInvoiceValue(Card card, DateTime month) {
		int total = 0;
		for (Expense expense : creditCardBills(card, month))
			total += expense.getValue();

		return total;
	}
}