package br.com.jonathanzanella.myexpenses.card;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;

public class Card implements Chargeable, UnsyncModel {
	private static final String LOG_TAG = "Card";
	private AccountRepository accountRepository;

	private long id;

	@Expose
	private String uuid;

	@Expose
	private String name;

	@Expose
	private CardType type;

	@Expose
	private String accountUuid;

	@Expose
	private String userUuid;

	@Expose @SerializedName("_id")
	private String serverId;

	@Expose @SerializedName("created_at")
	private long createdAt;

	@Expose @SerializedName("updated_at")
	private long updatedAt;

	private boolean sync;

	Card() {}

	public Card(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@WorkerThread
	public Account getAccount() {
		return getAccountRepository().find(accountUuid);
	}

	@WorkerThread
	private AccountRepository getAccountRepository() {
		if(accountRepository == null)
			accountRepository = new AccountRepository(new RepositoryImpl<Account>(MyApplication.getContext()));
		return accountRepository;
	}

	public void setAccount(Account account) {
		accountUuid = account.getUuid();
	}

	@Override
	public ChargeableType getChargeableType() {
		switch (type) {
			case CREDIT:
				return ChargeableType.CREDIT_CARD;
			case DEBIT:
				return ChargeableType.DEBIT_CARD;
		}

		Log.e(LOG_TAG, "new card type?");
		return ChargeableType.DEBIT_CARD;
	}

	@Override
	public boolean canBePaidNextMonth() {
		return type == CardType.CREDIT;
	}

	@WorkerThread
	@Override
	public void debit(final int value) {
		if(type == CardType.DEBIT) {
			Account account = getAccount();
			account.debit(value);
			getAccountRepository().save(account);
		}
	}

	@WorkerThread
	@Override
	public void credit(final int value) {
		if(type == CardType.DEBIT) {
			Account account = getAccount();
			account.credit(value);
			getAccountRepository().save(account);
		}
	}

	@Override
	public String getData() {
		return "name=" + name +
				"\nuuid=" + uuid +
				"\ntype=" + type +
				"\naccount=" + accountUuid;
	}

	public void setAccountRepository(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
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

	public CardType getType() {
		return type;
	}

	public void setType(CardType type) {
		this.type = type;
	}

	String getAccountUuid() {
		return accountUuid;
	}

	void setAccountUuid(String accountUuid) {
		this.accountUuid = accountUuid;
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
}
