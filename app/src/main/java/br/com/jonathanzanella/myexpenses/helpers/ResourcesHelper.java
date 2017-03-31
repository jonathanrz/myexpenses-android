package br.com.jonathanzanella.myexpenses.helpers;

import android.content.Context;
import android.support.annotation.StringRes;

import java.lang.ref.WeakReference;

public class ResourcesHelper {
	private WeakReference<Context> contextWeakReference;

	public ResourcesHelper(Context context) {
		this.contextWeakReference = new WeakReference<>(context);
	}

	public String getString(@StringRes int string) {
		return contextWeakReference.get().getString(string);
	}
}
