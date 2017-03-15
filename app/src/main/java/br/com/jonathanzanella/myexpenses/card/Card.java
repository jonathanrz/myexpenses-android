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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class Card implements Chargeable, UnsyncModel {
	private static final String LOG_TAG = "Card";
	private AccountRepository accountRepository;

	@Setter @Getter
	private long id;

	@Getter @Setter @Expose
	private String uuid;

	@Getter @Setter @Expose
	private String name;

	@Getter @Setter @Expose
	private CardType type;

	@Getter @Setter @Expose
	private String accountUuid;

	@Getter @Setter @Expose
	private String userUuid;

	@Getter @Setter @Expose @SerializedName("_id")
	private String serverId;

	@Getter @Setter @Expose @SerializedName("created_at")
	private long createdAt;

	@Getter @Setter @Expose @SerializedName("updated_at")
	private long updatedAt;

	@Getter @Setter
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
		return (type == CardType.CREDIT);
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
}
