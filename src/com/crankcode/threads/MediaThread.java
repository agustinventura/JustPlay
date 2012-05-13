package com.crankcode.threads;

import android.util.Log;

public class MediaThread implements Runnable {

	public void run() {
		Log.d("CrankPlayer", "MediaThread.run");
	}

}
