package br.com.jonathanzanella.myexpenses.sync;

import android.content.Intent;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.TaskParams;

import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.Environment;
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
public class SyncService extends GcmTaskService {
	public static final String KEY_EXECUTE_SYNC = "KeyExecuteSync";
	private static int NOTIFICATION_ID = 1;
	private static final String LOG_TAG = SyncService.class.getSimpleName();
	private List<UnsyncModelApi<? extends UnsyncModel>> apis;

	private int totalSaved;
	private int totalUpdated;

	public SyncService() {
		super();
		apis = new ArrayList<>();
		apis.add(new AccountApi());
		apis.add(new BillApi());
		apis.add(new CardApi());
		apis.add(new ExpenseApi());
		apis.add(new ReceiptApi());
		apis.add(new SourceApi());

		selfSchedule();
	}

	private void selfSchedule() {
		if(getBaseContext() == null)
			return;

		GcmNetworkManager.getInstance(this)
				.schedule(new PeriodicTask.Builder()
				.setService(SyncService.class)
				.setTag(SyncService.class.getSimpleName() + "-Periodic")
				.setRequiredNetwork(PeriodicTask.NETWORK_STATE_UNMETERED)
				.setPeriod(Environment.SYNC_PERIODIC_EXECUTION_FREQUENCY)
				.setFlex(Environment.SYNC_FLEX_EXECUTION)
				.setUpdateCurrent(true)
				.setPersisted(true)
				.setRequiresCharging(false)
				.build());
	}

	@Override
	public int onStartCommand(Intent intent, int i, int i1) {
		if(intent.getExtras() != null && intent.getExtras().containsKey(KEY_EXECUTE_SYNC)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					onRunTask(null);
				}
			}).start();
			return START_NOT_STICKY;
		} else {
			return super.onStartCommand(intent, i, i1);
		}
	}

	@Override
	public int onRunTask(TaskParams taskParams) {
		Log.debug(LOG_TAG, "init SyncService, task: " + (taskParams != null ? taskParams.getTag() : "without task"));
		totalSaved = 0;
		totalUpdated = 0;

		SyncServiceNotification notification = new SyncServiceNotification(this, NOTIFICATION_ID++, apis.size());

		if(new ServerApi().healthCheck()) {
			notification.incrementProgress();
			for (UnsyncModelApi<? extends UnsyncModel> api : apis) {
				syncApi(api);
				notification.incrementProgress();
			}
		} else {
			Log.debug(LOG_TAG, "error in health check");
			return GcmNetworkManager.RESULT_FAILURE;
		}

		notification.showFinishedJobNotification(this, totalSaved, totalUpdated);

		Log.debug(LOG_TAG, "end SyncService");
		return GcmNetworkManager.RESULT_SUCCESS;
	}

	private void syncApi(final UnsyncModelApi<? extends UnsyncModel> api) {
		final String logTag = LOG_TAG + "-" + api.getClass().getSimpleName();
		Log.debug(logTag, "init sync");
		List<? extends UnsyncModel> unsyncModels = api.index();
		if(unsyncModels != null) {
			for (UnsyncModel unsyncModel : unsyncModels) {
				unsyncModel.syncAndSave(unsyncModel);
				totalSaved++;
				Log.info(logTag, "Saved: " + unsyncModel.getData());
			}

			syncLocalData(api);
			Log.debug(logTag, "finished sync");
		} else {
			Log.error(logTag, "error syncing");
		}
	}

	private void syncLocalData(final UnsyncModelApi<? extends UnsyncModel> api) {
		final String logTag = LOG_TAG + "-" + api.getClass().getSimpleName();
		Log.debug(logTag, "init of syncLocalData");
		for (UnsyncModel unsyncModel : api.unsyncModels()) {
			api.save(unsyncModel);
			totalUpdated++;
		}
		Log.debug(logTag, "end of syncLocalData");
	}
}
