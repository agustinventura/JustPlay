package com.crankcode.adapters;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.crankcode.activities.R;
import com.crankcode.utils.ID3Reader;

public class SongAdapter extends BaseAdapter {

	private final List<File> songs;
	private final ID3Reader id3Reader;
	private final Context context;

	public SongAdapter(Context context, List<File> songs, ID3Reader id3Reader) {
		this.context = context;
		this.songs = songs;
		this.id3Reader = id3Reader;
	}

	public int getCount() {
		return songs.size();
	}

	public Object getItem(int position) {
		return this.songs.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parentView) {
		TextView text = null;
		LayoutInflater inflater = null;
		File song = null;
		if (convertView == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater
					.inflate(R.layout.file_row, parentView, false);
			text = (TextView) convertView.findViewById(R.id.fileRow);
			song = this.songs.get(position);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = text;
			convertView.setTag(viewHolder);
		} else {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			text = viewHolder.text;
		}

		if (song != null) {
			text.setText(this.id3Reader.procesar(song));
		}
		return convertView;
	}

	private class ViewHolder {
		TextView text;
	}

}
