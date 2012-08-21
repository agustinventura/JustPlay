package com.crankcode.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ID3Reader {

	public ID3Reader() {

	}

	public String procesar(File mp3File) {
		StringBuilder artistAndTitle = new StringBuilder("");
		try {
			FileInputStream in = new FileInputStream(mp3File);
			int fileSize = (int) mp3File.length();
			in.skip(fileSize - 128);
			byte[] tagBytes = new byte[128];
			in.read(tagBytes, 0, tagBytes.length);
			String id3 = new String(tagBytes);
			String tag = id3.substring(0, 3);
			if (tag.equals("TAG")) {
				String artist = id3.substring(33, 62).trim();
				String title = id3.substring(3, 32).trim();
				if (!"".equals(artist) && !"".equals(title)) {
					artistAndTitle.append(artist).append(" - ").append(title);
				} else {
					artistAndTitle.append(mp3File.getName());
				}
			} else {
				artistAndTitle.append(mp3File.getName());
			}
			in.close();
		} catch (IOException e) {
			Logger.e(this, "procesar() - " + e.getLocalizedMessage());
			artistAndTitle.append(mp3File.getName());
		}
		return artistAndTitle.toString();
	}

	public List<String> procesar(List<File> mp3Files) {
		List<String> artistsAndTitles = new ArrayList<String>();
		for (File mp3File : mp3Files) {
			artistsAndTitles.add(this.procesar(mp3File));
		}
		return artistsAndTitles;
	}
}
