package br.com.jonathanzanella.myexpenses.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import br.com.jonathanzanella.myexpenses.model.Account;
import br.com.jonathanzanella.myexpenses.model.Receipt;

/**
 * Created by jzanella on 2/2/16.
 */
public class CashierService extends IntentService {
	private static final String LOG_TAG = "CashierService";

	public CashierService() {
		super(LOG_TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(LOG_TAG, "init CashierService");
		creditReceipts();
	}

	private void creditReceipts() {
		for (Receipt receipt : Receipt.uncredited()) {
			Account a = receipt.getAccount();
			a.credit(receipt.getIncome());
			a.save();
			receipt.setCredited(true);
			receipt.save();
			Log.d(LOG_TAG, "credited " + receipt.getIncome() + " to " + a.getName() + " from "
					+ receipt.getSource().getName() + " id=" + receipt.getId());
		}

		for (Receipt receipt : Receipt.changed()) {
			Account a = receipt.getAccount();
			int changedValue = receipt.changedValue();
			a.credit(changedValue);
			a.save();
			receipt.resetNewIncome();
			receipt.save();
			Log.d(LOG_TAG, "updated " + a.getName() + " with " + changedValue + " from "
					+ receipt.getSource().getName() + " id=" + receipt.getId());
		}
	}
}
