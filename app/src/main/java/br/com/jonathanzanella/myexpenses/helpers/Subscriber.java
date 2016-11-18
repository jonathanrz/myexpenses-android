package br.com.jonathanzanella.myexpenses.helpers;

import android.util.Log;

/**
 * Created by jzanella on 11/17/16.
 */

public abstract class Subscriber<T> extends rx.Subscriber<T> {
	private String tag;

	public Subscriber(String tag) {
		this.tag = tag;
	}

	@Override
	public void onCompleted() {
	}

	@Override
	public void onError(Throwable e) {
		Log.e(tag, e.getMessage());
	}
}
