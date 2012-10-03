package com.crankcode.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.crankcode.threads.MediaThread;

public class SDCardManager extends BroadcastReceiver {

	private final MediaThread mediaThread;

	public SDCardManager(MediaThread mediaThread) {
		this.mediaThread = mediaThread;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_SHARED.equals(state)) {
			// Device has been mounted on pc as mass storage
			this.mediaThread.stop();
		}
	}

}
