package com.crankcode.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.crankcode.services.MediaService;

public class CrankPlayerActivity extends CrankListActivity {
	private final static int REQUEST_CODE = 101;
	private final List<File> playlist = new ArrayList<File>();

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
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Intent intent = new Intent(getBaseContext(), MediaService.class);
		stopService(intent);
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	public void openCrankExplorer(View v) {
		Intent openCrankExplorer = new Intent(this, CrankExplorer.class);
		startActivityForResult(openCrankExplorer, REQUEST_CODE);
	}

	private void renderPlaylist() {
		ArrayAdapter<File> fileList = new ArrayAdapter<File>(this,
				R.layout.file_row, playlist);
		setListAdapter(fileList);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data.hasExtra("selectedFile")) {
			this.playlist.add((File) data.getExtras().get("selectedFile"));
			this.renderPlaylist();
		}
	}
}