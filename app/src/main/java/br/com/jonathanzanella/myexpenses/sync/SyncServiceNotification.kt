package br.com.jonathanzanella.myexpenses.sync

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat

import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.views.MainActivity

internal class SyncServiceNotification(ctx: Context, private val notificationId: Int, private val totalProgress: Int) {
    private val notifyMgr: NotificationManager
    private val notification: NotificationCompat.Builder
    private var currentProgress: Int = 0

    init {
        this.currentProgress = 0

        notifyMgr = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notification = NotificationCompat.Builder(ctx)
                .setContentTitle(CONTENT_TITLE)
                .setContentText(CONTENT_TEXT_SYNCING)
                .setSmallIcon(R.drawable.ic_server_synced_black_24dp)
                .setProgress(totalProgress, currentProgress, false)
                .setAutoCancel(false)

        notifyMgr.notify(notificationId, notification.build())
    }

    fun incrementProgress() {
        notification.setProgress(totalProgress, currentProgress++, false)
        notifyMgr.notify(notificationId, notification.build())
    }

    fun showFinishedJobNotification(ctx: Context, totalSaved: Int, totalUpdated: Int) {
        val resultIntent = Intent(ctx, MainActivity::class.java)
        val resultPendingIntent = PendingIntent.getActivity(
                ctx,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle(CONTENT_TEXT_SYNCED)
        inboxStyle.addLine(totalSaved.toString() + " items saved")
        inboxStyle.addLine(totalUpdated.toString() + " items updated")

        notification
                .setContentText(CONTENT_TEXT_SYNCED)
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)

        notifyMgr.notify(notificationId, notification.build())
    }

    companion object {
        private val CONTENT_TITLE = "My Expenses"
        private val CONTENT_TEXT_SYNCING = "Data syncing"
        private val CONTENT_TEXT_SYNCED = "Data synced"
    }
}
