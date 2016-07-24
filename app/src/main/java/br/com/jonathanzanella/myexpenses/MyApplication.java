package br.com.jonathanzanella.myexpenses;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import net.danlew.android.joda.JodaTimeAndroid;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.sync.SyncService;
import lombok.Getter;

/**
 * Created by jonathan on 01/11/15.
 * Copyright (c) 2015. All rights reserved.
 */
public class MyApplication extends Application {
	@Getter
	private static Context context;
	@Getter
	private static MyApplication application;

	@Override
	public void onCreate() {
		super.onCreate();
		MyApplication.application = this;
		MyApplication.context = getApplicationContext();

		FlowManager.init(new FlowConfig.Builder(this).build());
		JodaTimeAndroid.init(this);
		Stetho.initializeWithDefaults(this);

		GcmNetworkManager.getInstance(this)
				.schedule(new PeriodicTask.Builder()
						.setService(SyncService.class)
						.setTag(SyncService.class.getSimpleName() + "-Periodic")
						.setRequiredNetwork(PeriodicTask.NETWORK_STATE_UNMETERED)
						.setPeriod(Environment.SYNC_PERIODIC_EXECUTION_FREQUENCY)
						.setFlex(Environment.SYNC_FLEX_EXECUTION)
						.setUpdateCurrent(false)
						.setPersisted(true)
						.setRequiresCharging(false)
						.build());
	}

	public void resetDatabase() {
		FlowManager.getDatabase(MyDatabase.NAME).reset(this);
	}
}