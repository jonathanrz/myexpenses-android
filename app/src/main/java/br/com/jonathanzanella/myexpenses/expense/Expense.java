package br.com.jonathanzanella.myexpenses.expense;

import android.support.annotation.WorkerThread;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.bill.BillRepository;
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.card.CardRepository;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;

public class Expense implements Transaction, UnsyncModel {
	private static AccountRepository accountRepository;
	private static CardRepository cardRepository;
	private static ExpenseRepository expenseRepository;

	private long id;

	@Expose
	private String uuid;

	@Expose
	private String name;

	@Expose
	private DateTime date;

	@Expose
	private int value;

	@Expose
	private int valueToShowInOverview;

	@Expose
	private String chargeableUuid;

	@Expose
	private ChargeableType chargeableType;

	@Expose
	private String billUuid;

	@Expose
	private boolean charged;

	@Expose
	private boolean chargedNextMonth;

	@Expose
	private boolean ignoreInOverview;

	@Expose
	private boolean ignoreInResume;

	@Expose @SerializedName("_id")
	private String serverId;

	@Expose @SerializedName("created_at")
	private long createdAt;

	@Expose @SerializedName("updated_at")
	private long updatedAt;

	private boolean sync;

	@Expose
	private boolean removed;

	private Card creditCard;

	private int repetition = 1;
	private int installments = 1;
	private Chargeable chargeable;

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
		this.chargeable = chargeable;
		chargeableType = chargeable.getChargeableType();
		chargeableUuid = chargeable.getUuid();
	}

	void setChargeable(String uuid, ChargeableType type) {
		chargeableType = type;
		chargeableUuid = uuid;
	}

	@WorkerThread
	public Chargeable getChargeable() {
		if(chargeable == null)
			chargeable = Expense.findChargeable(chargeableType, chargeableUuid);
		return chargeable;
	}

	void uncharge() {
		if(charged) {
			Chargeable c = getChargeable();
			c.credit(getValue());
			switch (c.getChargeableType()) {
				case ACCOUNT:
					getAccountRepository().save((Account) c);
				case CREDIT_CARD:
				case DEBIT_CARD:
					if(c instanceof Card)
						getCardRepository().save((Card) c);
					else
						throw new UnsupportedOperationException("Chargeable should be a card");
			}
			charged = false;
		}
	}

	public void setBill(Bill bill) {
		billUuid = bill != null ? bill.getUuid() : null;
	}

	@WorkerThread
	public Bill getBill() {
		return new BillRepository(new RepositoryImpl<Bill>(MyApplication.getContext()), expenseRepository).find(billUuid);
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

	Expense repeat(String originalName, int index) {
		Expense expense = new Expense();
		if(installments > 1)
			expense.name = formatExpenseName(originalName, index);
		else
			expense.name = originalName;
		expense.date = date.plusMonths(1);
		expense.value = value;
		expense.valueToShowInOverview = valueToShowInOverview;
		expense.chargeableUuid = chargeableUuid;
		expense.chargeableType = chargeableType;
		expense.billUuid = billUuid;
		expense.chargedNextMonth = chargedNextMonth;
		expense.ignoreInOverview = ignoreInOverview;
		expense.ignoreInResume = ignoreInResume;
		expense.creditCard = creditCard;
		expense.repetition = repetition;
		expense.installments = installments;
		expense.chargeable = chargeable;
		return expense;
	}

	int getRepetition() {
		return Math.max(repetition, installments);
	}

	String formatExpenseName(String originalName, int i) {
		return String.format(Environment.PTBR_LOCALE, "%s %02d/%02d", originalName, i, installments);
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
				"\nserverId=" + serverId +
				"\ndate=" + SIMPLE_DATE_FORMAT.format(date.toDate()) +
				"\nvalue=" + value +
				"\nremoved=" + removed;
	}

	public void debit() {
		Chargeable c = getChargeable();
		c.debit(getValue());
		switch (c.getChargeableType()) {
			case ACCOUNT:
				getAccountRepository().save((Account) c);
				break;
			case DEBIT_CARD:
			case CREDIT_CARD:
				getCardRepository().save((Card) c);
				break;
		}
		setCharged(true);
		getExpenseRepository().save(this);

	}

	public String getIncomeFormatted() {
		return CurrencyHelper.format(value);
	}

	void delete() {
		removed = true;
		sync = false;
		getExpenseRepository().save(this);
	}

	private static AccountRepository getAccountRepository() {
		if(accountRepository == null)
			accountRepository = new AccountRepository(new RepositoryImpl<Account>(MyApplication.getContext()));
		return accountRepository;
	}

	private static CardRepository getCardRepository() {
		if(cardRepository == null)
			cardRepository = new CardRepository(new RepositoryImpl<Card>(MyApplication.getContext()), expenseRepository);
		return cardRepository;
	}

	private static ExpenseRepository getExpenseRepository() {
		if(expenseRepository == null)
			expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(MyApplication.getContext()));
		return expenseRepository;
	}

	public static void setAccountRepository(AccountRepository accountRepository) {
		Expense.accountRepository = accountRepository;
	}

	public static void setExpenseRepository(ExpenseRepository expenseRepository) {
		Expense.expenseRepository = expenseRepository;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getValueToShowInOverview() {
		return valueToShowInOverview;
	}

	void setValueToShowInOverview(int valueToShowInOverview) {
		this.valueToShowInOverview = valueToShowInOverview;
	}

	public String getBillUuid() {
		return billUuid;
	}

	void setBillUuid(String billUuid) {
		this.billUuid = billUuid;
	}

	public boolean isCharged() {
		return charged;
	}

	public void setCharged(boolean charged) {
		this.charged = charged;
	}

	boolean isChargedNextMonth() {
		return chargedNextMonth;
	}

	void setChargedNextMonth(boolean chargedNextMonth) {
		this.chargedNextMonth = chargedNextMonth;
	}

	boolean isIgnoreInOverview() {
		return ignoreInOverview;
	}

	void setIgnoreInOverview(boolean ignoreInOverview) {
		this.ignoreInOverview = ignoreInOverview;
	}

	boolean isIgnoreInResume() {
		return ignoreInResume;
	}

	void setIgnoreInResume(boolean ignoreInResume) {
		this.ignoreInResume = ignoreInResume;
	}

	@Override
	public String getServerId() {
		return serverId;
	}

	@Override
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	@Override
	public long getCreatedAt() {
		return createdAt;
	}

	@Override
	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public long getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public void setUpdatedAt(long updatedAt) {
		this.updatedAt = updatedAt;
	}

	public boolean isSync() {
		return sync;
	}

	@Override
	public void setSync(boolean sync) {
		this.sync = sync;
	}

	boolean isRemoved() {
		return removed;
	}

	void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public Card getCreditCard() {
		return creditCard;
	}

	void setCreditCard(Card creditCard) {
		this.creditCard = creditCard;
	}

	public void setRepetition(int repetition) {
		this.repetition = repetition;
	}

	public int getInstallments() {
		return installments;
	}

	public void setInstallments(int installments) {
		this.installments = installments;
	}
}
