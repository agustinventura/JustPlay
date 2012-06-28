package com.crankcode.utils;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.crankcode.threads.MediaThread;

public class CallManager extends PhoneStateListener {

	private final MediaThread mediaThread;

	public CallManager(MediaThread mediaThread) {
		this.mediaThread = mediaThread;
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		if (state == TelephonyManager.CALL_STATE_RINGING) {
			this.mediaThread.pause();
		} else if (state == TelephonyManager.CALL_STATE_IDLE) {
			this.mediaThread.play();
		}
		super.onCallStateChanged(state, incomingNumber);
	}
}
