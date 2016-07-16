package br.com.jonathanzanella.myexpenses.sync;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.account.AccountApi;
import br.com.jonathanzanella.myexpenses.bill.BillApi;
import br.com.jonathanzanella.myexpenses.card.CardApi;
import br.com.jonathanzanella.myexpenses.expense.ExpenseApi;
import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.receipt.ReceiptApi;
import br.com.jonathanzanella.myexpenses.server.ServerApi;
import br.com.jonathanzanella.myexpenses.source.SourceApi;

/**
 * Created by jzanella on 7/13/16.
 */
public class SyncService extends IntentService {
	private static final String LOG_TAG = SyncService.class.getSimpleName();
	private List<UnsyncModelApi<? extends UnsyncModel>> apis;

	public SyncService() {
		super(SyncService.class.getName());
		apis = new ArrayList<>();
		apis.add(new AccountApi());
		apis.add(new BillApi());
		apis.add(new CardApi());
		apis.add(new ExpenseApi());
		apis.add(new ReceiptApi());
		apis.add(new SourceApi());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.debug(LOG_TAG, "init SyncService");
		if(new ServerApi().healthCheck()) {
			for (UnsyncModelApi<? extends UnsyncModel> api : apis) {
				syncApi(api);
			}
		} else {
			Log.debug(LOG_TAG, "error in health check");
		}
		Log.debug(LOG_TAG, "end SyncService");
	}

	private void syncApi(final UnsyncModelApi<? extends UnsyncModel> api) {
		Log.debug(LOG_TAG, "init sync of " + api.getClass().getSimpleName());
		List<? extends UnsyncModel> unsyncModels = api.index();
		if(unsyncModels != null) {
			for (UnsyncModel unsyncModel : unsyncModels) {
				unsyncModel.syncAndSave(unsyncModel);
				Log.info(LOG_TAG, unsyncModel.getClass().getSimpleName() + " saved\n" + unsyncModel.getData());
			}

			syncLocalData(api);
			Log.debug(LOG_TAG, "finished sync of " + api.getClass().getSimpleName());
		} else {
			Log.error(LOG_TAG, "error syncing " + api.getClass().getSimpleName());
		}
	}

	private void syncLocalData(final UnsyncModelApi<? extends UnsyncModel> api) {
		Log.debug(LOG_TAG, "init of syncLocalData of " + api.getClass().getSimpleName());
		for (UnsyncModel unsyncModel : api.unsyncModels()) {
			api.save(unsyncModel);
		}
		Log.debug(LOG_TAG, "end of syncLocalData of " + api.getClass().getSimpleName());
	}
}
