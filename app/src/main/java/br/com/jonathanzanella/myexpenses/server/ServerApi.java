package br.com.jonathanzanella.myexpenses.server;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jzanella on 7/13/16.
 */
public class ServerApi {
	ServerInterface serverInterface;

	private ServerInterface getInterface() {
		if(serverInterface == null)
			serverInterface = new Server().serverInterface();
		return serverInterface;
	}

	public void healthCheck(Subscriber<Void> subscriber) {
		Observable<Void> observable = getInterface().healthCheck();
		observable.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.newThread())
				.subscribe(subscriber);
	}
}