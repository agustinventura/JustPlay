package com.crankcode.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;

import com.crankcode.adapters.FileAdapter;
import com.crankcode.utils.MediaFileFilter;

public class FileExplorer extends ListActivity {

	private final String root = "/";

	private File position = null;

	private final List<File> filesInDir = new ArrayList<File>();

	private final MediaFileFilter mediaFileFilter = new MediaFileFilter();

	private File selected = null;

	private TextView myPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.restoreState(savedInstanceState);
		setContentView(R.layout.file_explorer);
		myPath = (TextView) findViewById(R.id.path);
		if (position == null) {
			getDir(root);
		} else {
			getDir(this.position.getPath());
		}
		registerForContextMenu(getListView());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("position", position);
		super.onSaveInstanceState(outState);
	}

	private void restoreState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("position")) {
				this.position = (File) savedInstanceState.get("position");
			}
		} else {
			SharedPreferences prefs = this.getSharedPreferences(
					"com.crankcode.FileExplorer", MODE_PRIVATE);
			if (prefs.contains("fileExplorerDirectory")) {
				this.position = new File(prefs.getString(
						"fileExplorerDirectory", ""));
			}
		}
	}

	private void getDir(String dirPath) {
		myPath.setText(getResources().getString(R.string.directory) + dirPath);
		filesInDir.clear();
		this.position = new File(dirPath);
		File[] files = position.listFiles(mediaFileFilter);
		Arrays.sort(files);
		filesInDir.addAll(Arrays.asList(files));
		filesInDir.add(0, new File("/"));
		filesInDir.add(1, position.getParentFile());

		FileAdapter fileAdapter = new FileAdapter(this, filesInDir);
		setListAdapter(fileAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File file = filesInDir.get(position);
		if (file.isDirectory()) {
			onDirectoryClick(position, file);
		} else {
			onFileClick(file);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		int selectedPosition = (int) info.id;
		File selectedFile = filesInDir.get(selectedPosition);
		if (selectedFile.isDirectory()) {
			super.onCreateContextMenu(menu, v, menuInfo);
			menu.setHeaderTitle(R.string.directory_contextual_title);
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.crankexplorer_context, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addDirectoryItem:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			this.selected = filesInDir.get(((int) info.id));
			if (this.selected.isDirectory()) {
				finish();
			}
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void onFileClick(File file) {
		this.selected = file;
		showAddSongDialog();
	}

	private void showAddSongDialog() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(this.getText(R.string.add_file));
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.yes, new OnClickListener() {
			public void onClick(DialogInterface dialog, int button) {
				dialog.cancel();
				finish();
			}
		});
		builder.setNegativeButton(R.string.no, new OnClickListener() {
			public void onClick(DialogInterface dialog, int button) {
				dialog.cancel();
			}
		});
		AlertDialog addFileDialog = builder.create();
		addFileDialog.show();
	}

	private void onDirectoryClick(int position, File file) {
		getDir(filesInDir.get(position).getAbsolutePath());
	}

	@Override
	public void finish() {
		Intent data = new Intent();
		this.saveCurrentDirectory();
		if (this.selected != null) {
			if (this.selected.isFile()) {
				data.putExtra("selectedFile", this.selected);
			} else if (this.selected.isDirectory()) {
				List<File> filesInDirectory = this.explodeDir(this.selected);
				data.putExtra("selectedFiles",
						(ArrayList<File>) filesInDirectory);
			}
		}
		setResult(RESULT_OK, data);
		super.finish();
	}

	private void saveCurrentDirectory() {
		if (this.selected != null) {
			SharedPreferences prefs = this.getSharedPreferences(
					"com.crankcode.FileExplorer", MODE_PRIVATE);
			Editor editor = prefs.edit();
			if (prefs.contains("fileExplorerDirectory")) {
				editor.remove("fileExplorerDirectory");
			}
			editor.putString("fileExplorerDirectory", this.selected.getParent());
			editor.commit();
		}
	}

	private List<File> explodeDir(File directory) {
		List<File> filesInDirectory = new ArrayList<File>();
		File[] files = directory.listFiles(mediaFileFilter);
		Arrays.sort(files);
		for (File file : files) {
			if (file.isFile()) {
				filesInDirectory.add(file);
			} else if (file.isDirectory()) {
				filesInDirectory.addAll(explodeDir(file));
			}
		}
		return filesInDirectory;
	}
}
