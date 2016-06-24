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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.converter.DateTimeConverter;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.server.BillApi;
import br.com.jonathanzanella.myexpenses.server.UnsyncModelApi;
import lombok.Getter;
import lombok.Setter;

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

	@Column @Getter @Setter @Expose
	String uuid;

	@Column @Getter @Setter @Expose
	String name;

	@Column @Getter @Setter @Expose
	int amount;

	@Column @Getter @Setter @Expose
	int dueDate;

	@Column(typeConverter = DateTimeConverter.class) @Getter @Expose
	DateTime initDate;

	@Column(typeConverter = DateTimeConverter.class) @Getter @Expose
	DateTime endDate;

	@Column @Getter @Setter @Expose @SerializedName("_id")
	String serverId;

	@Column @Getter @Setter @Expose @SerializedName("created_at")
	long createdAt;

	@Column @Getter @Setter @Expose @SerializedName("updated_at")
	long updatedAt;

	@Column @Getter @Setter
	boolean sync;

	public static List<Bill> all() {
		return initQuery().queryList();
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

	public static List<Bill> monthly(DateTime month, List<Expense> expenses) {
		List<Bill> bills = initQuery()
				.where(Bill_Table.initDate.lessThanOrEq(month))
				.and(Bill_Table.endDate.greaterThanOrEq(month))
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

		return  bills;
	}

	public void setInitDate(DateTime initDate) {
		this.initDate = initDate.withMillisOfDay(0);
	}

	public void setEndDate(DateTime endDate) {
		this.endDate = endDate.withMillisOfDay(0);
	}

	@Override
	public DateTime getDate() {
		return DateTime.now().withDayOfMonth(dueDate);
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
				", amount=" + amount +
				", dueDate=" + dueDate +
				", initDate=" + sdf.format(initDate.toDate()) +
				", endDate="+ sdf.format(endDate.toDate());
	}

	@Override
	public void save() {
		if(id == 0 && uuid == null)
			uuid = UUID.randomUUID().toString();
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