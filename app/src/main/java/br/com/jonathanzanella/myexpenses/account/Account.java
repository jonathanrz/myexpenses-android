package br.com.jonathanzanella.myexpenses.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;

public class Account implements Chargeable, UnsyncModel {
	private long id;

	@Expose
	private String uuid;

	@Expose
	private String name;

	@Expose
	private int balance;

	@Expose
	private boolean accountToPayCreditCard;

	@Expose
	private boolean accountToPayBills;

	@Expose @SerializedName("_id")
	private String serverId;

	@Expose @SerializedName("created_at")
	private long createdAt;

	@Expose @SerializedName("updated_at")
	private long updatedAt;

	private boolean sync;

	public void credit(int value) {
		balance += value;
	}

	@Override
	public ChargeableType getChargeableType() {
		return ChargeableType.ACCOUNT;
	}

	@Override
	public boolean canBePaidNextMonth() {
		return false;
	}

	@Override
	public void debit(int value) {
		balance -= value;
	}

	@Override
	public String getData() {
		return "name=" + name +
				"\nuuid=" + uuid +
				"\nserverId=" + serverId +
				"\nbalance=" + balance +
				"\naccountToPayCreditCard=" + accountToPayCreditCard;
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

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public boolean isAccountToPayCreditCard() {
		return accountToPayCreditCard;
	}

	public void setAccountToPayCreditCard(boolean accountToPayCreditCard) {
		this.accountToPayCreditCard = accountToPayCreditCard;
	}

	public boolean isAccountToPayBills() {
		return accountToPayBills;
	}

	public void setAccountToPayBills(boolean accountToPayBills) {
		this.accountToPayBills = accountToPayBills;
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
}