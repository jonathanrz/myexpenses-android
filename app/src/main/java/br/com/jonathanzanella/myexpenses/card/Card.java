package br.com.jonathanzanella.myexpenses.card;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jzanella on 1/31/16.
 */
@EqualsAndHashCode(callSuper = false)
public class Card implements Chargeable, UnsyncModel {
	private AccountRepository accountRepository;
	private static final String LOG_TAG = "Card";
	private static final CardApi cardApi = new CardApi();

	@Setter @Getter
	long id;

	@NotNull @Getter @Setter @Expose
	String uuid;

	@NotNull @Getter @Setter @Expose
	String name;

	@Column @Getter @Setter @Expose
	CardType type;

	@NotNull @Getter @Setter @Expose
	String accountUuid;

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

	Card() {
	}

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
			accountRepository = new AccountRepository(new Repository<Account>(MyApplication.getContext()));
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
	public boolean isSaved() {
		return id != 0;
	}

	@Override
	public String getData() {
		return "name=" + name +
				"\nuuid=" + uuid +
				"\ntype=" + type +
				"\naccount=" + accountUuid;
	}

	@Override
	public void syncAndSave(UnsyncModel serverModel) {
		throw new UnsupportedOperationException("You should use CardRepository");
	}

	@Override
	public String getHeader(Context ctx) {
		return ctx.getString(R.string.cards);
	}

	@SuppressWarnings("unchecked")
	@Override
	public UnsyncModelApi getServerApi() {
		return cardApi;
	}
}
