package br.com.jonathanzanella.myexpenses.models;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;

import java.util.List;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jzanella on 1/31/16.
 */
@Table(database = MyDatabase.class)
public class Card extends BaseModel implements Chargeable {
	private static final String LOG_TAG = "Card";
	@Column
	@PrimaryKey(autoincrement = true) @Getter
	long id;

	@Column @Getter @Setter
	String name;

	@Column @Getter @Setter
	CardType type;

	@Column @Getter @Setter
	String accountUuid;

	public static List<Card> all() {
		return initQuery().queryList();
	}
	public static List<Card> creditCards() {
		return initQuery()
				.where(Card_Table.type.eq(CardType.CREDIT))
				.queryList();
	}

	private static From<Card> initQuery() {
		return SQLite.select().from(Card.class);
	}

	public static Card find(long id) {
		return initQuery().where(Card_Table.id.eq(id)).querySingle();
	}

	public static Card accountDebitCard(Account acc) {
		return initQuery()
				.where(Card_Table.accountUuid.eq(acc.getUuid()))
				.and(Card_Table.type.eq(CardType.DEBIT))
				.querySingle();
	}

	public Account getAccount() {
		return Account.find(accountUuid);
	}

	public void setAccount(Account account) {
		accountUuid = account.getUuid();
	}

	@Override
	public ChargeableType getChargeableType() {
		switch (type) {
			case CREDIT:
				return ChargeableType.CARD;
			case DEBIT:
				return ChargeableType.DEBIT_CARD;
		}

		Log.e(LOG_TAG, "new card type?");
		return ChargeableType.DEBIT_CARD;
	}

	@Override
	public boolean canBePaidNextMonth() {
		return (type == CardType.CREDIT);
	}

	@Override
	public void debit(int value) {
		if(type == CardType.DEBIT) {
			Account a = getAccount();
			a.debit(value);
			a.save();
		}
	}

	@Override
	public void credit(int value) {
		if(type == CardType.DEBIT) {
			Account a = getAccount();
			a.credit(value);
			a.save();
		}
	}

	public int getInvoiceValue(DateTime month) {
		int total = 0;
		for (Expense expense : creditCardBills(month))
			total += expense.getValue();

		return total;
	}

	private static From<Expense> initExpenseQuery() {
		return SQLite.select().from(Expense.class);
	}

	public List<Expense> creditCardBills(DateTime date) {
		date = date.withDayOfMonth(1).withMillisOfDay(0);
		DateTime initOfMonth = date.minusMonths(1);
		DateTime endOfMonth = date;

		List<Expense> bills = initExpenseQuery()
				.where(Expense_Table.chargeableId.eq(getId()))
				.and(Expense_Table.chargeableType.eq(ChargeableType.CARD))
				.and(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeNextMonth.eq(true))
				.and(Expense_Table.charged.eq(false))
				.orderBy(Expense_Table.date, true)
				.queryList();

		initOfMonth = endOfMonth;
		endOfMonth = date.plusMonths(1);

		bills.addAll(initExpenseQuery()
				.where(Expense_Table.chargeableId.eq(getId()))
				.and(Expense_Table.chargeableType.eq(ChargeableType.CARD))
				.and(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeNextMonth.eq(false))
				.and(Expense_Table.charged.eq(false))
				.orderBy(Expense_Table.date, true)
				.queryList());

		return bills;
	}
}
