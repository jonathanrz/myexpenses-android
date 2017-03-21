package br.com.jonathanzanella.myexpenses;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

import net.danlew.android.joda.JodaTimeAndroid;

import java.lang.ref.WeakReference;

import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;

public class MyApplication extends Application {
	@SuppressLint("StaticFieldLeak")
	private static WeakReference<Context> context;

	@Override
	public void onCreate() {
		super.onCreate();
		MyApplication.context = new WeakReference<>(getApplicationContext());

		JodaTimeAndroid.init(this);
		Stetho.initializeWithDefaults(this);
		new DatabaseHelper(this).runMigrations();
	}

	public static Context getContext() {
		return context.get();
	}
}