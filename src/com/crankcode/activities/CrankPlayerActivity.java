package com.crankcode.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ArrayAdapter;

import com.crankcode.services.MediaService;
import com.crankcode.services.binders.MediaServiceBinder;

public class CrankPlayerActivity extends CrankListActivity {
	private final static int REQUEST_CODE = 101;
	private final List<File> playlist = new ArrayList<File>();
	private MediaServiceBinder mediaServiceBinder = null;

	private final ServiceConnection mediaServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mediaServiceBinder = (MediaServiceBinder) service;
		}

		public void onServiceDisconnected(ComponentName className) {
			mediaServiceBinder = null;
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crankplayer);
		Intent intent = new Intent(getBaseContext(), MediaService.class);
		startService(intent);
		this.renderPlaylist();
	}

	@Override
	public void onStart() {
		super.onStart();
		Intent intent = new Intent(this, MediaService.class);
		bindService(intent, mediaServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		unbindService(mediaServiceConnection);
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Intent intent = new Intent(getBaseContext(), MediaService.class);
		stopService(intent);
		super.onDestroy();
	}

	public void openCrankExplorer(View v) {
		Intent openCrankExplorer = new Intent(this, CrankExplorer.class);
		startActivityForResult(openCrankExplorer, REQUEST_CODE);
	}

	public void clearPlaylist(View v) {
		this.playlist.clear();
		this.mediaServiceBinder.clearPlaylist();
		renderPlaylist();
	}

	public void play(View v) {
		this.mediaServiceBinder.play();
	}

	public void stop(View v) {
		this.mediaServiceBinder.stop();
	}

	public void previousSong(View v) {
		this.mediaServiceBinder.previousSong();
	}

	public void nextSong(View v) {
		this.mediaServiceBinder.nextSong();
	}

	private void renderPlaylist() {
		ArrayAdapter<File> fileList = new ArrayAdapter<File>(this,
				R.layout.file_row, playlist);
		setListAdapter(fileList);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data.hasExtra("selectedFile")) {
			File selectedFile = (File) data.getExtras().get("selectedFile");
			this.playlist.add(selectedFile);
			this.renderPlaylist();
			if (this.mediaServiceBinder != null) {
				this.mediaServiceBinder.addToPlaylist(selectedFile);
			}
		} else if (data.hasExtra("selectedFiles")) {
			List<File> selectedFiles = (List<File>) data.getExtras().get(
					"selectedFiles");
			this.playlist.addAll(selectedFiles);
			if (this.mediaServiceBinder != null) {
				this.mediaServiceBinder.addToPlaylist(selectedFiles);
			}
			this.renderPlaylist();
		}
	}
}