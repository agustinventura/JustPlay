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
import android.widget.TextView;

import com.crankcode.services.MediaService;
import com.crankcode.services.binders.MediaServiceBinder;
import com.crankcode.threads.MediaThread;
import com.crankcode.utils.MediaStatus;

public class CrankPlayerActivity extends CrankListActivity {

	private final static int REQUEST_CODE = 101;
	private final List<File> playlist = new ArrayList<File>();
	private MediaServiceBinder mediaServiceBinder = null;
	private MediaThread mediaThread = null;
	private int song = 0;
	private MediaStatus status = null;

	private final ServiceConnection mediaServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			if (mediaServiceBinder == null || mediaThread == null) {
				mediaServiceBinder = (MediaServiceBinder) service;
				mediaThread = mediaServiceBinder.getMediaService()
						.getMediaThread();
				status = mediaThread.getStatus();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			mediaServiceBinder = null;
			mediaThread = null;
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
		if (!this.status.equals(MediaStatus.PLAYING)) {
			Intent intent = new Intent(getBaseContext(), MediaService.class);
			stopService(intent);
		}
		super.onDestroy();
	}

	public void openCrankExplorer(View v) {
		Intent openCrankExplorer = new Intent(this, CrankExplorer.class);
		startActivityForResult(openCrankExplorer, REQUEST_CODE);
	}

	public void clearPlaylist(View v) {
		this.playlist.clear();
		this.mediaThread.getPlaylist().clear();
		renderPlaylist();
	}

	public void play(View v) {
		this.mediaThread.play();
		this.status = this.mediaThread.getStatus();
		// We need to stablish here the current song info
		this.renderCurrentSongInfo();
	}

	public void stop(View v) {
		this.mediaThread.stopPlayback();
		this.status = this.mediaThread.getStatus();
		this.song = this.mediaThread.getSong();
		TextView currentSongView = (TextView) findViewById(R.id.current_song_info);
		currentSongView.setText("");
	}

	public void pause(View v) {
		this.mediaThread.pause();
		this.status = this.mediaThread.getStatus();
		TextView currentSongView = (TextView) findViewById(R.id.current_song_info);
		currentSongView.setText(currentSongView.getText() + " - "
				+ getText(R.string.pause));
	}

	public void previousSong(View v) {
		this.mediaThread.previousSong();
		this.renderCurrentSongInfo();
	}

	public void nextSong(View v) {
		this.mediaThread.nextSong();
		this.renderCurrentSongInfo();
	}

	public void rewind(View v) {
		this.mediaThread.rewind();
		this.renderCurrentSongInfo();
	}

	public void fastforward(View v) {
		this.mediaThread.fastforward();
		this.renderCurrentSongInfo();
	}

	private void renderPlaylist() {
		ArrayAdapter<File> fileList = new ArrayAdapter<File>(this,
				R.layout.file_row, playlist);
		setListAdapter(fileList);
	}

	private void renderCurrentSongInfo() {
		this.song = this.mediaThread.getSong();
		TextView currentSongView = (TextView) findViewById(R.id.current_song_info);
		currentSongView.setText(this.playlist.get(this.song).getName());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data.hasExtra("selectedFile")) {
			File selectedFile = (File) data.getExtras().get("selectedFile");
			this.playlist.add(selectedFile);
			this.renderPlaylist();
			if (this.mediaServiceBinder != null) {
				this.mediaThread.getPlaylist().add(selectedFile);
			}
		} else if (data.hasExtra("selectedFiles")) {
			List<File> selectedFiles = (List<File>) data.getExtras().get(
					"selectedFiles");
			this.playlist.addAll(selectedFiles);
			if (this.mediaServiceBinder != null) {
				this.mediaThread.getPlaylist().addAll(selectedFiles);
			}
			this.renderPlaylist();
		}
	}
}