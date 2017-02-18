package br.com.jonathanzanella.myexpenses.receipt;

import android.support.annotation.WorkerThread;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@EqualsAndHashCode
public class Receipt implements Transaction, UnsyncModel {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
	private AccountRepository accountRepository = null;
	private ReceiptRepository receiptRepository = null;

	@Setter @Getter
	private long id;

	@Getter @Setter @Expose
	private String uuid;

	@Getter @Setter @Expose
	private String name;

	@Getter @Setter @Expose
	private DateTime date;

	@Getter @Setter @Expose
	private int income;

	@Getter @Setter @Expose
	private String sourceUuid;

	@Getter @Setter @Expose
	private String accountUuid;

	@Getter @Setter @Expose
	private boolean credited;

	@Getter @Setter @Expose
	private boolean ignoreInResume;

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

	@Getter @Setter @Expose
	private boolean removed;

	@Override
	public int getAmount() {
		return getIncome();
	}

	@Override
	public boolean credited() {
		return credited;
	}

	@Override
	public boolean debited() {
		return true;
	}

	private AccountRepository getAccountRepository() {
		if(accountRepository == null)
			accountRepository = new AccountRepository(new Repository<Account>(MyApplication.getContext()));
		return accountRepository;
	}

	private ReceiptRepository getReceiptRepository() {
		if(receiptRepository == null)
			receiptRepository = new ReceiptRepository(new Repository<Receipt>(MyApplication.getContext()));
		return receiptRepository;
	}

	@WorkerThread
	public Source getSource() {
		return new SourceRepository(new Repository<Source>(MyApplication.getContext())).find(sourceUuid);
	}

	public void setSource(@NonNull Source s) {
		sourceUuid = s.getUuid();
	}

	public Account getAccount() {
		return getAccountRepository().find(accountUuid);
	}

	public void setAccount(@NonNull Account a) {
		accountUuid = a.getUuid();
	}

	boolean isShowInResume() {
		return !ignoreInResume;
	}

	void setShowInResume(boolean b) {
		ignoreInResume = !b;
	}

	public String getIncomeFormatted() {
		return NumberFormat.getCurrencyInstance().format(income / 100.0);
	}

	void repeat() {
		id = 0;
		uuid = null;
		date = date.plusMonths(1);
	}

	@Override
	public String getData() {
		return "name=" + name +
				"\nuuid=" + uuid +
				"\ndate=" + sdf.format(date.toDate()) +
				"\nincome=" + income;
	}

	public void credit() {
		Account account = getAccount();
		account.credit(getIncome());
		getAccountRepository().save(account);
		setCredited(true);
		getReceiptRepository().save(this);
	}

	void delete() {
		removed = true;
		sync = false;
		getReceiptRepository().save(this);
	}

}
