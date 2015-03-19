package com.shwy.bestjoy.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;

public class DeviceStorageUtils {
	private static final String TAG = "DeviceStorageUtils";
	private static DeviceStorageUtils INSTANCE = new DeviceStorageUtils();
	private Context mContext;
	private File mRootDir;
	public static final long MIN_THRESHOLD = 1024 * 1024 * 10;
	
	public static class PartitionCapacityStat {
		public int mBlockSize;
		public int mTotalBlockCount;
		public long mTotalSize;
		public long mAvailableSize;
		public int mAvailableBlockCount;
	}

	private DeviceStorageUtils(){
		mRootDir = Environment.getExternalStorageDirectory();
	};
	
	public static DeviceStorageUtils getInstance() {
		return INSTANCE;
	}
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	public boolean isExternalStorageMounted() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	public File getBackupContactsDir() {
		return getBackupDir("contacts");
	}
	
	public File getBackupDir(String subDir) {
		File file = getRootDir(".backup");
		
		if (!TextUtils.isEmpty(subDir)) {
			file = new File(file, subDir);
		}
		
		if (!file.exists() || !file.isDirectory()) {
			file.mkdirs();
			DebugUtils.logD(TAG, "getBackupDir() mkdir " + file.getAbsolutePath());
		}
		return file;
	}
	
	public File getRootDir(String subDir) {
		if (TextUtils.isEmpty(subDir)) {
			return mRootDir;
		} else {
			return new File(mRootDir, subDir);
		}
	}
	
	
	public static PartitionCapacityStat checkPartitionCapacity(String path) {
		if (path.contains("/sdcard")) {
			return checkPartition(path);
		} else {
			return null;
		}
	}
	
	public static PartitionCapacityStat checkPartition(String path) {
		PartitionCapacityStat statInfo = new PartitionCapacityStat();
		StatFs stat = new StatFs(path);
		statInfo.mBlockSize = stat.getBlockSize();
		statInfo.mTotalBlockCount = stat.getBlockCount();
		statInfo.mTotalSize = (long) statInfo.mBlockSize * (long)statInfo.mTotalBlockCount;
		statInfo.mAvailableBlockCount = stat.getAvailableBlocks();
		statInfo.mAvailableSize = (long) statInfo.mBlockSize * (long)statInfo.mAvailableBlockCount;
		return statInfo;
	}
	
	public static boolean isCanBackup(PartitionCapacityStat part, long threshold) {
		return part != null && part.mAvailableSize > threshold;
	}
}
