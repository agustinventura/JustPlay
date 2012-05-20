package com.crankcode.threads;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.crankcode.utils.CrankLog;

public class MediaThread extends Thread {

	private MediaPlayer mediaPlayer;
	private List<File> playlist = new ArrayList<File>();
	private int song = 0;
	private boolean pause = false;

	@Override
	public void run() {
		CrankLog.v(this, "run()");
		this.setMediaPlayer(new MediaPlayer());
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}

	public List<File> getPlaylist() {
		return playlist;
	}

	public void setPlaylist(List<File> playlist) {
		this.playlist = playlist;
	}

	public void play() {
		this.stopPlayback();
		if (!this.playlist.isEmpty()) {
			this.play(this.playlist.get(song));
		}
	}

	private void play(File file) {
		try {
			if (!this.pause) {
				if (this.mediaPlayer.isPlaying()) {
					this.mediaPlayer.stop();
				}
				this.mediaPlayer.reset();
				this.mediaPlayer.setDataSource(file.getAbsolutePath());
				this.mediaPlayer.prepare();
				this.mediaPlayer.start();
				this.mediaPlayer
						.setOnCompletionListener(new OnCompletionListener() {
							public void onCompletion(MediaPlayer arg0) {
								nextSong();
							}
						});
			} else {
				this.mediaPlayer.start();
			}
			this.pause = false;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopPlayback() {
		if (this.mediaPlayer.isPlaying()) {
			this.mediaPlayer.stop();
			this.song = 0;
		}
	}

	public void nextSong() {
		// Check if last song or not
		++this.song;
		if (this.song >= this.playlist.size()) {
			this.song = 0;
		}
		this.pause = false;
		play(this.playlist.get(this.song));
	}

	public void previousSong() {
		if (this.song > 0) {
			--this.song;
		}
		this.pause = false;
		play(this.playlist.get(this.song));
	}

	public void end() {
		this.stopPlayback();
		this.playlist.clear();
		this.mediaPlayer.release();
		// We make sure playlist and mediaPlayer are eligible for garbage
		// collection
		this.playlist = null;
		this.mediaPlayer = null;
	}

	public void pause() {
		this.pause = true;
		this.mediaPlayer.pause();
	}

}
