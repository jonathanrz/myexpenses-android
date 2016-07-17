package br.com.jonathanzanella.myexpenses.sync;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

/**
 * Created by jzanella on 7/17/16.
 */
class SyncServiceNotification {
	public static final String CONTENT_TITLE = "My Expenses";
	public static final String CONTENT_TEXT_SYNCING = Environment.CURRENT_USER + " data syncing";
	public static final String CONTENT_TEXT_SYNCED = Environment.CURRENT_USER + " data synced";
	private final int notificationId;
	private final NotificationManager notifyMgr;
	private final NotificationCompat.Builder notification;
	private int currentProgress;
	private final int totalProgress;

	SyncServiceNotification(Context ctx, int notificationId, int totalProgress) {
		this.notificationId = notificationId;
		this.currentProgress = 0;
		this.totalProgress = totalProgress;

		notifyMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

		notification = new NotificationCompat.Builder(ctx)
				.setContentTitle(CONTENT_TITLE)
				.setContentText(CONTENT_TEXT_SYNCING)
				.setSmallIcon(R.drawable.ic_server_synced_black_24dp)
				.setProgress(totalProgress, currentProgress, false)
				.setAutoCancel(false);

		notifyMgr.notify(notificationId, notification.build());
	}

	void incrementProgress() {
		notification.setProgress(totalProgress, currentProgress++, false);
		notifyMgr.notify(notificationId, notification.build());
	}

	void showFinishedJobNotification(Context ctx, int totalSaved, int totalUpdated) {
		Intent resultIntent = new Intent(ctx, MainActivity.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(
				ctx,
				0,
				resultIntent,
				PendingIntent.FLAG_UPDATE_CURRENT
		);

		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		inboxStyle.setBigContentTitle(CONTENT_TEXT_SYNCED);
		inboxStyle.addLine(totalSaved + " items saved");
		inboxStyle.addLine(totalUpdated + " items updated");

		notification
				.setContentText(CONTENT_TEXT_SYNCED)
				.setStyle(inboxStyle)
				.setAutoCancel(true)
				.setContentIntent(resultPendingIntent);

		notifyMgr.notify(notificationId, notification.build());
	}
}
