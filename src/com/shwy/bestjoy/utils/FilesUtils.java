package com.shwy.bestjoy.utils;

import java.io.File;

public class FilesUtils {

	/**
	 * �ݹ�ɾ��һ��Ŀ¼����Ŀ¼
	 * @param tag
	 * @param file
	 */
	public static void deleteFile(String tag, File file) {
		if (!file.exists()) {
			DebugUtils.logDeleteFiles(tag, "deleteFile " + file.getAbsolutePath() + " not exsit, just skip");
			return;
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for(File subFile:files) {
				deleteFile(tag, subFile);
			}
		} else {
			DebugUtils.logDeleteFiles(tag, "deleteFile " + file.getAbsolutePath());
			file.delete();
		}
	}
}
