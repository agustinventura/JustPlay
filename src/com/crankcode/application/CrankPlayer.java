package com.crankcode.application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "dENJRGczTmdGdy1SbXVnV2pGNXNWdWc6MQ")
public class CrankPlayer extends Application {

	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();
	}

}
