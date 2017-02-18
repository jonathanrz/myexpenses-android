package br.com.jonathanzanella.myexpenses.account;

import android.support.annotation.WorkerThread;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.database.ModelRepository;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static br.com.jonathanzanella.myexpenses.log.Log.warning;

public class AccountRepository implements ModelRepository<Account> {
	private Repository<Account> repository;
	AccountTable accountTable = new AccountTable();

	public AccountRepository(Repository<Account> repository) {
		this.repository = repository;
	}

	@WorkerThread
	public Account find(final String uuid) {
		return repository.find(accountTable, uuid);
	}

	@WorkerThread
	List<Account> userAccounts() {
		return repository.userData(accountTable);
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
	public OperationResult save(Account account) {
		OperationResult result = new OperationResult();
		if(StringUtils.isEmpty(account.getName()))
			result.addError(ValidationError.NAME);
		if(result.isValid()) {
			if(account.getId() == 0 && account.getUuid() == null)
				account.setUuid(UUID.randomUUID().toString());
			if(account.getId() == 0 && account.getUserUuid() == null)
				account.setUserUuid(Environment.CURRENT_USER_UUID);
			account.setSync(false);
			repository.saveAtDatabase(accountTable, account);
		}
		return result;
	}

	@WorkerThread
	@Override
	public void syncAndSave(final Account unsyncAccount) {
		Account account = find(unsyncAccount.getUuid());

		if(account != null && account.id != unsyncAccount.getId()) {
			if(account.getUpdatedAt() != unsyncAccount.getUpdatedAt())
				warning("Account overwritten", unsyncAccount.getData());
			unsyncAccount.setId(account.id);
		}

		unsyncAccount.setSync(true);
		repository.saveAtDatabase(accountTable, unsyncAccount);
	}
}