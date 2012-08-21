package com.crankcode.services;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.crankcode.activities.MediaPlayer;
import com.crankcode.activities.R;
import com.crankcode.services.binders.MediaServiceBinder;
import com.crankcode.threads.MediaThread;
import com.crankcode.utils.CallManager;
import com.crankcode.utils.ID3Reader;
import com.crankcode.utils.MediaStatus;

public class MediaService extends Service {

	private MediaThread mediaThread = null;
	private final IBinder mediaServiceBinder = new MediaServiceBinder(this);
	private NotificationManager nm;
	private static final int NOTIFY_ID = R.layout.crankplayer;
	private final ID3Reader id3Reader = new ID3Reader();
	private CallManager callManager;

	@Override
	public void onCreate() {
		super.onCreate();
		if (this.mediaThread == null) {
			this.mediaThread = new MediaThread(this);
			this.mediaThread.start();
		}
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.registerCallManager();
	}

	private void registerCallManager() {
		TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if (mgr != null) {
			this.callManager = new CallManager(this.mediaThread);
			mgr.listen(this.callManager, PhoneStateListener.LISTEN_CALL_STATE);
		}
	}

	@Override
	public void onDestroy() {
		this.mediaThread.end();
		this.mediaThread.interrupt();
		this.clearNotifications();
		this.unregisterCallManager();
		super.onDestroy();
	}

	private void unregisterCallManager() {
		if (this.callManager != null) {
			TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			if (mgr != null) {
				mgr.listen(this.callManager, PhoneStateListener.LISTEN_NONE);
				this.callManager = null;
			}
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent bindingIntent) {
		return this.mediaServiceBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		if (this.mediaThread.getStatus().equals(MediaStatus.STOPPED)
				|| this.mediaThread.getStatus().equals(MediaStatus.ERROR)) {
			this.unregisterCallManager();
		}
		return super.onUnbind(intent);
	}

	public MediaThread getMediaThread() {
		return this.mediaThread;
	}

	public void updateNotification(MediaStatus status, File song) {
		Notification notification = null;
		Context context = getApplicationContext();
		Intent notificationIntent = new Intent(this, MediaPlayer.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		switch (status) {
		case PLAYING:
			notification = new Notification(R.drawable.ic_stat_notify_play,
					this.getText(R.string.crankplayer_playing),
					System.currentTimeMillis());
			notification.setLatestEventInfo(context,
					this.getText(R.string.playing), id3Reader.procesar(song),
					contentIntent);
			this.startForeground(NOTIFY_ID, notification);
			break;
		case STOPPED:
			notification = new Notification(R.drawable.ic_stat_notify_stop,
					this.getText(R.string.crankplayer_stopped),
					System.currentTimeMillis());
			notification.setLatestEventInfo(context,
					this.getText(R.string.stopped), "", contentIntent);
			this.stopForeground(true);
			break;
		case PAUSED:
			notification = new Notification(R.drawable.ic_stat_notify_pause,
					this.getText(R.string.crankplayer_paused),
					System.currentTimeMillis());
			notification.setLatestEventInfo(context,
					this.getText(R.string.paused), id3Reader.procesar(song),
					contentIntent);
			this.stopForeground(true);
			break;
		case ERROR:
			notification = new Notification(R.drawable.ic_stat_notify_error,
					this.getText(R.string.crankplayer_error),
					System.currentTimeMillis());
			notification.setLatestEventInfo(context,
					this.getText(R.string.error), "", contentIntent);
			this.stopForeground(true);
			break;
		}
		nm.notify(NOTIFY_ID, notification);
	}

	public void clearNotifications() {
		this.nm.cancel(NOTIFY_ID);
	}
}
