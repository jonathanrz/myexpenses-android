package br.com.jonathanzanella.myexpenses.receipt;

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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.helpers.converter.DateTimeConverter;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by jzanella on 2/1/16.
 */
@Table(database = MyDatabase.class)
public class Receipt extends BaseModel implements Transaction, UnsyncModel {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
	private static final ReceiptApi receiptApi = new ReceiptApi();

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
	int income;

	@Column @Getter @Setter @Expose
	int newIncome;

	@Column @Getter @Setter @Expose
	String sourceUuid;

	@Column @Getter @Setter @Expose
	String accountUuid;

	@Column @Getter @Setter @Expose
	boolean credited;

	@Column @Getter @Setter @Expose
	boolean ignoreInResume;

	@Column @Getter @Setter @Expose @SerializedName("_id")
	String serverId;

	@Column @Getter @Setter @Expose @SerializedName("created_at")
	long createdAt;

	@Column @Getter @Setter @Expose @SerializedName("updated_at")
	long updatedAt;

	@Column
	boolean sync;

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

	public static long greaterUpdatedAt() {
		Receipt receipt = initQuery().orderBy(Receipt_Table.updatedAt, false).limit(1).querySingle();
		if(receipt == null)
			return 0L;
		return receipt.getUpdatedAt();
	}

	public static List<Receipt> unsync() {
		return initQuery().where(Receipt_Table.sync.eq(false)).queryList();
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
		return Source.find(sourceUuid);
	}

	public void setSource(@NonNull Source s) {
		sourceUuid = s.getUuid();
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

	@Override
	public boolean isSaved() {
		return id != 0;
	}

	@Override
	public String getData() {
		return "uuid=" + uuid +
				", name=" + name +
				", date=" + sdf.format(date.toDate()) +
				", income=" + income;
	}

	@Override
	public void save() {
		if(id == 0 && uuid == null)
			uuid = UUID.randomUUID().toString();
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
		return ctx.getString(R.string.receipts);
	}

	@SuppressWarnings("unchecked")
	@Override
	public UnsyncModelApi getServerApi() {
		return receiptApi;
	}
}
