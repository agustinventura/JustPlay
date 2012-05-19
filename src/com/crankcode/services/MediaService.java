package com.crankcode.services;

import java.io.File;
import java.util.List;

import android.content.Intent;
import android.os.IBinder;

import com.crankcode.services.binders.MediaServiceBinder;
import com.crankcode.threads.MediaThread;

public class MediaService extends CrankService {

	private MediaThread mediaThread;
	private final IBinder mediaServiceBinder = new MediaServiceBinder(this);

	@Override
	public void onCreate() {
		super.onCreate();
		this.mediaThread = new MediaThread();
		this.mediaThread.start();
	}

	@Override
	public void onDestroy() {
		this.mediaThread.end();
		this.mediaThread.interrupt();
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent bindingIntent) {
		return this.mediaServiceBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// Nothing to do here
		return super.onUnbind(intent);
	}

	public List<File> getPlaylist() {
		return this.mediaThread.getPlaylist();
	}

	public void setPlaylist(List<File> playlist) {
		this.mediaThread.setPlaylist(playlist);
	}

	public void play() {
		this.mediaThread.play();
	}

	public void stop() {
		this.mediaThread.stopPlayback();
	}

	public void previousSong() {
		this.mediaThread.previousSong();
	}

	public void nextSong() {
		this.mediaThread.nextSong();
	}

}
