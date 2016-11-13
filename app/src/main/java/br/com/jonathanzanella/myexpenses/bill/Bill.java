package br.com.jonathanzanella.myexpenses.bill;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.NotNull;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.helpers.DateHelper;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Jonathan Zanella on 07/02/16.
 */
@EqualsAndHashCode(callSuper = false, of = {"id", "uuid", "name"})
public class Bill implements Transaction, UnsyncModel {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
	private static final BillApi billApi = new BillApi();

	@Setter @Getter
	long id;

	@NotNull @Getter @Setter @Expose
	String uuid;

	@NotNull @Getter @Setter @Expose
	String name;

	@Getter @Setter @Expose
	int amount;

	@Getter @Setter @Expose
	int dueDate;

	@Getter @Expose
	DateTime initDate;

	@Getter @Expose
	DateTime endDate;

	@NotNull @Getter @Setter @Expose
	String userUuid;

	@Getter @Setter @Expose @SerializedName("_id")
	String serverId;

	@Getter @Setter @Expose @SerializedName("created_at")
	long createdAt;

	@Getter @Setter @Expose @SerializedName("updated_at")
	long updatedAt;

	@Getter @Setter
	boolean sync;

	DateTime month;

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
	public void syncAndSave(UnsyncModel unsyncModel) {
		throw new UnsupportedOperationException("You should use BillRepository");
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