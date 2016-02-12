package br.com.jonathanzanella.myexpenses.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import br.com.jonathanzanella.myexpenses.models.Account;
import br.com.jonathanzanella.myexpenses.models.Chargeable;
import br.com.jonathanzanella.myexpenses.models.Expense;
import br.com.jonathanzanella.myexpenses.models.Receipt;

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
		chargeExpenses();
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

	private void chargeExpenses() {
		for (Expense expense : Expense.uncharged()) {
			Chargeable c = expense.getChargeable();
			c.debit(expense.getValue());
			c.save();
			expense.setCharged(true);
			expense.save();
			Log.d(LOG_TAG, "charged " + expense.getValue() + " to " + c.getName() + " id=" + expense.getId());
		}

		for (Expense expense : Expense.changed()) {
			Chargeable c = expense.getChargeable();
			int changedValue = expense.changedValue();
			c.debit(changedValue);
			c.save();
			expense.resetNewValue();
			expense.save();
			Log.d(LOG_TAG, "updated " + c.getName() + " with " + changedValue + " id=" + expense.getId());
		}
	}
}
