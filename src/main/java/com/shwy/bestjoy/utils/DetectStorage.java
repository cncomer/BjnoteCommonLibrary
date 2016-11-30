package com.shwy.bestjoy.utils;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;


public class DetectStorage {
	
	/**
	* 检查某文件所在分区容量
	* @return 
	*/
	public static PartitionCapacityStat checkPartitionCapacity(String filePath){
		
		File fileDir = null;
		
		if(filePath.contains("/data")){
			fileDir = Environment.getDataDirectory(); 
		}else if(filePath.contains("/cache")){
			fileDir = Environment.getDownloadCacheDirectory(); 
		}else if(filePath.contains("/sdcard") 
				&& Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			fileDir = Environment.getExternalStorageDirectory(); 
		}else if(filePath.contains("/system")){
			fileDir = Environment.getRootDirectory(); 
		}else{
			return null;
		}
		
		String path = fileDir.getPath();		 			
	    return checkPartition(path);
	}

	/**
	* 检查分区容量
	* @return 
	*/
	public static PartitionCapacityStat checkPartition(String path){
		
		PartitionCapacityStat statInfo = new PartitionCapacityStat();
									
	    StatFs stat = new StatFs(path); 
	    statInfo.mBlockSize = stat.getBlockSize(); 
	    statInfo.mTotalBlockCount = stat.getBlockCount();
	    
	    statInfo.mTotalSize = (long)(statInfo.mBlockSize * (long)statInfo.mTotalBlockCount);
	    statInfo.mAvailabelBlockCount = stat.getAvailableBlocks(); 
	    statInfo.mAvailableSize =(long)statInfo.mBlockSize * (long)statInfo.mAvailabelBlockCount;
	    return statInfo;
	}
	
	/**
	 * 检查某分区可用空间是否不小于提供的阀值
	 * @param part 分区信息
	 * @param threshold 阀值
	 * @return
	 */
	public static boolean enough(PartitionCapacityStat part, long threshold) {
	    return part != null && part.mAvailableSize > threshold;
	}
	
	public static class PartitionCapacityStat {

		/** block的size */
		public int mBlockSize;           
		/** block的个数 */
		public int mTotalBlockCount;     
		/** 总容量 */
		public long mTotalSize;           
		/** 可用block的个数 */
		public int mAvailabelBlockCount;  
		/** 可用容量 */
		public long mAvailableSize;
		
		PartitionCapacityStat(){
			
		}
	}
	
	public enum StorageStat {
		UNMOUNTED,   //存储卡未加载
		NOENOUGH,    //没有足够的存储空间
		AVAILABLE    //可用
	}
	
	public StorageStat checkFsAvailable(long sizeThreshold) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return StorageStat.UNMOUNTED;
		}
		PartitionCapacityStat storageStat = DetectStorage.checkPartitionCapacity(Environment.getExternalStorageDirectory().getAbsolutePath());
		if (DetectStorage.enough(storageStat, sizeThreshold)) {
			return StorageStat.AVAILABLE;
		}
		return StorageStat.NOENOUGH;
	}
}
