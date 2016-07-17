package br.com.jonathanzanella.myexpenses;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.facebook.stetho.Stetho;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.PeriodicTask;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import net.danlew.android.joda.JodaTimeAndroid;

import br.com.jonathanzanella.myexpenses.sync.SyncService;
import lombok.Getter;

/**
 * Created by jonathan on 01/11/15.
 * Copyright (c) 2015. All rights reserved.
 */
public class MyApplication extends Application {
	@Getter
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		MyApplication.context = getApplicationContext();

		FlowManager.init(new FlowConfig.Builder(this).build());
		JodaTimeAndroid.init(this);
		Stetho.initializeWithDefaults(this);

		GcmNetworkManager.getInstance(this)
				.schedule(new OneoffTask.Builder()
				.setService(SyncService.class)
				.setTag(SyncService.class.getSimpleName() + "-OneTime")
				.setRequiredNetwork(PeriodicTask.NETWORK_STATE_UNMETERED)
				.setExecutionWindow(0, 30)
				.setUpdateCurrent(true)
				.setPersisted(true)
				.setRequiresCharging(false)
				.build());

		startService(new Intent(this, SyncService.class));
	}
}