package com.crankcode.activities;

import android.app.ListActivity;
import android.os.Bundle;

import com.crankcode.utils.CrankLog;

public class CrankListActivity extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CrankLog.v(this, "onCreate()");
	}

	@Override
	public void onStart() {
		super.onStart();
		CrankLog.v(this, "onStart()");
	}

	@Override
	public void onResume() {
		super.onResume();
		CrankLog.v(this, "onResume()");

	}

	@Override
	public void onPause() {
		super.onPause();
		System.gc();
		CrankLog.v(this, "onPause()");
	}

	@Override
	public void onStop() {
		super.onStop();
		CrankLog.v(this, "onStop()");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		CrankLog.v(this, "onDestroy()");
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		CrankLog.w(this, "onLowMemory()");
	}
}
