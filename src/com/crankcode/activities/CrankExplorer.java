package com.crankcode.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.crankcode.utils.MediaFileFilter;

public class CrankExplorer extends ListActivity {

	private List<String> item = null;

	private List<String> path = null;

	private final String root = "/";

	private TextView myPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crankexplorer);
		myPath = (TextView) findViewById(R.id.path);
		getDir(root);
	}

	private void getDir(String dirPath) {
		myPath.setText("Location: " + dirPath);
		item = new ArrayList<String>();
		path = new ArrayList<String>();
		File f = new File(dirPath);
		File[] files = f.listFiles(new MediaFileFilter());
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

	private void onFileClick(File file) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(this.getText(R.string.add_file));
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.yes, new OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO: send intent with mp3 path to CrankPlayer
			}

		});
		AlertDialog addFileDialog = builder.create();
		addFileDialog.show();
	}

	private void onDirectoryClick(int position, File file) {
		getDir(path.get(position));
	}
}
