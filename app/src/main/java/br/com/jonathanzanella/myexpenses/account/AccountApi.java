package br.com.jonathanzanella.myexpenses.account;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.server.Server;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import retrofit2.Call;
import retrofit2.Response;

@WorkerThread
public class AccountApi implements UnsyncModelApi<Account> {
	private static final String LOG_TAG = AccountApi.class.getSimpleName();
	private AccountInterface accountInterface;
	private AccountRepository accountRepository;

	@Override
	@Nullable
	public List<Account> index() {
		Call<List<Account>> caller = getInterface().index(getRepository().greaterUpdatedAt());

		try {
			Response<List<Account>> response = caller.execute();
			if(response.isSuccessful()) {
				return response.body();
			} else {
				Log.error(LOG_TAG, "Index request error: " + response.message());
			}
		} catch (IOException e) {
			Log.error(LOG_TAG, "Index request error: " + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void save(UnsyncModel model) {
		Account account = (Account) model;
		Call<Account> caller;
		if(StringUtils.isEmpty(account.getServerId()))
			caller = getInterface().create(account);
		else
			caller = getInterface().update(account.getServerId(), account);

		try {
			Response<Account> response = caller.execute();
			if(response.isSuccessful()) {
				getRepository().syncAndSave(response.body());
				Log.info(LOG_TAG, "Updated: " + account.getData());
			} else {
				Log.error(LOG_TAG, "Save request error: " + response.message() + " uuid: " + account.getUuid());
			}
		} catch (IOException e) {
			Log.error(LOG_TAG, "Save request error: " + e.getMessage() + " uuid: " + account.getUuid());
			e.printStackTrace();
		}
	}

	@Override
	public void syncAndSave(UnsyncModel unsyncAccount) {
		if(!(unsyncAccount instanceof Account))
			throw new UnsupportedOperationException("UnsyncModel is not an Account");
		getRepository().syncAndSave((Account)unsyncAccount);
	}

	@Override
	public List<Account> unsyncModels() {
		return getRepository().unsync();
	}

	@Override
	public long greaterUpdatedAt() {
		return getRepository().greaterUpdatedAt();
	}

	@WorkerThread
	public AccountRepository getRepository() {
		if(accountRepository == null)
			accountRepository = new AccountRepository(new RepositoryImpl<Account>(MyApplication.getContext()));
		return accountRepository;
	}

	private AccountInterface getInterface() {
		if(accountInterface == null)
			accountInterface = new Server(MyApplication.getContext()).accountInterface();
		return accountInterface;
	}
}