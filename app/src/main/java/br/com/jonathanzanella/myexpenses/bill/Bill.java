package br.com.jonathanzanella.myexpenses.bill;

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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.helpers.DateHelper;
import br.com.jonathanzanella.myexpenses.helpers.converter.DateTimeConverter;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;
import lombok.Getter;
import lombok.Setter;

import static br.com.jonathanzanella.myexpenses.log.Log.warning;

/**
 * Created by Jonathan Zanella on 07/02/16.
 */
@Table(database = MyDatabase.class)
public class Bill extends BaseModel implements Transaction, UnsyncModel {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
	private static final BillApi billApi = new BillApi();

	@Column
	@PrimaryKey(autoincrement = true)
	long id;

	@Column @Unique @NotNull
	@Getter @Setter @Expose
	String uuid;

	@Column @Unique @NotNull
	@Getter @Setter @Expose
	String name;

	@Column @Getter @Setter @Expose
	int amount;

	@Column @Getter @Setter @Expose
	int dueDate;

	@Column(typeConverter = DateTimeConverter.class) @Getter @Expose
	DateTime initDate;

	@Column(typeConverter = DateTimeConverter.class) @Getter @Expose
	DateTime endDate;

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

	private DateTime month;

	public static List<Bill> all() {
		return initQuery().queryList();
	}

	public static List<Bill> user() {
		return initQuery().where(Bill_Table.userUuid.is(Environment.CURRENT_USER_UUID)).queryList();
	}

	private static From<Bill> initQuery() {
		return SQLite.select().from(Bill.class);
	}

	public static Bill find(String uuid) {
		return initQuery().where(Bill_Table.uuid.eq(uuid)).querySingle();
	}

	public static long greaterUpdatedAt() {
		Bill bill = initQuery().orderBy(Bill_Table.updatedAt, false).limit(1).querySingle();
		if(bill == null)
			return 0L;
		return bill.getUpdatedAt();
	}

	public static List<Bill> unsync() {
		return initQuery().where(Bill_Table.sync.eq(false)).queryList();
	}

	public static List<Bill> monthly(DateTime month) {
		List<Expense> expenses = Expense.monthly(month);
		List<Bill> bills = initQuery()
				.where(Bill_Table.initDate.lessThanOrEq(month))
				.and(Bill_Table.endDate.greaterThanOrEq(month))
				.and(Bill_Table.userUuid.is(Environment.CURRENT_USER_UUID))
				.queryList();

		for (int i = 0; i < bills.size(); i++) {
			Bill bill = bills.get(i);
			boolean billAlreadyPaid = false;
			for (Expense expense : expenses) {
				Bill b = expense.getBill();
				if(b != null && b.getUuid().equals(bill.getUuid())) {
					billAlreadyPaid = true;
					break;
				}
			}
			if(billAlreadyPaid) {
				bills.remove(i);
				i--;
			}
		}

		for (Bill bill : bills)
			bill.month = month;

		return bills;
	}

	public void setInitDate(DateTime initDate) {
		if(initDate != null)
			this.initDate = initDate.withMillisOfDay(0);
		else
			this.initDate = null;
	}

	public void setEndDate(DateTime endDate) {
		if(endDate != null)
			this.endDate = endDate.withMillisOfDay(0);
		else
			this.endDate = null;
	}

	@Override
	public DateTime getDate() {
		if(month == null)
			month = DateTime.now();
		int lastDayOfMonth = DateHelper.lastDayOfMonth(month).getDayOfMonth();
		if(dueDate > lastDayOfMonth)
			return month.withDayOfMonth(lastDayOfMonth);
		return month.withDayOfMonth(dueDate);
	}

	@Override
	public boolean credited() {
		return true;
	}

	@Override
	public boolean debited() {
		return false;
	}

	@Override
	public boolean isSaved() {
		return id != 0;
	}

	@Override
	public String getData() {
		return "name=" + name +
				"\nuuid=" + uuid +
				"\namount=" + amount +
				"\ndueDate=" + dueDate +
				"\ninitDate=" + sdf.format(initDate.toDate()) +
				"\nendDate="+ sdf.format(endDate.toDate());
	}

	@Override
	public void save() {
		if(id == 0 && uuid == null)
			uuid = UUID.randomUUID().toString();
		if(id == 0 && userUuid == null)
			userUuid = Environment.CURRENT_USER_UUID;
		sync = false;
		super.save();
	}

	@Override
	public void syncAndSave(UnsyncModel unsyncModel) {
		Bill bill = Bill.find(uuid);

		if(bill != null && bill.id != id) {
			if(bill.getUpdatedAt() != getUpdatedAt())
				warning("Bill overwritten", getData());
			id = bill.id;
		}

		setServerId(unsyncModel.getServerId());
		setCreatedAt(unsyncModel.getCreatedAt());
		setUpdatedAt(unsyncModel.getUpdatedAt());

		save();
		sync = true;
		super.save();
	}

	@Override
	public String getHeader(Context ctx) {
		return ctx.getString(R.string.bill);
	}

	@SuppressWarnings("unchecked")
	@Override
	public UnsyncModelApi getServerApi() {
		return billApi;
	}
}