package com.crankcode.services.binders;

import java.io.File;
import java.util.List;

import android.os.Binder;

import com.crankcode.services.MediaService;

public class MediaServiceBinder extends Binder {

	private final MediaService mediaService;

	public MediaServiceBinder(MediaService mediaService) {
		this.mediaService = mediaService;
	}

	public void play() {
		this.mediaService.play();
	}

	public void stop() {
		this.mediaService.stop();
	}

	public void addToPlaylist(File selectedFile) {
		this.mediaService.getPlaylist().add(selectedFile);
	}

	public void addToPlaylist(List<File> selectedFiles) {
		this.mediaService.getPlaylist().addAll(selectedFiles);
	}

	public void clearPlaylist() {
		this.mediaService.getPlaylist().clear();
	}

	public void previousSong() {
		this.mediaService.previousSong();
	}

	public void nextSong() {
		this.mediaService.nextSong();
	}
}
