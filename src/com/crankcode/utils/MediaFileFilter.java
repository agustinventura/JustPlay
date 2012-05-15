package com.crankcode.utils;

import java.io.File;
import java.io.FilenameFilter;

public class MediaFileFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		boolean valido = false;
		File file = new File(dir.getAbsoluteFile() + File.separator + name);
		if (file.canRead() && !file.isHidden()) {
			if (name.endsWith(".mp3")) {
				valido = true;
			} else if (name.endsWith(".ogg")) {
				valido = true;
			} else if (file.isDirectory()) {
				valido = true;
			}
		}

		return valido;
	}
}
