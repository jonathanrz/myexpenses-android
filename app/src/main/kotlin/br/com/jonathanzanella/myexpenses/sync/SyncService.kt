package br.com.jonathanzanella.myexpenses.sync

import android.app.Service
import android.content.Intent
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.Environment
import br.com.jonathanzanella.myexpenses.account.AccountApi
import br.com.jonathanzanella.myexpenses.bill.BillApi
import br.com.jonathanzanella.myexpenses.card.CardApi
import br.com.jonathanzanella.myexpenses.expense.ExpenseApi
import br.com.jonathanzanella.myexpenses.receipt.ReceiptApi
import br.com.jonathanzanella.myexpenses.server.ServerApi
import br.com.jonathanzanella.myexpenses.source.SourceApi
import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.GcmTaskService
import com.google.android.gms.gcm.PeriodicTask
import com.google.android.gms.gcm.TaskParams
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class SyncService : GcmTaskService() {
    @Inject
    lateinit var accountApi: AccountApi
    @Inject
    lateinit var billApi: BillApi
    @Inject
    lateinit var cardApi: CardApi
    @Inject
    lateinit var sourceApi: SourceApi
    @Inject
    lateinit var expenseApi: ExpenseApi
    @Inject
    lateinit var receiptApi: ReceiptApi
    private val apis: MutableList<UnsyncModelApi<UnsyncModel>>

    private var totalSaved: Int = 0
    private var totalUpdated: Int = 0

    init {
        App.getAppComponent().inject(this)

        apis = ArrayList()
        apis.add(accountApi)
        apis.add(billApi)
        apis.add(cardApi)
        apis.add(sourceApi)
        apis.add(expenseApi)
        apis.add(receiptApi)
    }

    private fun selfSchedule() {
        GcmNetworkManager.getInstance(this)
                .schedule(PeriodicTask.Builder()
                        .setService(SyncService::class.java)
                        .setTag(SyncService::class.java.simpleName + "-Periodic")
                        .setRequiredNetwork(PeriodicTask.NETWORK_STATE_UNMETERED)
                        .setPeriod(Environment.SYNC_PERIODIC_EXECUTION_FREQUENCY)
                        .setFlex(Environment.SYNC_FLEX_EXECUTION)
                        .setUpdateCurrent(true)
                        .setPersisted(true)
                        .setRequiresCharging(false)
                        .build())
    }

    override fun onStartCommand(intent: Intent?, i: Int, i1: Int) =
        if (intent!!.extras != null && intent.extras.containsKey(KEY_EXECUTE_SYNC)) {
            Thread(Runnable { onRunTask(null) }).start()
            Service.START_NOT_STICKY
        } else {
            super.onStartCommand(intent, i, i1)
        }

    override fun onRunTask(taskParams: TaskParams?): Int {
        val log = Timber.tag("init SyncService")
        if (StringUtils.isEmpty(ServerData(baseContext).serverUrl) || StringUtils.isEmpty(ServerData(baseContext).serverToken)) {
            log.d("Did not executed SyncService because server url and token are not informed")
            return GcmNetworkManager.RESULT_SUCCESS
        }

        log .d("task: ${if (taskParams != null) taskParams.tag else "without task"}")
        totalSaved = 0
        totalUpdated = 0

        val notification = SyncServiceNotification(this, notificationId++, apis.size)

        if (ServerApi().healthCheck()) {
            notification.incrementProgress()
            for (api in apis) {
                syncApi(api)
                notification.incrementProgress()
            }
        } else {
            log.d("error in health check")
            return GcmNetworkManager.RESULT_FAILURE
        }

        notification.showFinishedJobNotification(this, totalSaved, totalUpdated)

        log.d("end SyncService")
        selfSchedule()
        return GcmNetworkManager.RESULT_SUCCESS
    }

    private fun syncApi(api: UnsyncModelApi<UnsyncModel>) {
        val log = Timber.tag("SyncService.syncApi")
        log.d("init sync")
        val unsyncModels = api.index()
        for (unsyncModel in unsyncModels) {
            api.syncAndSave(unsyncModel)
            totalSaved++
            log.i("Saved: " + unsyncModel.getData())
        }

        syncLocalData(api)
        log.d("finished sync")
    }

    private fun syncLocalData(api: UnsyncModelApi<UnsyncModel>) {
        val log = Timber.tag("SyncService.syncLocalData")
        log.d("init sync")
        for (unsyncModel in api.unsyncModels()) {
            api.save(unsyncModel)
            totalUpdated++
        }
        log.d("end sync")
    }

    companion object {
        val KEY_EXECUTE_SYNC = "KeyExecuteSync"
        private var notificationId = 1
    }
}
