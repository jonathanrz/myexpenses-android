package br.com.jonathanzanella.myexpenses.account;

import android.support.annotation.Nullable;
import android.support.test.espresso.idling.CountingIdlingResource;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.helpers.Subscriber;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import rx.Observable;
import rx.schedulers.Schedulers;

import static br.com.jonathanzanella.myexpenses.log.Log.warning;

/**
 * Created by jzanella on 8/27/16.
 */

public class AccountRepository {
	private Repository<Account> repository;
	AccountTable accountTable = new AccountTable();

	public AccountRepository(Repository<Account> repository) {
		this.repository = repository;
	}

	public Observable<Account> find(final String uuid) {
		return Observable.fromCallable(new Callable<Account>() {

			@Override
			public @Nullable
			Account call() throws Exception {
				return repository.find(accountTable, uuid);
			}
		}).observeOn(Schedulers.io());
	}

	List<Account> userAccounts() {
		return repository.userData(accountTable);
	}

	public long greaterUpdatedAt() {
		return repository.greaterUpdatedAt(accountTable);
	}

	public List<Account> unsync() {
		return repository.unsync(accountTable);
	}

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

	public void syncAndSave(final Account unsyncAccount) {
		final CountingIdlingResource idlingResource = new CountingIdlingResource("AccountRepositorySave");
		idlingResource.increment();
		find(unsyncAccount.getUuid())
				.observeOn(Schedulers.io())
				.subscribe(new Subscriber<Account>("AccountRepository.") {
					@Override
					public void onNext(Account bill) {
						if(bill != null && bill.id != unsyncAccount.getId()) {
							if(bill.getUpdatedAt() != unsyncAccount.getUpdatedAt())
								warning("Account overwritten", unsyncAccount.getData());
							unsyncAccount.setId(bill.id);
						}

						unsyncAccount.setSync(true);
						repository.saveAtDatabase(accountTable, unsyncAccount);
						idlingResource.decrement();
					}
				});
	}
}