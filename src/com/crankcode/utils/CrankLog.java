package com.crankcode.utils;

import android.util.Log;

public class CrankLog {

	private static String appName = "CrankPlayer";
	
	public static void v(Object source, String message) {
		Log.v(appName, source.getClass().getSimpleName() + " - " + message);
	}

	public static void d(Object source, String message) {
		Log.d(appName, source.getClass().getSimpleName() + " - " + message);
	}

	public static void i(Object source, String message) {
		Log.i(appName, source.getClass().getSimpleName() + " - " + message);
	}

	public static void w(Object source, String message) {
		Log.w(appName, source.getClass().getSimpleName() + " - " + message);
	}

	public static void e(Object source, String message) {
		Log.e(appName, source.getClass().getSimpleName() + " - " + message);
	}
}
