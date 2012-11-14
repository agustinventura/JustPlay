package com.crankcode.utils;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File> {

	public int compare(File first, File second) {
		int result = 0;
		if (first.isDirectory() && !second.isDirectory()) {
			result = -1;
		} else if (!first.isDirectory() && second.isDirectory()) {
			result = 1;
		} else {
			result = first.compareTo(second);
		}
		return result;
	}

}