package com.crankcode.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class CrankActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		v("CrankPlayer" + "CrankActivity.onCreate()");

	}

	@Override
	public void onStart() {
		super.onStart();
		v("CrankPlayer" + "CrankActivity.onStart()");
	}

	@Override
	public void onResume() {
		super.onResume();
		v("CrankPlayer" + "CrankActivity.onResume()");

	}

	@Override
	public void onPause() {
		super.onPause();
		System.gc();
		v("CrankPlayer" + "CrankActivity.onPause()");
	}

	@Override
	public void onStop() {
		super.onStop();
		v("CrankPlayer" + "CrankActivity.onStop()");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		v("CrankPlayer" + "CrankActivity.onDestroy()");
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		w("CrankPlayer" + "CrankActivity.onLowMemory()");

	}

	public void v(String message) {
		Log.v(getClass().getSimpleName(), message);
	}

	public void d(String message) {
		Log.d(getClass().getSimpleName(), message);
	}

	public void i(String message) {
		Log.i(getClass().getSimpleName(), message);
	}

	public void w(String message) {
		Log.w(getClass().getSimpleName(), message);
	}

	public void e(String message) {
		Log.e(getClass().getSimpleName(), message);
	}
}
