package br.com.jonathanzanella.myexpenses.bill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.helpers.DateHelper;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;

public class Bill implements Transaction, UnsyncModel {
	private long id;

	@Expose
	private String uuid;

	@Expose
	private String name;

	@Expose
	private int amount;

	@Expose
	private int dueDate;

	@Expose
	private DateTime initDate;

	@Expose
	private DateTime endDate;

	@Expose
	private String userUuid;

	@Expose @SerializedName("_id")
	private String serverId;

	@Expose @SerializedName("created_at")
	private long createdAt;

	@Expose @SerializedName("updated_at")
	private long updatedAt;

	private boolean sync;

	private DateTime month;

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
	public String getData() {
		return "name=" + name +
				"\nuuid=" + uuid +
				"\namount=" + amount +
				"\ndueDate=" + dueDate +
				"\ninitDate=" + SIMPLE_DATE_FORMAT.format(initDate.toDate()) +
				"\nendDate="+ SIMPLE_DATE_FORMAT.format(endDate.toDate());
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	int getDueDate() {
		return dueDate;
	}

	public void setDueDate(int dueDate) {
		this.dueDate = dueDate;
	}

	public DateTime getInitDate() {
		return initDate;
	}

	public DateTime getEndDate() {
		return endDate;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	@Override
	public String getServerId() {
		return serverId;
	}

	@Override
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	@Override
	public long getCreatedAt() {
		return createdAt;
	}

	@Override
	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public long getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public void setUpdatedAt(long updatedAt) {
		this.updatedAt = updatedAt;
	}

	public boolean isSync() {
		return sync;
	}

	@Override
	public void setSync(boolean sync) {
		this.sync = sync;
	}

	public DateTime getMonth() {
		return month;
	}

	public void setMonth(DateTime month) {
		this.month = month;
	}
}