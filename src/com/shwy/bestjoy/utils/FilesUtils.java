package com.shwy.bestjoy.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class FilesUtils {

	private static final String TAG = "FilesUtils";
	/**
	 * 递归删除文件
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
			boolean deleted = file.delete();
			DebugUtils.logDeleteFiles(tag, "deleteFile " + file.getAbsolutePath() + ", deleted = " +deleted);
		} else {
			boolean deleted = file.delete();
			DebugUtils.logDeleteFiles(tag, "deleteFile " + file.getAbsolutePath() + ", deleted = " +deleted);
		}
	}
	
	
	public static long UNIT_M = 1 * 1024 * 1024;
	public static long UNIT_K = 1 * 1024;
	public static String computeLengthToString(long length) {
		StringBuilder sb = new StringBuilder();
		if (length < UNIT_K) {
			sb.append(length).append('B');
		} else if (length < UNIT_M) {
			float len = 1.0f * length / UNIT_K;
			sb.append(Math.round(len)).append("KB");
		} else {
			float len = 1.0f * length / UNIT_M;
			sb.append(Math.round(len)).append("MB");
		}
		return sb.toString();
	}
	/**
	 * 将输入流保存为文件
	 * @param src
	 * @param out
	 * @return
	 */
	public static boolean saveFile(InputStream src, File out) {
		 boolean success = true;
		  FileOutputStream fos = null;
		  try {
			  fos = new FileOutputStream(out);
			  byte[] buffer = new byte[8192];
			  int count = 0;
			  while ((count = src.read(buffer)) > 0) {
				  fos.write(buffer, 0, count);
			  }
			  fos.flush();
			  DebugUtils.logD(TAG, "start to save File " + out.getAbsolutePath());
		  } catch (IOException e) {
				e.printStackTrace();
				success = false;
		  } finally {
			  NetworkUtils.closeOutStream(fos);
		  }
		  DebugUtils.logD(TAG, "saveFile to " + out.getAbsolutePath() + " success? " + success);
		 return success;
	  }
}
