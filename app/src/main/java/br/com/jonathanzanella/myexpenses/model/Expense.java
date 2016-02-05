package br.com.jonathanzanella.myexpenses.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;

import java.util.List;

import br.com.jonathanzanella.myexpenses.converter.DateTimeConverter;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jzanella on 2/2/16.
 */
@Table(database = MyDatabase.class)
public class Expense extends BaseModel {
	@Column
	@PrimaryKey(autoincrement = true) @Getter
	long id;

	@Column @Getter @Setter
	String name;

	@Column(typeConverter = DateTimeConverter.class) @Getter @Setter
	DateTime date;

	@Column
	int value;

	@Column
	int newValue;

	@Column
	long chargeableId;

	@Column
	ChargeableType chargeableType;

	@Column @Setter
	boolean charged;

	@Column @Setter
	boolean chargeNextMonth;

	public static List<Expense> all() {
		return initQuery().queryList();
	}

	public static List<Expense> uncharged() {
		return initQuery()
				.where(Expense_Table.charged.eq(false))
				.and(Expense_Table.date.lessThanOrEq(DateTime.now()))
				.queryList();
	}

	public static List<Expense> changed() {
		return initQuery()
				.where(Expense_Table.charged.eq(true))
				.and(Expense_Table.newValue.greaterThan(0))
				.queryList();
	}

	public static List<Expense> creditCardBills(Card creditCard, DateTime date) {
		DateTime initOfMonth = date.withDate(date.getYear(), date.getMonthOfYear() - 1, 1);
		DateTime endOfMonth = date.withDate(date.getYear(), date.getMonthOfYear(), 1);

		List<Expense> bills = initQuery()
									.where(Expense_Table.chargeableId.eq(creditCard.getId()))
									.and(Expense_Table.chargeableType.eq(ChargeableType.CARD))
									.and(Expense_Table.date.between(initOfMonth).and(endOfMonth))
									.and(Expense_Table.chargeNextMonth.eq(true))
									.queryList();

		initOfMonth = endOfMonth;
		endOfMonth = date.withDate(date.getYear(), date.getMonthOfYear() + 1, 1);

		bills.addAll(initQuery()
					.where(Expense_Table.chargeableId.eq(creditCard.getId()))
					.and(Expense_Table.chargeableType.eq(ChargeableType.CARD))
					.and(Expense_Table.date.between(initOfMonth).and(endOfMonth))
					.and(Expense_Table.chargeNextMonth.eq(false))
					.queryList());

		return bills;
	}

	public static List<Expense> monthly(DateTime month) {
		return initQuery()
				.where(Receipt_Table.date.between(month).and(month.plusMonths(1)))
				.queryList();
	}

	private static From<Expense> initQuery() {
		return SQLite.select().from(Expense.class);
	}

	public static Expense find(long id) {
		return initQuery().where(Expense_Table.id.eq(id)).querySingle();
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
		chargeableId = chargeable.getId();
	}

	public Chargeable getChargeable() {
		return Expense.findChargeable(chargeableType, chargeableId);
	}

	public static Chargeable findChargeable(ChargeableType type, long id) {
		switch (type) {
			case ACCOUNT:
				return Account.find(id);
			case CARD:
				return Card.find(id);
		}
		return null;
	}
}
