package com.crankcode.services;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.crankcode.activities.CrankPlayer;
import com.crankcode.activities.R;
import com.crankcode.services.binders.MediaServiceBinder;
import com.crankcode.threads.MediaThread;
import com.crankcode.utils.ID3Reader;
import com.crankcode.utils.MediaStatus;

public class MediaService extends CrankService {

	private MediaThread mediaThread = null;
	private final IBinder mediaServiceBinder = new MediaServiceBinder(this);
	private NotificationManager nm;
	private static final int NOTIFY_ID = R.layout.crankplayer;
	private final ID3Reader id3Reader = new ID3Reader();

	@Override
	public void onCreate() {
		super.onCreate();
		if (this.mediaThread == null) {
			this.mediaThread = new MediaThread(this);
			this.mediaThread.start();
		}
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.createNotification();
	}

	@Override
	public void onDestroy() {
		this.mediaThread.end();
		this.mediaThread.interrupt();
		this.nm.cancel(NOTIFY_ID);
		super.onDestroy();
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
		// Nothing to do here
		return super.onUnbind(intent);
	}

	public MediaThread getMediaThread() {
		return this.mediaThread;
	}

	public void createNotification() {
		// Create the notification
		// TODO: display crankplayer icon
		Notification notification = new Notification(R.drawable.playbackstart,
				this.getText(R.string.started), System.currentTimeMillis());

		Context context = getApplicationContext();
		// Create the intent that will be fired when user clicks on the
		// notification
		Intent notificationIntent = new Intent(this, CrankPlayer.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// The PendingIntent represents the firing of above intent
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		// Notification general information
		notification.setLatestEventInfo(context,
				this.getText(R.string.started), "", contentIntent);
		// Display
		nm.notify(NOTIFY_ID, notification);
	}

	public void updateNotification(MediaStatus status, File song) {
		Notification notification = null;
		Context context = getApplicationContext();
		Intent notificationIntent = new Intent(this, CrankPlayer.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		// TODO: ICONS!!!
		switch (status) {
		case PLAYING:
			notification = new Notification(R.drawable.playbackstart,
					this.getText(R.string.crankplayer_playing),
					System.currentTimeMillis());
			notification.setLatestEventInfo(context,
					this.getText(R.string.playing), id3Reader.procesar(song),
					contentIntent);
			break;
		case STOPPED:
			notification = new Notification(R.drawable.playbackpause,
					this.getText(R.string.crankplayer_stopped),
					System.currentTimeMillis());
			notification.setLatestEventInfo(context,
					this.getText(R.string.stopped), "", contentIntent);
			break;
		case PAUSED:
			notification = new Notification(R.drawable.playbackpause,
					this.getText(R.string.crankplayer_paused),
					System.currentTimeMillis());
			notification.setLatestEventInfo(context,
					this.getText(R.string.paused), id3Reader.procesar(song),
					contentIntent);
			break;
		case ERROR:
			notification = new Notification(R.drawable.playbackpause,
					this.getText(R.string.crankplayer_error),
					System.currentTimeMillis());
			notification.setLatestEventInfo(context,
					this.getText(R.string.error), "", contentIntent);
			break;
		}
		nm.notify(NOTIFY_ID, notification);
	}
}
