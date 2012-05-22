package com.crankcode.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		this.song = position;
		this.status = this.mediaThread.play(this.song);
		this.renderCurrentSongInfo();
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
		this.status = this.mediaThread.play();
		this.renderCurrentSongInfo();
	}

	public void stop(View v) {
		this.status = this.mediaThread.stopPlayback();
		this.song = this.mediaThread.getSong();
		this.renderCurrentSongInfo();
	}

	public void pause(View v) {
		if (this.status.equals(MediaStatus.PLAYING)) {
			this.status = this.mediaThread.pause();
		} else {
			this.status = this.mediaThread.play(this.song);
		}
		this.renderCurrentSongInfo();
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
		if (this.status.equals(MediaStatus.PLAYING)) {
			currentSongView.setText(this.playlist.get(this.song).getName());
		} else if (this.status.equals(MediaStatus.PAUSED)) {
			currentSongView.setText(getText(R.string.pause) + " - "
					+ this.playlist.get(this.song).getName());
		} else if (this.status.equals(MediaStatus.STOPPED)) {
			currentSongView.setText(R.string.stop);
		} else if (this.status.equals(MediaStatus.ERROR)) {
			this.showErrorDialog();
		}
	}

	private void showErrorDialog() {
		Builder builder = new AlertDialog.Builder(this);
		String errorMessage = this.getText(R.string.play_error).toString();
		errorMessage = errorMessage.replace("##songName##",
				this.playlist.get(this.song).getName());
		builder.setMessage(errorMessage);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.yes, new OnClickListener() {
			public void onClick(DialogInterface dialog, int button) {
				playlist.remove(song);
				mediaThread.getPlaylist().remove(song);
				status = MediaStatus.STOPPED;
				renderCurrentSongInfo();
				renderPlaylist();
			}
		});
		builder.setNegativeButton(R.string.no, new OnClickListener() {
			public void onClick(DialogInterface dialog, int button) {
				dialog.cancel();
			}
		});
		AlertDialog errorDialog = builder.create();
		errorDialog.show();
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