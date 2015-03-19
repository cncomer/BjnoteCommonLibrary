package com.shwy.bestjoy.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
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
		if (file == null) {
			DebugUtils.logDeleteFiles(tag, "deleteFile file is null");
			return;
		}
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
		File dir = out.getParentFile();
		if (!dir.exists()) {
			boolean created = dir.mkdirs();
			DebugUtils.logD(TAG, "saveFile mkdir " + dir.getAbsolutePath() + ", created="+created);
		}
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
		  } catch (IOException e) {
				e.printStackTrace();
				success = false;
		  } finally {
			  NetworkUtils.closeOutStream(fos);
		  }
		  DebugUtils.logD(TAG, "saveFile to " + out.getAbsolutePath() + " success? " + success);
		 return success;
	  }
	
	public static boolean installDatabaseFiles(Context context, String fileName, String ext, String extReplace) {
		 File file = context.getDatabasePath(fileName + extReplace);
		 boolean success = true;
		 if(file.exists() || file.isDirectory()) {
			 boolean deleted = file.delete();
			  if (deleted) {
				  DebugUtils.logD(TAG, "delete exsited  " + fileName);
			  }
		 }
			  
		  DebugUtils.logD(TAG, "start to install DatabaseFiles " + fileName);
		  file.getParentFile().mkdirs();
		  InputStream is = null;
		  FileOutputStream fos = null;
		  try {
			  is = context.getResources().getAssets().open(fileName + ext);
			  fos = new FileOutputStream(file);
			  byte[] buffer = new byte[8192];
			  int count = 0;
			  while ((count = is.read(buffer)) > 0) {
				  fos.write(buffer, 0, count);
			  }
			  fos.flush();
		  } catch (IOException e) {
				e.printStackTrace();
				success = false;
		  } finally {
			  NetworkUtils.closeInputStream(is);
			  NetworkUtils.closeOutStream(fos);
		  }
		  DebugUtils.logD(TAG, "install " + fileName + " success? " + success);
		 return success;
	  }
	
	public static boolean installFiles(File src, File out) {
		 boolean success = true;
		  DebugUtils.logD(TAG, "start to install File " + src.getAbsolutePath());
		  InputStream is = null;
		  FileOutputStream fos = null;
		  try {
			  is = new FileInputStream(src);
			  fos = new FileOutputStream(out);
			  byte[] buffer = new byte[8192];
			  int count = 0;
			  while ((count = is.read(buffer)) > 0) {
				  fos.write(buffer, 0, count);
			  }
			  fos.flush();
			  NetworkUtils.closeOutStream(fos);
			  DebugUtils.logD(TAG, "start to save File " + out.getAbsolutePath());
		  } catch (IOException e) {
				e.printStackTrace();
				success = false;
		  } finally {
			  NetworkUtils.closeInputStream(is);
		  }
		  DebugUtils.logD(TAG, "install " + src.getAbsolutePath() + " success? " + success);
		 return success;
	  }

    /**accounts目录*/
    public static final String DIR_ACCOUNTS_ROOT = "accounts";

    /**accounts/cache目录*/
    public static final String DIR_ACCOUNTS_CACHE = "cache";

    /**accounts/files目录*/
    public static final String DIR_ACCOUNTS_FILE = "files";
    
    /**cache目录*/
    public static final String DIR_CACHE = "cache";

    /**files目录*/
    public static final String DIR_FILES = "files";
    
    
    /**json文件*/
    public static final String SUFFIX_JSON = ".json";
    /**xml文件*/
    public static final String SUFFIX_XML = ".xml";
}
