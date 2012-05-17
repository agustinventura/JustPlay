package com.crankcode.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.crankcode.utils.MediaFileFilter;

public class CrankExplorer extends CrankListActivity {

	private List<String> item = null;

	private List<String> path = null;

	private final String root = "/";

	private final MediaFileFilter mediaFileFilter = new MediaFileFilter();

	private File selected = null;

	private TextView myPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crankexplorer);
		myPath = (TextView) findViewById(R.id.path);
		getDir(root);
		registerForContextMenu(getListView());
	}

	private void getDir(String dirPath) {
		myPath.setText(getResources().getString(R.string.directory) + dirPath);
		item = new ArrayList<String>();
		path = new ArrayList<String>();
		File f = new File(dirPath);
		File[] files = f.listFiles(mediaFileFilter);
		if (!dirPath.equals(root)) {
			item.add(root);
			path.add(root);
			item.add("../");
			path.add(f.getParent());
		}

		for (File file : files) {
			path.add(file.getPath());
			if (file.isDirectory()) {
				item.add(file.getName() + "/");
			} else {
				item.add(file.getName());
			}
		}

		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this,
				R.layout.file_row, item);
		setListAdapter(fileList);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File file = new File(path.get(position));
		if (file.isDirectory()) {
			onDirectoryClick(position, file);
		} else {
			onFileClick(file);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(R.string.directory_contextual_title);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.crankexplorer_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addDirectoryItem:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			this.selected = new File(this.path.get((int) info.id));
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
		getDir(path.get(position));
	}

	@Override
	public void finish() {
		Intent data = new Intent();
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

	private List<File> explodeDir(File directory) {
		List<File> filesInDirectory = new ArrayList<File>();
		File[] files = directory.listFiles(mediaFileFilter);
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
