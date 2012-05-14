package com.crankcode.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.crankcode.utils.CrankLog;

public class CrankService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		CrankLog.v(this, "onBind()");
		return null;
	}

	@Override
	public void onCreate() {
		CrankLog.v(this, "onCreate()");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		CrankLog.v(this, "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		CrankLog.w(this, "onLowMemory()");
		super.onLowMemory();
	}

	@Override
	public void onRebind(Intent intent) {
		CrankLog.v(this, "onRebind()");
		super.onRebind(intent);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		CrankLog.v(this, "onStart()");
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		CrankLog.v(this, "onStartCommand()");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		CrankLog.v(this, "onUnbind()");
		return super.onUnbind(intent);
	}

}
