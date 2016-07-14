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
import rx.Subscriber;

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
		new ServerApi().healthCheck(new Subscriber<Void>() {
			@Override
			public void onCompleted() {
				for (UnsyncModelApi<? extends UnsyncModel> api : apis) {
					syncApi(api);
				}
			}

			@Override
			public void onError(Throwable e) {
				Log.error(LOG_TAG, "Error in health check: " + e.getMessage());
				e.printStackTrace();
			}

			@Override
			public void onNext(Void v) {
				Log.debug(LOG_TAG, "finished health check");
			}
		});
	}

	private void syncApi(final UnsyncModelApi api) {
		Log.info(LOG_TAG, "init sync of " + api.getClass().getSimpleName());
		//noinspection unchecked
		api.index(new Subscriber<List<? extends UnsyncModel>>() {
			@Override
			public void onCompleted() {
				Log.info(LOG_TAG, "finished sync of " + api.getClass().getSimpleName());
			}

			@Override
			public void onError(Throwable e) {
				Log.error(LOG_TAG, api.getClass().toString() + "#index error: " + e.getMessage());
				e.printStackTrace();
			}

			@Override
			public void onNext(List<? extends UnsyncModel> unsyncModels) {
				for (UnsyncModel unsyncModel : unsyncModels) {
					unsyncModel.syncAndSave();
					Log.debug(LOG_TAG, unsyncModel.getClass().getSimpleName() + " saved\nuuid: " + unsyncModel.getUuid());
				}
			}
		});
	}
}
