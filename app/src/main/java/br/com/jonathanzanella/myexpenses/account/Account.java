package br.com.jonathanzanella.myexpenses.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = false)
public class Account implements Chargeable, UnsyncModel {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

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
	public String getData() {
		return "name=" + name +
				"\nuuid=" + uuid +
				"\nbalance=" + balance +
				"\naccountToPayCreditCard=" + accountToPayCreditCard +
				"\nuuid=" + uuid;
	}
}