package com.crankcode.services.binders;

import android.os.Binder;

import com.crankcode.services.MediaService;

public class MediaServiceBinder extends Binder {

	private final MediaService mediaService;

	public MediaServiceBinder(MediaService mediaService) {
		this.mediaService = mediaService;
	}
}
