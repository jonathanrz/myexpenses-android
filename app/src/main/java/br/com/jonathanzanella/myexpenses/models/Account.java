package br.com.jonathanzanella.myexpenses.models;

import android.content.Context;
import android.support.annotation.Nullable;

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

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.converter.DateTimeConverter;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.server.AccountApi;
import br.com.jonathanzanella.myexpenses.server.UnsyncModelApi;
import lombok.Getter;
import lombok.Setter;

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

	@Column @Getter @Setter @Expose
	String name;

	@Column @Getter @Setter @Expose
	int balance;

	@Column(typeConverter = DateTimeConverter.class) @Getter @Setter @Expose
	DateTime balanceDate;

	@Column @Getter @Setter @Expose
	boolean accountToPayCreditCard;

	@Column @Getter @Setter @Expose
	String uuid;

	@Column @Getter @Setter @Expose @SerializedName("_id")
	String serverId;

	@Column @Getter @Setter @Expose @SerializedName("created_at")
	long createdAt;

	@Column @Getter @Setter @Expose @SerializedName("updated_at")
	long updatedAt;

	@Column @Getter @Setter
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

	public @Nullable Card getDebitCard() {
		return Card.accountDebitCard(this);
	}

	@Override
	public boolean isSaved() {
		return id != 0;
	}

	@Override
	public String getData() {
		return "name=" + name +
				", balance=" + balance +
				", balanceDate=" + balanceDate.getMillis() +
				", accountToPayCreditCard=" + accountToPayCreditCard +
				", uuid=" + uuid;
	}

	@Override
	public String getHeader(Context ctx) {
		return ctx.getString(R.string.account);
	}

	@Override
	public UnsyncModelApi getServerApi() {
		return accountApi;
	}
}
