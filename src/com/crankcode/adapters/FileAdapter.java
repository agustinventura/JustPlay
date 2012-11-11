package com.crankcode.adapters;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crankcode.activities.R;

public class FileAdapter extends BaseAdapter {

	private final List<File> files;
	private final Context context;

	public FileAdapter(Context context, List<File> files) {
		this.context = context;
		this.files = files;
	}

	public int getCount() {
		return files.size();
	}

	public Object getItem(int position) {
		return this.files.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parentView) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.file_row, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.fileName);
			viewHolder.image = (ImageView) rowView.findViewById(R.id.fileImage);
			rowView.setTag(viewHolder);
		}
		ViewHolder holder = (ViewHolder) rowView.getTag();
		switch (position) {
		case 0:
			// Special case, it is root
			holder.text.setText(R.string.root_folder);
			holder.image.setImageResource(R.drawable.ic_folder);
			break;
		case 1:
			// Special case, it is parent folder
			holder.text.setText(R.string.parent_folder);
			holder.image.setImageResource(R.drawable.ic_folder);
			break;
		default:
			File file = this.files.get(position);
			holder.text.setText(file.getName());
			if (file.isDirectory()) {
				holder.image.setImageResource(R.drawable.ic_folder);
			} else {
				holder.image.setImageResource(R.drawable.ic_music);
			}
			break;
		}
		return rowView;
	}

	private class ViewHolder {
		TextView text;
		ImageView image;
	}
}
