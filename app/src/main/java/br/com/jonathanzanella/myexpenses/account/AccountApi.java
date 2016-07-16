package br.com.jonathanzanella.myexpenses.account;

import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.StringUtils;

import java.io.IOException;
import java.util.List;

import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.server.Server;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jzanella on 6/12/16.
 */
public class AccountApi implements UnsyncModelApi<Account> {
	private static final String LOG_TAG = AccountApi.class.getSimpleName();
	private AccountInterface accountInterface;

	private AccountInterface getInterface() {
		if(accountInterface == null)
			accountInterface = new Server().accountInterface();
		return accountInterface;
	}

	@Override
	public @Nullable List<Account> index() {
		Call<List<Account>> caller = getInterface().index(Account.greaterUpdatedAt());

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
		if(StringUtils.isNotNullOrEmpty(account.getServerId()))
			caller = getInterface().update(account.getServerId(), account);
		else
			caller = getInterface().create(account);

		try {
			Response<Account> response = caller.execute();
			if(response.isSuccessful()) {
				model.syncAndSave(response.body());
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
	public List<Account> unsyncModels() {
		return Account.unsync();
	}

	@Override
	public long greaterUpdatedAt() {
		return Account.greaterUpdatedAt();
	}
}