package com.crankcode.services;

import com.crankcode.threads.MediaThread;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MediaService extends Service {

	private Thread mediaThread;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
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
		Log.d("CrankPlayer", "MediaService.onStart()");
		super.onStartCommand(intent, flags, startId);
		this.mediaThread = new Thread(new MediaThread());
		this.mediaThread.start();
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
