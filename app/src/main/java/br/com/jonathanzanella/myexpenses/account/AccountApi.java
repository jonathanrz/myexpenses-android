package br.com.jonathanzanella.myexpenses.account;

import com.raizlabs.android.dbflow.StringUtils;

import java.util.List;

import br.com.jonathanzanella.myexpenses.server.Server;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jzanella on 6/12/16.
 */
public class AccountApi implements UnsyncModelApi<Account> {
	AccountInterface accountInterface;

	private AccountInterface getInterface() {
		if(accountInterface == null)
			accountInterface = new Server().accountInterface();
		return accountInterface;
	}

	@Override
	public void index(Subscriber<List<Account>> subscriber) {
		Observable<List<Account>> observable = getInterface().index(Account.greaterUpdatedAt());
		observable.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.newThread())
				.subscribe(subscriber);
	}

	@Override
	public void save(UnsyncModel model, Subscriber<Account> subscriber) {
		Account account = (Account) model;
		Observable<Account> observable;
		if(StringUtils.isNotNullOrEmpty(account.getServerId()))
			observable = getInterface().update(account.getServerId(), account);
		else
			observable = getInterface().create(account);

		observable.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.newThread())
				.subscribe(subscriber);
	}
}