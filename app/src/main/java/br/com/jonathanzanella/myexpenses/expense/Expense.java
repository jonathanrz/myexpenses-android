package br.com.jonathanzanella.myexpenses.expense;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.helpers.DateHelper;
import br.com.jonathanzanella.myexpenses.helpers.converter.DateTimeConverter;
import br.com.jonathanzanella.myexpenses.overview.WeeklyPagerAdapter;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;
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

	@Column @Unique @NotNull
	@Getter @Setter @Expose
	String uuid;

	@Column @NotNull
	@Getter @Setter @Expose
	String name;

	@Column(typeConverter = DateTimeConverter.class) @Getter @Setter @Expose
	DateTime date;

	@Column @Getter @Setter @Expose
	int value;

	@Column @Getter @Setter @Expose
	int valueToShowInOverview;

	@Column @NotNull @Expose
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

	@Column @Getter @Setter @Expose
	boolean ignoreInResume;

	@Column @NotNull @Getter @Setter @Expose
	String userUuid;

	@Column @Unique
	@Getter @Setter @Expose @SerializedName("_id")
	String serverId;

	@Column @Getter @Setter @Expose @SerializedName("created_at")
	long createdAt;

	@Column @Getter @Setter @Expose @SerializedName("updated_at")
	long updatedAt;

	@Column
	boolean sync;

	@Getter
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

	public static List<Expense> all() {
		return initQuery().queryList();
	}

	public static List<Expense> monthly(DateTime date) {
		DateTime lastMonth = date.minusMonths(1);
		DateTime initOfMonth = DateHelper.firstDayOfMonth(lastMonth);
		DateTime endOfMonth = DateHelper.lastDayOfMonth(lastMonth);

		List<Expense> expenses = initQuery()
				.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeNextMonth.eq(true))
				.and(Expense_Table.userUuid.is(Environment.CURRENT_USER_UUID))
				.orderBy(Expense_Table.date, true)
				.queryList();

		initOfMonth = DateHelper.firstDayOfMonth(date);
		endOfMonth = DateHelper.lastDayOfMonth(date);

		expenses.addAll(initQuery()
				.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeNextMonth.eq(false))
				.and(Expense_Table.userUuid.is(Environment.CURRENT_USER_UUID))
				.orderBy(Expense_Table.date, true)
				.queryList());

		return expenses;
	}

	public static List<Expense> expenses(WeeklyPagerAdapter.Period period) {
		List<Expense> expenses = new ArrayList<>();

		if(period.init.getDayOfMonth() == 1) {
			DateTime date = DateHelper.firstDayOfMonth(period.init);
			DateTime initOfMonth = date.minusMonths(1);
			DateTime endOfMonth = DateHelper.lastDayOfMonth(initOfMonth);

			expenses.addAll(initQuery()
					.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
					.and(Expense_Table.chargeableType.eq(ChargeableType.CREDIT_CARD))
					.and(Expense_Table.chargeNextMonth.eq(true))
					.and(Expense_Table.ignoreInOverview.eq(false))
					.and(Expense_Table.userUuid.is(Environment.CURRENT_USER_UUID))
					.orderBy(Expense_Table.date, true)
					.queryList());
		}

		DateTime init = DateHelper.firstMillisOfDay(period.init);
		DateTime end = DateHelper.lastMillisOfDay(period.end);

		expenses.addAll(initQuery()
				.where(Expense_Table.date.between(init).and(end))
				.and(Expense_Table.chargeNextMonth.eq(false))
				.and(Expense_Table.ignoreInOverview.eq(false))
				.and(Expense_Table.userUuid.is(Environment.CURRENT_USER_UUID))
				.orderBy(Expense_Table.date, true)
				.queryList());

		return expenses;
	}

	public static List<Expense> expenses(DateTime date) {
		DateTime lastMonth = date.minusMonths(1);
		DateTime initOfMonth = DateHelper.firstDayOfMonth(lastMonth);
		DateTime endOfMonth = DateHelper.lastDayOfMonth(lastMonth);

		List<Expense> expenses = initQuery()
				.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeableType.notEq(ChargeableType.CREDIT_CARD))
				.and(Expense_Table.chargeNextMonth.eq(true))
				.and(Expense_Table.ignoreInResume.eq(false))
				.and(Expense_Table.userUuid.is(Environment.CURRENT_USER_UUID))
				.orderBy(Expense_Table.date, true)
				.queryList();

		initOfMonth = DateHelper.firstDayOfMonth(date);
		endOfMonth = DateHelper.lastDayOfMonth(date);

		expenses.addAll(initQuery()
				.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeableType.notEq(ChargeableType.CREDIT_CARD))
				.and(Expense_Table.chargeNextMonth.eq(false))
				.and(Expense_Table.ignoreInResume.eq(false))
				.and(Expense_Table.userUuid.is(Environment.CURRENT_USER_UUID))
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
			expenses.add(expense);
		}

		return expenses;
	}

	public static List<Expense> accountExpenses(Account account, DateTime month) {
		DateTime lastMonth = month.minusMonths(1);
		DateTime initOfMonth = DateHelper.firstDayOfMonth(lastMonth);
		DateTime endOfMonth = DateHelper.lastDayOfMonth(lastMonth);

		Card card = account.getDebitCard();

		List<Expense> expenses = initQuery()
				.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeableType.eq(ChargeableType.ACCOUNT))
				.and(Expense_Table.chargeableUuid.eq(account.getUuid()))
				.and(Expense_Table.chargeNextMonth.eq(true))
				.queryList();

		if(card != null) {
			expenses.addAll(initQuery()
					.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
					.and(Expense_Table.chargeableType.eq(ChargeableType.DEBIT_CARD))
					.and(Expense_Table.chargeableUuid.eq(card.getUuid()))
					.and(Expense_Table.chargeNextMonth.eq(true))
					.queryList());
		}

		initOfMonth = DateHelper.firstDayOfMonth(month);
		endOfMonth = DateHelper.lastDayOfMonth(month);

		expenses.addAll(initQuery()
				.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeableType.eq(ChargeableType.ACCOUNT))
				.and(Expense_Table.chargeableUuid.eq(account.getUuid()))
				.and(Expense_Table.chargeNextMonth.eq(false))
				.queryList());

		if(card != null) {
			expenses.addAll(initQuery()
					.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
					.and(Expense_Table.chargeableType.eq(ChargeableType.DEBIT_CARD))
					.and(Expense_Table.chargeableUuid.eq(card.getUuid()))
					.and(Expense_Table.chargeNextMonth.eq(false))
					.queryList());
		}

		if(account.isAccountToPayCreditCard()) {
			DateTime creditCardMonth = month.minusMonths(1);
			for (Card creditCard : Card.creditCards()) {
				int total = creditCard.getInvoiceValue(creditCardMonth);
				if (total == 0)
					continue;

				Expense expense = new Expense();
				expense.setChargeable(card);
				expense.setName(MyApplication.getContext().getString(R.string.invoice) + " " + creditCard.getName());
				expense.setDate(creditCardMonth.plusMonths(1));
				expense.setValue(total);
				expense.creditCard = card;
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
			case CREDIT_CARD:
				return Card.find(uuid);
		}
		return null;
	}

	public void repeat() {
		id = 0;
		uuid = null;
		date = date.plusMonths(1);
	}

	public boolean isShowInOverview() {
		return !ignoreInOverview;
	}

	public boolean isShowInResume() {
		return !ignoreInResume;
	}

	public void showInOverview(boolean b) {
		ignoreInOverview = !b;
	}

	public void showInResume(boolean b) {
		ignoreInResume = !b;
	}

	@Override
	public boolean isSaved() {
		return id != 0;
	}

	@Override
	public String getData() {
		return "uuid=" + uuid + "" +
				"\nname=" + name +
				"\ndate=" + sdf.format(date.toDate()) +
				"\nvalue=" + value;
	}

	public void debit() {
		Chargeable c = getChargeable();
		c.debit(getValue());
		c.save();
		setCharged(true);
		save();
	}

	public String getIncomeFormatted() {
		return NumberFormat.getCurrencyInstance().format(value / 100.0);
	}

	@Override
	public void save() {
		if(id == 0 && uuid == null) {
			do
				uuid = UUID.randomUUID().toString();
			while (Expense.find(uuid) != null);
		}
		sync = false;
		super.save();
	}

	@Override
	public void syncAndSave() {
		save();
		sync = true;
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
