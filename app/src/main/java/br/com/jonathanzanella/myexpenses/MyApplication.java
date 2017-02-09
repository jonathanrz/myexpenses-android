package br.com.jonathanzanella.myexpenses;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

import net.danlew.android.joda.JodaTimeAndroid;

import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import lombok.Getter;

public class MyApplication extends Application {
	@Getter
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		MyApplication.context = getApplicationContext();

		JodaTimeAndroid.init(this);
		Stetho.initializeWithDefaults(this);
		new DatabaseHelper(this).runMigrations();
	}
}