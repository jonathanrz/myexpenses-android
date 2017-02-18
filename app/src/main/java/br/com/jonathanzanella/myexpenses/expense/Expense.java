package br.com.jonathanzanella.myexpenses.expense;

import android.support.annotation.WorkerThread;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.bill.BillRepository;
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.card.CardRepository;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = false, of = {"id", "uuid", "name"})
public class Expense implements Transaction, UnsyncModel {
	private static AccountRepository accountRepository;
	private static CardRepository cardRepository;
	private static ExpenseRepository expenseRepository;

	@Setter @Getter
	private long id;

	@Getter @Setter @Expose
	private String uuid;

	@Getter @Setter @Expose
	private String name;

	@Getter @Setter @Expose
	private DateTime date;

	@Getter @Setter @Expose
	private int value;

	@Getter @Setter @Expose
	private int valueToShowInOverview;

	@Expose @Setter
	private String chargeableUuid;

	@Expose
	private ChargeableType chargeableType;

	@Expose @Getter @Setter
	private String billUuid;

	@Getter @Setter @Expose
	private boolean charged;

	@Getter @Setter @Expose
	private boolean chargedNextMonth;

	@Getter @Setter @Expose
	private boolean ignoreInOverview;

	@Getter @Setter @Expose
	private boolean ignoreInResume;

	@Getter @Setter @Expose
	private String userUuid;

	@Getter @Setter @Expose @SerializedName("_id")
	private String serverId;

	@Getter @Setter @Expose @SerializedName("created_at")
	private long createdAt;

	@Getter @Setter @Expose @SerializedName("updated_at")
	private long updatedAt;

	@Getter @Setter
	private boolean sync;

	@Getter @Setter @Expose
	private boolean removed;

	@Getter @Setter
	private Card creditCard;

	@Override
	public int getAmount() {
		return getValue();
	}

	@Override
	public boolean credited() {
		return true;
	}

	@Override
	public boolean debited() {
		return charged;
	}

	public void setChargeable(Chargeable chargeable) {
		chargeableType = chargeable.getChargeableType();
		chargeableUuid = chargeable.getUuid();
	}

	void setChargeable(String uuid, ChargeableType type) {
		chargeableType = type;
		chargeableUuid = uuid;
	}

	@WorkerThread
	public Chargeable getChargeable() {
		return Expense.findChargeable(chargeableType, chargeableUuid);
	}

	void uncharge() {
		if(charged) {
			Chargeable chargeable = getChargeable();
			chargeable.credit(getValue());
			switch (chargeable.getChargeableType()) {
				case ACCOUNT:
					getAccountRepository().save((Account) chargeable);
				case CREDIT_CARD:
				case DEBIT_CARD:
					if(chargeable instanceof Card)
						getCardRepository().save((Card) chargeable);
					else
						throw new UnsupportedOperationException("Chargeable should be a card");
			}
			charged = false;
		}
	}

	public void setBill(Bill bill) {
		billUuid = (bill != null ? bill.getUuid() : null);
	}

	@WorkerThread
	public Bill getBill() {
		return new BillRepository(new Repository<Bill>(MyApplication.getContext()), expenseRepository).find(billUuid);
	}

	static Chargeable findChargeable(ChargeableType type, final String uuid) {
		if(type == null || uuid == null)
			return null;

		switch (type) {
			case ACCOUNT:
				return getAccountRepository().find(uuid);
			case DEBIT_CARD:
			case CREDIT_CARD:
				return getCardRepository().find(uuid);
		}
		return null;
	}

	void repeat() {
		id = 0;
		uuid = null;
		date = date.plusMonths(1);
	}

	boolean isShowInOverview() {
		return !ignoreInOverview;
	}

	boolean isShowInResume() {
		return !ignoreInResume;
	}

	void showInOverview(boolean b) {
		ignoreInOverview = !b;
	}

	void showInResume(boolean b) {
		ignoreInResume = !b;
	}

	@Override
	public String getData() {
		return "name=" + name + "" +
				"\nuuid=" + uuid +
				"\ndate=" + sdf.format(date.toDate()) +
				"\nvalue=" + value +
				"\nremoved=" + removed;
	}

	public void debit() {
		Chargeable chargeable = getChargeable();
		chargeable.debit(getValue());
		switch (chargeable.getChargeableType()) {
			case ACCOUNT:
				getAccountRepository().save((Account) chargeable);
				break;
			case DEBIT_CARD:
			case CREDIT_CARD:
				getCardRepository().save((Card) chargeable);
				break;
		}
		setCharged(true);
		getExpenseRepository().save(this);

	}

	public String getIncomeFormatted() {
		return NumberFormat.getCurrencyInstance().format(value / 100.0);
	}

	void delete() {
		removed = true;
		sync = false;
		getExpenseRepository().save(this);
	}

	private static AccountRepository getAccountRepository() {
		if(accountRepository == null)
			accountRepository = new AccountRepository(new Repository<Account>(MyApplication.getContext()));
		return accountRepository;
	}

	private static CardRepository getCardRepository() {
		if(cardRepository == null)
			cardRepository = new CardRepository(new Repository<Card>(MyApplication.getContext()), expenseRepository);
		return cardRepository;
	}

	private static ExpenseRepository getExpenseRepository() {
		if(expenseRepository == null)
			expenseRepository = new ExpenseRepository(new Repository<Expense>(MyApplication.getContext()));
		return expenseRepository;
	}
}
