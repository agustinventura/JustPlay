package com.crankcode.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.crankcode.adapters.SongAdapter;
import com.crankcode.services.MediaService;
import com.crankcode.services.binders.MediaServiceBinder;
import com.crankcode.threads.MediaThread;
import com.crankcode.utils.CrankLog;
import com.crankcode.utils.ID3Reader;
import com.crankcode.utils.MediaStatus;

public class MediaPlayer extends CrankListActivity {

	private final static int REQUEST_CODE = 101;
	private final List<File> playlist = new ArrayList<File>();
	private final ID3Reader id3Reader = new ID3Reader();
	private MediaServiceBinder mediaServiceBinder = null;
	private MediaThread mediaThread = null;
	private int song = 0;
	private MediaStatus status = MediaStatus.STOPPED;
	private final ServiceConnection mediaServiceConnection = new MediaServiceConnection();
	private final ErrorDialogClickListener errorDialogClickListener = new ErrorDialogClickListener();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.crankplayer);
		Intent intent = new Intent(getBaseContext(), MediaService.class);
		this.startService(intent);
		this.registerForContextMenu(getListView());
		this.restoreState(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		Intent intent = new Intent(this, MediaService.class);
		this.bindService(intent, mediaServiceConnection,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onResume() {
		super.onResume();
		this.updateStatus();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		this.unbindService(mediaServiceConnection);
		super.onStop();
	}

	@Override
	public void onDestroy() {
		if (!this.status.equals(MediaStatus.PLAYING)
				&& !this.status.equals(MediaStatus.PAUSED)) {
			Intent intent = new Intent(getBaseContext(), MediaService.class);
			this.stopService(intent);
		}
		this.saveStateToFile();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("playlist", (Serializable) this.playlist);
		outState.putInt("song", this.song);
		outState.putSerializable("status", this.status);
		super.onSaveInstanceState(outState);
	}

	private void saveStateToFile() {
		try {
			FileOutputStream fos = this.openFileOutput(
					"crankplayer_playlist.cpl", MODE_PRIVATE);
			PrintWriter pw = new PrintWriter(fos);
			for (File song : this.playlist) {
				pw.println(song.getPath());
			}
			pw.close();
			fos.close();
		} catch (IOException e) {
			CrankLog.e(
					this,
					"Not able to write playlist to a file: "
							+ e.getLocalizedMessage());
		}
	}

	private void restoreState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("playlist")) {
				List<File> savedPlaylist = (List<File>) savedInstanceState
						.get("playlist");
				int savedSong = savedInstanceState.getInt("song");
				MediaStatus savedStatus = (MediaStatus) savedInstanceState
						.get("status");
				if (!savedPlaylist.isEmpty()) {
					this.playlist.addAll(savedPlaylist);
					this.song = savedSong;
					this.status = savedStatus;
				}
			}
		} else {
			this.readStateFromFile();
		}
		this.renderPlaylist();
	}

	private void readStateFromFile() {
		try {
			FileInputStream fis = this
					.openFileInput("crankplayer_playlist.cpl");
			InputStreamReader inputStreamReader = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(inputStreamReader);
			String path = null;
			while ((path = br.readLine()) != null) {
				File song = new File(path);
				this.playlist.add(song);
			}
			br.close();
			inputStreamReader.close();
			fis.close();
			File deletePlaylist = new File("crankplayer_playlist.cpl");
			deletePlaylist.delete();
		} catch (FileNotFoundException e) {
			CrankLog.w(this, "Playlist recovery from unexistent file failed");
		} catch (IOException e) {
			CrankLog.e(
					this,
					"Not able to read playlist from file: "
							+ e.getLocalizedMessage());
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		this.mediaThread.play(position);
		this.updateStatus();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.crankplayer_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.playSong:
			this.mediaThread.play((int) info.id);
			this.updateStatus();
			return true;
		case R.id.removeSong:
			int songToRemove = (int) info.id;
			this.removeSong(songToRemove);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void removeSong(int songToRemove) {
		this.playlist.remove(songToRemove);
		this.mediaThread.getPlaylist().remove(songToRemove);
		this.renderPlaylist();
	}

	public void openCrankExplorer(View v) {
		Intent openCrankExplorer = new Intent(this, FileExplorer.class);
		this.startActivityForResult(openCrankExplorer, REQUEST_CODE);
	}

	public void clearPlaylist(View v) {
		this.playlist.clear();
		this.mediaThread.clearPlaylist();
		this.song = 0;
		this.renderPlaylist();
	}

	public void play(View v) {
		this.mediaThread.play();
		this.updateStatus();
	}

	public void stop(View v) {
		this.mediaThread.stopPlayback();
		this.updateStatus();
	}

	public void pause(View v) {
		if (this.status.equals(MediaStatus.PLAYING)) {
			this.mediaThread.pause();
		} else {
			this.mediaThread.play(this.song);
		}
		this.updateStatus();
	}

	public void previousSong(View v) {
		this.mediaThread.previousSong();
		this.updateStatus();
	}

	public void nextSong(View v) {
		this.mediaThread.nextSong();
		this.updateStatus();
	}

	public void rewind(View v) {
		this.mediaThread.rewind();
		this.updateStatus();
	}

	public void fastforward(View v) {
		this.mediaThread.fastforward();
		this.updateStatus();
	}

	private void renderPlaylist() {
		SongAdapter fileList = new SongAdapter(this.getBaseContext(),
				this.playlist, this.id3Reader);
		this.setListAdapter(fileList);
	}

	private void updateStatus() {
		if (this.mediaThread != null) {
			this.song = this.mediaThread.getSong();
			this.status = this.mediaThread.getStatus();
		} else {
			this.song = 0;
			this.status = MediaStatus.STOPPED;
		}
		this.renderCurrentSongInfo();
		if (this.status.equals(MediaStatus.PLAYING)) {
			this.renderPauseButton();
		} else if (this.status.equals(MediaStatus.STOPPED)) {
			this.renderPlayButton();
		}
	}

	private void renderPauseButton() {
		ImageButton pauseButton = (ImageButton) findViewById(R.id.pause);
		pauseButton.setVisibility(View.VISIBLE);
		ImageButton playButton = (ImageButton) findViewById(R.id.play);
		playButton.setVisibility(View.GONE);
	}

	private void renderPlayButton() {
		ImageButton pauseButton = (ImageButton) findViewById(R.id.pause);
		pauseButton.setVisibility(View.GONE);
		ImageButton playButton = (ImageButton) findViewById(R.id.play);
		playButton.setVisibility(View.VISIBLE);
	}

	private void renderQuotes(boolean show) {
		ImageView leftQuote = (ImageView) findViewById(R.id.leftQuote);
		ImageView rightQuote = (ImageView) findViewById(R.id.rightQuote);
		if (show) {
			leftQuote.setVisibility(View.VISIBLE);
			rightQuote.setVisibility(View.VISIBLE);
		} else {
			leftQuote.setVisibility(View.GONE);
			rightQuote.setVisibility(View.GONE);
		}
	}

	private void renderCurrentSongInfo() {
		TextView currentSongView = (TextView) findViewById(R.id.current_song_info);
		if (this.status.equals(MediaStatus.PLAYING)) {
			this.renderQuotes(true);
			currentSongView.setText(this.id3Reader.procesar(this.playlist
					.get(this.song)));
		} else if (this.status.equals(MediaStatus.PAUSED)) {
			this.renderQuotes(true);
			currentSongView.setText(getText(R.string.paused) + " - "
					+ this.id3Reader.procesar(this.playlist.get(this.song)));
		} else if (this.status.equals(MediaStatus.STOPPED)) {
			this.renderQuotes(false);
			currentSongView.setText(R.string.stopped);
		} else if (this.status.equals(MediaStatus.ERROR)) {
			this.renderQuotes(false);
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
		builder.setPositiveButton(R.string.yes, this.errorDialogClickListener);
		builder.setNegativeButton(R.string.no, this.errorDialogClickListener);
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

	private final class MediaServiceConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName className, IBinder service) {
			if (mediaServiceBinder == null || mediaThread == null) {
				mediaServiceBinder = (MediaServiceBinder) service;
				mediaThread = mediaServiceBinder.getMediaService()
						.getMediaThread();
				status = mediaThread.getStatus();
				if (mediaThread.getPlaylist().isEmpty() && !playlist.isEmpty()) {
					mediaThread.getPlaylist().addAll(playlist);
				}
				updateStatus();
				renderCurrentSongInfo();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			mediaServiceBinder = null;
			mediaThread = null;
		}
	}

	private final class ErrorDialogClickListener implements OnClickListener {
		public void onClick(DialogInterface dialog, int button) {
			switch (button) {
			case -1:
				// -1 stands for "remove song" or positive button
				removeSong(song);
				status = MediaStatus.STOPPED;
				renderCurrentSongInfo();
				break;
			case -2:
				// -2 stands for "dismiss" or negative button
				dialog.cancel();
				break;
			}

		}
	}
}