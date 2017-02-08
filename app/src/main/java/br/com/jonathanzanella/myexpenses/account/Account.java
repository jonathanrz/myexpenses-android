package br.com.jonathanzanella.myexpenses.account;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
@EqualsAndHashCode(callSuper = false)
public class Account implements Chargeable, UnsyncModel {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
	private static final AccountApi accountApi = new AccountApi();

	@Setter @Getter
	long id;

	@Getter @Setter @Expose
	String uuid;

	@Getter @Setter @Expose
	String name;

	@Getter @Setter @Expose
	int balance;

	@Getter @Setter @Expose
	boolean accountToPayCreditCard;

	@Getter @Setter @Expose
	boolean accountToPayBills;

	@Getter @Setter @Expose
	String userUuid;

	@Getter @Setter @Expose @SerializedName("_id")
	String serverId;

	@Getter @Setter @Expose @SerializedName("created_at")
	long createdAt;

	@Getter @Setter @Expose @SerializedName("updated_at")
	long updatedAt;

	@Getter @Setter
	boolean sync;

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
	public boolean isSaved() {
		return id != 0;
	}

	@Override
	public void syncAndSave(UnsyncModel unsyncModel) {
		throw new UnsupportedOperationException("You should use AccountRepository");
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
