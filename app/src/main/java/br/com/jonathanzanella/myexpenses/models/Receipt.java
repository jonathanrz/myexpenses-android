package br.com.jonathanzanella.myexpenses.models;

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
public class Receipt extends BaseModel implements Transaction{
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

	@Column
	@PrimaryKey(autoincrement = true)
	long id;

	@Column @Getter @Setter
	String uuid;

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

	@Column @Getter @Setter
	String sourceUuid;

	@Column @Getter @Setter
	String accountUuid;

	@Column @Getter @Setter
	boolean credited;

	@Column @Getter @Setter
	boolean ignoreInResume;

	@Override
	public int getAmount() {
		return getIncome();
	}

	@Override
	public boolean credited() {
		return credited;
	}

	@Override
	public boolean debited() {
		return true;
	}

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

	public static List<Receipt> monthly(DateTime month, Account account) {
		return initQuery()
				.where(Receipt_Table.date.between(month).and(month.plusMonths(1)))
				.and(Receipt_Table.accountUuid.eq(account.getUuid()))
				.queryList();
	}

	public static List<Receipt> resume(DateTime month) {
		return initQuery()
				.where(Receipt_Table.date.between(month).and(month.plusMonths(1)))
				.and(Receipt_Table.ignoreInResume.is(false))
				.queryList();
	}

	private static From<Receipt> initQuery() {
		return SQLite.select().from(Receipt.class);
	}

	public static Receipt find(String uuid) {
		return initQuery().where(Receipt_Table.uuid.eq(uuid)).querySingle();
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
		sourceUuid= s.getUuid();
	}

	public Account getAccount() {
		return Account.find(accountUuid);
	}

	public void setAccount(@NonNull Account a) {
		accountUuid = a.getUuid();
	}

	public boolean isShowInResume() {
		return !ignoreInResume;
	}

	public void setShowInResume(boolean b) {
		ignoreInResume = !b;
	}

	public void repeat() {
		id = 0;
		date = date.plusMonths(1);
	}
}
