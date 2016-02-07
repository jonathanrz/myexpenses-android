package br.com.jonathanzanella.myexpenses.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.converter.DateTimeConverter;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by jzanella on 2/1/16.
 */
@Table(database = MyDatabase.class)
public class Receipt extends BaseModel {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

	@Column
	@PrimaryKey(autoincrement = true) @Getter
	long id;

	@Column @Getter @Setter
	String name;

	@Column(typeConverter = DateTimeConverter.class) @Getter @Setter
	DateTime date;

	@Column
	int income;

	@Column @Getter @Setter
	int newIncome;

	@Column
	long sourceId;

	@Column
	long accountId;

	@Column @Setter
	boolean credited;

	public static List<Receipt> all() {
		return initQuery().queryList();
	}

	public static List<Receipt> uncredited() {
		return initQuery()
				.where(Receipt_Table.credited.eq(false))
				.and(Receipt_Table.date.lessThanOrEq(DateTime.now()))
				.queryList();
	}

	public static List<Receipt> changed() {
		return initQuery()
				.where(Receipt_Table.credited.eq(true))
				.and(Receipt_Table.newIncome.greaterThan(0))
				.queryList();
	}

	public static List<Receipt> monthly(DateTime month) {
		return initQuery()
				.where(Receipt_Table.date.between(month).and(month.plusMonths(1)))
				.queryList();
	}

	private static From<Receipt> initQuery() {
		return SQLite.select().from(Receipt.class);
	}

	public static Receipt find(long id) {
		return initQuery().where(Receipt_Table.id.eq(id)).querySingle();
	}

	public int getIncome() {
		if(newIncome != 0)
			return newIncome;
		return income;
	}

	public void setIncome(int income) {
		if(credited && this.income != income)
			newIncome = income;
		else
			this.income = income;
	}

	public void resetNewIncome() {
		this.income = newIncome;
		newIncome = 0;
	}

	public int changedValue() {
		return newIncome - income;
	}

	public Source getSource() {
		return Source.find(sourceId);
	}

	public void setSource(@NonNull Source s) {
		sourceId = s.getId();
	}

	public Account getAccount() {
		return Account.find(accountId);
	}

	public void setAccount(@NonNull Account a) {
		accountId = a.getId();
	}

	public void repeat() {
		id = 0;
		date = date.plusMonths(1);
	}
}
