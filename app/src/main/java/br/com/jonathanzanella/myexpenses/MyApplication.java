package br.com.jonathanzanella.myexpenses;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.model.Account;
import br.com.jonathanzanella.myexpenses.model.Source;

/**
 * Created by jonathan on 01/11/15.
 * Copyright (c) 2015. All rights reserved.
 */
public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		FlowManager.init(this);
		JodaTimeAndroid.init(this);

		if(Account.all().size() == 0) {
			Account a = new Account();
			a.setBalance(100000);
			a.setBalanceDate(DateTime.now());
			a.setName("Banco");
			a.save();
			a = new Account();
			a.setBalance(1000);
			a.setBalanceDate(DateTime.now());
			a.setName("Bolso");
			a.save();
			a = new Account();
			a.setBalance(300000);
			a.setBalanceDate(DateTime.now());
			a.setName("Thainara");
			a.save();

			Source s = new Source();
			s.setName("TW");
			s.save();
			s = new Source();
			s.setName("Thainara");
			s.save();
			s = new Source();
			s.setName("Janete");
			s.save();
			s = new Source();
			s.setName("Audrey");
			s.save();
			s = new Source();
			s.setName("Sparta");
			s.save();
		}
	}
}