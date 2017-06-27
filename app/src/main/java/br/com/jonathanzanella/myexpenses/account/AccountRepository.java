package br.com.jonathanzanella.myexpenses.account;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.ModelRepository;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.database.Where;
import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;

public class AccountRepository implements ModelRepository<Account> {
	private final Repository<Account> repository;
	private final AccountTable accountTable = new AccountTable();

	public AccountRepository(Repository<Account> repository) {
		this.repository = repository;
	}

	@WorkerThread
	public Account find(final String uuid) {
		return repository.find(accountTable, uuid);
	}

	@WorkerThread
	List<Account> all() {
		return repository.query(accountTable, new Where(null).orderBy(Fields.NAME));
	}

	@WorkerThread
	List<Account> forResumeScreen() {
		return repository.query(accountTable, new Where(Fields.SHOW_IN_RESUME).eq(true).orderBy(Fields.NAME));
	}

	@WorkerThread
	public long greaterUpdatedAt() {
		return repository.greaterUpdatedAt(accountTable);
	}

	@WorkerThread
	public List<Account> unsync() {
		return repository.unsync(accountTable);
	}

	@WorkerThread
	public ValidationResult save(Account account) {
		ValidationResult result = validate(account);
		if(result.isValid()) {
			if(account.getId() == 0 && account.getUuid() == null)
				account.setUuid(UUID.randomUUID().toString());
			account.setSync(false);
			repository.saveAtDatabase(accountTable, account);
		}
		return result;
	}

	@NonNull
	private ValidationResult validate(Account account) {
		ValidationResult result = new ValidationResult();
		if(StringUtils.isEmpty(account.getName()))
			result.addError(ValidationError.NAME);
		return result;
	}

	@WorkerThread
	@Override
	public ValidationResult syncAndSave(final Account unsyncAccount) {
		ValidationResult result = validate(unsyncAccount);
		if(!result.isValid()) {
			Log.Companion.warning("Account sync validation failed", unsyncAccount.getData() + "\nerrors: " + result.getErrorsAsString());
			return result;
		}

		Account account = find(unsyncAccount.getUuid());

		if(account != null && account.getId() != unsyncAccount.getId()) {
			if(account.getUpdatedAt() != unsyncAccount.getUpdatedAt())
				Log.Companion.warning("Account overwritten", unsyncAccount.getData());
			unsyncAccount.setId(account.getId());
		}

		unsyncAccount.setSync(true);
		repository.saveAtDatabase(accountTable, unsyncAccount);

		return result;
	}
}