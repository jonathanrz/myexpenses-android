package br.com.jonathanzanella.myexpenses.models;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.adapters.WeeklyPagerAdapter;
import br.com.jonathanzanella.myexpenses.converter.DateTimeConverter;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.server.ExpenseApi;
import br.com.jonathanzanella.myexpenses.server.UnsyncModelApi;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jzanella on 2/2/16.
 */
@Table(database = MyDatabase.class)
public class Expense extends BaseModel implements Transaction, UnsyncModel {
	private static final ExpenseApi expenseApi = new ExpenseApi();

	@Column
	@PrimaryKey(autoincrement = true)
	long id;

	@Column @Getter @Setter @Expose
	String uuid;

	@Column @Getter @Setter @Expose
	String name;

	@Column(typeConverter = DateTimeConverter.class) @Getter @Setter @Expose
	DateTime date;

	@Column @Expose
	int value;

	@Column @Expose
	int newValue;

	@Column @Expose
	String chargeableUuid;

	@Column @Expose
	ChargeableType chargeableType;

	@Column @Expose
	String billUuid;

	@Column @Getter @Setter @Expose
	boolean charged;

	@Column @Getter @Setter @Expose
	boolean chargeNextMonth;

	@Column @Getter @Setter @Expose
	boolean ignoreInOverview;

	@Getter
	private Card creditCard;

	@Column @Getter @Setter @Expose @SerializedName("_id")
	String serverId;

	@Column @Getter @Setter @Expose @SerializedName("created_at")
	long createdAt;

	@Column @Getter @Setter @Expose @SerializedName("updated_at")
	long updatedAt;

	@Column @Getter @Setter
	boolean sync;

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

	public static List<Expense> all() {
		return initQuery().queryList();
	}

	public static List<Expense> uncharged() {
		return initQuery()
				.where(Expense_Table.charged.eq(false))
				.and(Expense_Table.date.lessThanOrEq(DateTime.now()))
				.and(Expense_Table.chargeableType.notEq(ChargeableType.CARD))
				.queryList();
	}

	public static List<Expense> changed() {
		return initQuery()
				.where(Expense_Table.charged.eq(true))
				.and(Expense_Table.newValue.greaterThan(0))
				.queryList();
	}

	public static List<Expense> monthly(DateTime date) {
		date = date.withDayOfMonth(1).withMillisOfDay(0);
		DateTime initOfMonth = date.minusMonths(1);
		DateTime endOfMonth = date;

		List<Expense> bills = initQuery()
				.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeNextMonth.eq(true))
				.orderBy(Expense_Table.date, true)
				.queryList();

		initOfMonth = endOfMonth;
		endOfMonth = date.plusMonths(1);

		bills.addAll(initQuery()
				.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeNextMonth.eq(false))
				.orderBy(Expense_Table.date, true)
				.queryList());

		return bills;
	}

	public static List<Expense> expenses(WeeklyPagerAdapter.Period period) {
		List<Expense> bills = new ArrayList<>();

		if(period.init.getDayOfMonth() == 1) {
			DateTime date = period.init.withDayOfMonth(1).withMillisOfDay(0);
			DateTime initOfMonth = date.minusMonths(1);
			DateTime endOfMonth = initOfMonth.dayOfMonth().withMaximumValue();

			bills.addAll(initQuery()
					.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
					.and(Expense_Table.chargeableType.eq(ChargeableType.CARD))
					.and(Expense_Table.chargeNextMonth.eq(true))
					.and(Expense_Table.ignoreInOverview.eq(false))
					.orderBy(Expense_Table.date, true)
					.queryList());
		}

		bills.addAll(initQuery()
				.where(Expense_Table.date.between(period.init).and(period.end))
				.and(Expense_Table.chargeNextMonth.eq(false))
				.and(Expense_Table.ignoreInOverview.eq(false))
				.orderBy(Expense_Table.date, true)
				.queryList());

		return bills;
	}

	public static List<Expense> expenses(DateTime date) {
		date = date.withDayOfMonth(1).withMillisOfDay(0);
		DateTime initOfMonth = date.minusMonths(1);
		DateTime endOfMonth = date;

		List<Expense> bills = initQuery()
				.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeableType.notEq(ChargeableType.CARD))
				.and(Expense_Table.chargeNextMonth.eq(true))
				.and(Expense_Table.ignoreInOverview.notEq(true))
				.orderBy(Expense_Table.date, true)
				.queryList();

		initOfMonth = endOfMonth;
		endOfMonth = date.plusMonths(1);

		bills.addAll(initQuery()
				.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeableType.notEq(ChargeableType.CARD))
				.and(Expense_Table.chargeNextMonth.eq(false))
				.orderBy(Expense_Table.date, true)
				.queryList());

		DateTime creditCardMonth = date.minusMonths(1);
		for (Card card : Card.creditCards()) {
			int total = card.getInvoiceValue(creditCardMonth);
			if(total == 0)
				continue;

			Expense expense = new Expense();
			expense.setChargeable(card);
			expense.setName(MyApplication.getContext().getString(R.string.invoice));
			expense.setDate(creditCardMonth);
			expense.setValue(total);
			expense.creditCard = card;
			bills.add(expense);
		}

		return bills;
	}

	public static List<Expense> accountExpenses(Account account, DateTime date) {
		date = date.withDayOfMonth(1).withMillisOfDay(0);
		DateTime initOfMonth = date.minusMonths(1);
		DateTime endOfMonth = date;

		Card card = account.getDebitCard();

		List<Expense> bills = initQuery()
				.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeableType.eq(ChargeableType.ACCOUNT))
				.and(Expense_Table.chargeableUuid.eq(account.getUuid()))
				.and(Expense_Table.chargeNextMonth.eq(true))
				.queryList();

		if(card != null) {
			bills.addAll(initQuery()
					.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
					.and(Expense_Table.chargeableType.eq(ChargeableType.DEBIT_CARD))
					.and(Expense_Table.chargeableUuid.eq(card.getUuid()))
					.and(Expense_Table.chargeNextMonth.eq(true))
					.queryList());
		}

		initOfMonth = endOfMonth;
		endOfMonth = date.plusMonths(1);

		bills.addAll(initQuery()
				.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeableType.eq(ChargeableType.ACCOUNT))
				.and(Expense_Table.chargeableUuid.eq(account.getUuid()))
				.and(Expense_Table.chargeNextMonth.eq(false))
				.queryList());

		if(card != null) {
			bills.addAll(initQuery()
					.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
					.and(Expense_Table.chargeableType.eq(ChargeableType.DEBIT_CARD))
					.and(Expense_Table.chargeableUuid.eq(card.getUuid()))
					.and(Expense_Table.chargeNextMonth.eq(false))
					.queryList());
		}

		if(account.isAccountToPayCreditCard()) {
			DateTime creditCardMonth = date.minusMonths(1);
			for (Card creditCard : Card.creditCards()) {
				int total = creditCard.getInvoiceValue(creditCardMonth);
				if (total == 0)
					continue;

				Expense expense = new Expense();
				expense.setChargeable(card);
				expense.setName(MyApplication.getContext().getString(R.string.invoice) + " " + creditCard.getName());
				expense.setDate(creditCardMonth);
				expense.setValue(total);
				expense.creditCard = card;
				bills.add(expense);
			}
		}

		Collections.sort(bills, new Comparator<Expense>() {
			@Override
			public int compare(Expense lhs, Expense rhs) {
				if(lhs.getDate().isAfter(rhs.getDate()))
					return 1;
				return -1;
			}
		});

		return bills;
	}

	private static From<Expense> initQuery() {
		return SQLite.select().from(Expense.class);
	}

	public static Expense find(String uuid) {
		return initQuery().where(Expense_Table.uuid.eq(uuid)).querySingle();
	}

	public static long greaterUpdatedAt() {
		Expense expense = initQuery().orderBy(Expense_Table.updatedAt, false).limit(1).querySingle();
		if(expense == null)
			return 0L;
		return expense.getUpdatedAt();
	}

	public static List<Expense> unsync() {
		return initQuery().where(Expense_Table.sync.eq(false)).queryList();
	}

	public int getValue() {
		if(newValue != 0)
			return newValue;
		return value;
	}

	public void setValue(int value) {
		if(charged && this.value != value)
			newValue = value;
		else
			this.value = value;
	}

	public void resetNewValue() {
		this.value = newValue;
		newValue = 0;
	}

	public int changedValue() {
		return newValue - value;
	}

	public void setChargeable(Chargeable chargeable) {
		chargeableType = chargeable.getChargeableType();
		chargeableUuid = chargeable.getUuid();
	}

	public Chargeable getChargeable() {
		return Expense.findChargeable(chargeableType, chargeableUuid);
	}

	public void uncharge() {
		if(charged) {
			Chargeable c = getChargeable();
			c.credit(getValue());
			c.save();
			charged = false;
		}
	}

	public void setBill(Bill bill) {
		billUuid = (bill != null ? bill.getUuid() : null);
	}

	public Bill getBill() {
		return Bill.find(billUuid);
	}

	public static Chargeable findChargeable(ChargeableType type, String uuid) {
		if(type == null || uuid == null)
			return null;

		switch (type) {
			case ACCOUNT:
				return Account.find(uuid);
			case DEBIT_CARD:
			case CARD:
				return Card.find(uuid);
		}
		return null;
	}

	public void repeat() {
		id = 0;
		date = date.plusMonths(1);
	}

	public boolean isShowInOverview() {
		return !ignoreInOverview;
	}

	public void showInOverview(boolean b) {
		ignoreInOverview = !b;
	}

	@Override
	public boolean isSaved() {
		return id != 0;
	}

	@Override
	public String getData() {
		return "uuid=" + uuid + "" +
				", name=" + name +
				", date=" + sdf.format(date.toDate()) +
				", value=" + value;
	}

	@Override
	public void save() {
		if(id == 0 && uuid == null)
			uuid = UUID.randomUUID().toString();
		super.save();
	}

	@Override
	public String getHeader(Context ctx) {
		return ctx.getString(R.string.expenses);
	}

	@SuppressWarnings("unchecked")
	@Override
	public UnsyncModelApi getServerApi() {
		return expenseApi;
	}
}
