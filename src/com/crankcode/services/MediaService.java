package com.crankcode.services;

import android.content.Intent;
import android.os.IBinder;

import com.crankcode.threads.MediaThread;

public class MediaService extends CrankService {

	private Thread mediaThread;

	@Override
	public void onCreate() {
		super.onCreate();
		this.mediaThread = new Thread(new MediaThread());
		this.mediaThread.start();
	}

	@Override
	public void onDestroy() {
		this.mediaThread.interrupt();
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		super.onRebind(intent);
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
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
