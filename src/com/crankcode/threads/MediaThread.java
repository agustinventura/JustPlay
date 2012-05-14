package com.crankcode.threads;

import android.media.MediaPlayer;

import com.crankcode.utils.CrankLog;

public class MediaThread implements Runnable {

	private MediaPlayer mediaPlayer;

	public void run() {
		CrankLog.v(this, "run()");
		this.mediaPlayer = new MediaPlayer();
	}

}
