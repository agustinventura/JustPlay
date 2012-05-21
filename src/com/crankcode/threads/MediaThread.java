package com.crankcode.threads;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.crankcode.utils.CrankLog;
import com.crankcode.utils.MediaStatus;

public class MediaThread extends Thread {

	private MediaPlayer mediaPlayer;
	private List<File> playlist = new ArrayList<File>();
	private int song = 0;
	private MediaStatus status = MediaStatus.STOPPED;
	private final int seekMiliseconds = 15000;

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

	public int getSong() {
		return this.song;
	}

	public MediaStatus getStatus() {
		return this.status;
	}

	public MediaStatus play() {
		this.stopPlayback();
		if (!this.playlist.isEmpty()) {
			this.play(this.playlist.get(song));
		}
		return this.status;
	}

	private MediaStatus play(File file) {
		try {
			if (!this.status.equals(MediaStatus.PAUSED)) {
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
			this.status = MediaStatus.PLAYING;
		} catch (IllegalArgumentException e) {
			CrankLog.e(this, "Error on play(): " + e.getLocalizedMessage());
			this.status = MediaStatus.ERROR;
		} catch (IllegalStateException e) {
			CrankLog.e(this, "Error on play(): " + e.getLocalizedMessage());
			this.status = MediaStatus.ERROR;
		} catch (IOException e) {
			CrankLog.e(this, "Error on play(): " + e.getLocalizedMessage());
			this.status = MediaStatus.ERROR;
		}
		return this.status;
	}

	public MediaStatus stopPlayback() {
		if (this.mediaPlayer.isPlaying()) {
			this.mediaPlayer.stop();
			this.song = 0;
			this.status = MediaStatus.STOPPED;
		}

		return this.status;
	}

	public MediaStatus nextSong() {
		// Check if last song or not
		++this.song;
		if (this.song >= this.playlist.size()) {
			this.song = 0;
		}
		return play(this.playlist.get(this.song));
	}

	public MediaStatus previousSong() {
		if (this.song > 0) {
			--this.song;
		}
		return play(this.playlist.get(this.song));
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

	public MediaStatus pause() {
		this.status = MediaStatus.PAUSED;
		this.mediaPlayer.pause();
		return this.status;
	}

	public MediaStatus rewind() {
		int songPosition = this.mediaPlayer.getCurrentPosition();
		if ((songPosition - this.seekMiliseconds) < 0) {
			this.previousSong();
		} else {
			this.mediaPlayer.seekTo(this.mediaPlayer.getCurrentPosition()
					- this.seekMiliseconds);
		}
		return this.status;
	}

	public MediaStatus fastforward() {
		int songPosition = this.mediaPlayer.getCurrentPosition();
		if ((songPosition + this.seekMiliseconds) > this.mediaPlayer
				.getDuration()) {
			this.nextSong();
		} else {
			this.mediaPlayer.seekTo(this.mediaPlayer.getCurrentPosition()
					+ this.seekMiliseconds);
		}
		return this.status;
	}

}
