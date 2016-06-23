package br.com.jonathanzanella.myexpenses;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.util.UUID;

import br.com.jonathanzanella.myexpenses.models.Account;
import br.com.jonathanzanella.myexpenses.models.Card;
import br.com.jonathanzanella.myexpenses.models.CardType;
import br.com.jonathanzanella.myexpenses.models.Receipt;
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

		//noinspection PointlessBooleanExpression
		if(Environment.IS_DEBUG && Account.all().size() == 0) {
			Account bankAcc = new Account();
			bankAcc.setBalance(100000);
			bankAcc.setBalanceDate(DateTime.now());
			bankAcc.setName("Banco");
			bankAcc.setUuid(UUID.randomUUID().toString());
			bankAcc.save();

			Card c = new Card();
			c.setName("Crédito");
			c.setType(CardType.CREDIT);
			c.setAccount(bankAcc);
			c.save();
			c = new Card();
			c.setName("Débito");
			c.setType(CardType.DEBIT);
			c.setAccount(bankAcc);
			c.save();
		}
	}
}