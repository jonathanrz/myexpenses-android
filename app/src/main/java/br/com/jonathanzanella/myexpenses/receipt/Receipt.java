package br.com.jonathanzanella.myexpenses.receipt;

import android.support.annotation.WorkerThread;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;
import lombok.NonNull;

public class Receipt implements Transaction, UnsyncModel {
	private AccountRepository accountRepository = null;
	private ReceiptRepository receiptRepository = null;

	private long id;
	@Expose
	private String uuid;
	@Expose
	private String name;
	@Expose
	private DateTime date;
	@Expose
	private int income;
	@Expose
	private String sourceUuid;
	@Expose
	private String accountUuid;
	@Expose
	private boolean credited;
	@Expose
	private boolean ignoreInResume;
	@Expose @SerializedName("_id")
	private String serverId;
	@Expose @SerializedName("created_at")
	private long createdAt;
	@Expose @SerializedName("updated_at")
	private long updatedAt;
	private boolean sync;
	@Expose
	private boolean removed;
	private int repetition = 1;
	private int installments = 1;
	private Account account;
	private Source source;

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
			accountRepository = new AccountRepository(new RepositoryImpl<Account>(MyApplication.getContext()));
		return accountRepository;
	}

	private ReceiptRepository getReceiptRepository() {
		if(receiptRepository == null)
			receiptRepository = new ReceiptRepository(new RepositoryImpl<Receipt>(MyApplication.getContext()));
		return receiptRepository;
	}

	@WorkerThread
	public Source getSource() {
		if(source == null)
			source = new SourceRepository(new RepositoryImpl<Source>(MyApplication.getContext())).find(sourceUuid);
		return source;
	}

	public void setSource(@NonNull Source s) {
		this.source = s;
		sourceUuid = s.getUuid();
	}

	Account getAccountFromCache() {
		return getAccount(false);
	}

	public Account loadAccount() {
		return getAccount(true);
	}

	private Account getAccount(boolean ignoreCache) {
		if(account == null || ignoreCache)
			account = getAccountRepository().find(accountUuid);
		return account;
	}

	public void setAccount(@NonNull Account a) {
		this.account = a;
		accountUuid = a.getUuid();
	}

	boolean isShowInResume() {
		return !ignoreInResume;
	}

	void setShowInResume(boolean b) {
		ignoreInResume = !b;
	}

	public String getIncomeFormatted() {
		return CurrencyHelper.format(income);
	}

	Receipt repeat(String originalName, int index) {
		Receipt receipt = new Receipt();
		if(installments > 1)
			receipt.name = formatReceiptName(originalName, index);
		else
			receipt.name = originalName;
		receipt.date = date.plusMonths(1);
		receipt.income = income;
		receipt.sourceUuid = sourceUuid;
		receipt.accountUuid = accountUuid;
		receipt.ignoreInResume = ignoreInResume;
		receipt.serverId = serverId;
		receipt.repetition = repetition;
		receipt.installments = installments;
		receipt.account = account;
		receipt.source = source;

		return receipt;
	}

	int getRepetition() {
		return Math.max(repetition, installments);
	}

	String formatReceiptName(String originalName, int i) {
		return String.format(Environment.PTBR_LOCALE, "%s %02d/%02d", originalName, i, installments);
	}

	@Override
	public String getData() {
		return "name=" + name +
				"\nuuid=" + uuid +
				"\nserverId=" + serverId +
				"\ndate=" + SIMPLE_DATE_FORMAT.format(date.toDate()) +
				"\nincome=" + income;
	}

	public void credit() {
		Account acc = loadAccount();
		acc.credit(getIncome());
		getAccountRepository().save(acc);
		setCredited(true);
		getReceiptRepository().save(this);
	}

	void delete() {
		removed = true;
		sync = false;
		getReceiptRepository().save(this);
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

	@Override
	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public int getIncome() {
		return income;
	}

	public void setIncome(int income) {
		this.income = income;
	}

	String getSourceUuid() {
		return sourceUuid;
	}

	void setSourceUuid(String sourceUuid) {
		this.sourceUuid = sourceUuid;
	}

	String getAccountUuid() {
		return accountUuid;
	}

	void setAccountUuid(String accountUuid) {
		this.accountUuid = accountUuid;
	}

	public boolean isCredited() {
		return credited;
	}

	void setCredited(boolean credited) {
		this.credited = credited;
	}

	boolean isIgnoreInResume() {
		return ignoreInResume;
	}

	void setIgnoreInResume(boolean ignoreInResume) {
		this.ignoreInResume = ignoreInResume;
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

	boolean isRemoved() {
		return removed;
	}

	void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public void setRepetition(int repetition) {
		this.repetition = repetition;
	}

	public int getInstallments() {
		return installments;
	}

	public void setInstallments(int installments) {
		this.installments = installments;
	}
}
