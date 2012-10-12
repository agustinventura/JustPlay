package com.crankcode.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.crankcode.threads.MediaThread;

public class SDCardManager extends BroadcastReceiver {

	private final MediaThread mediaThread;

	public SDCardManager(MediaThread mediaThread) {
		this.mediaThread = mediaThread;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
			// Device has been mounted on pc as mass storage
			this.mediaThread.releaseMediaPlayer();
		}
		if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
			this.mediaThread.createMediaPlayer();
		}
	}

}
