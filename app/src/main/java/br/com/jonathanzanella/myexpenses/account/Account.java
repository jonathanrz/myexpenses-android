package br.com.jonathanzanella.myexpenses.account;

import android.content.Context;
import android.support.annotation.Nullable;

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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import lombok.Getter;
import lombok.Setter;

import static br.com.jonathanzanella.myexpenses.log.Log.warning;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
@Table(database = MyDatabase.class)
public class Account extends BaseModel implements Chargeable, UnsyncModel {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
	private static final AccountApi accountApi = new AccountApi();

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
	int balance;

	@Column @Getter @Setter @Expose
	boolean accountToPayCreditCard;

	@Column @Getter @Setter @Expose
	boolean accountToPayBills;

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

	private static From<Account> initQuery() {
		return SQLite.select().from(Account.class);
	}

	public static List<Account> all() {
		return initQuery().queryList();
	}

	public static Account find(String uuid) {
		return initQuery().where(Account_Table.uuid.eq(uuid)).querySingle();
	}

	public static List<Account> unsync() {
		return initQuery().where(Account_Table.sync.eq(false)).queryList();
	}

	public static long greaterUpdatedAt() {
		Account account = initQuery().orderBy(Account_Table.updatedAt, false).limit(1).querySingle();
		if(account == null)
			return 0L;
		return account.getUpdatedAt();
	}

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

	public @Nullable
	Card getDebitCard() {
		return Card.accountDebitCard(this);
	}

	@Override
	public boolean isSaved() {
		return id != 0;
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
	public void syncAndSave() {
		Account account = Account.find(uuid);
		if(account != null && account.id != id) {
			if(account.getUpdatedAt() != getUpdatedAt())
				warning("Account overwritten", getData());
			id = account.id;
		}
		save();
		sync = true;
		super.save();
	}

	@Override
	public String getData() {
		return "name=" + name +
				"\nuuid=" + uuid +
				"\nbalance=" + balance +
				"\naccountToPayCreditCard=" + accountToPayCreditCard +
				"\nuuid=" + uuid;
	}

	@Override
	public String getHeader(Context ctx) {
		return ctx.getString(R.string.account);
	}

	@SuppressWarnings("unchecked")
	@Override
	public UnsyncModelApi getServerApi() {
		return accountApi;
	}
}
