package com.shwy.bestjoy.utils;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;


public class DetectStorage {
	
	/**
	* ���ĳ�ļ����ڷ�������
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
	* ����������
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
	 * ���ĳ�������ÿռ��Ƿ�С���ṩ�ķ�ֵ
	 * @param part ������Ϣ
	 * @param threshold ��ֵ
	 * @return
	 */
	public static boolean enough(PartitionCapacityStat part, long threshold) {
	    return part != null && part.mAvailableSize > threshold;
	}
	
	public static class PartitionCapacityStat {

		/** block��size */
		public int mBlockSize;           
		/** block�ĸ��� */
		public int mTotalBlockCount;     
		/** ������ */
		public long mTotalSize;           
		/** ����block�ĸ��� */
		public int mAvailabelBlockCount;  
		/** �������� */
		public long mAvailableSize;
		
		PartitionCapacityStat(){
			
		}
	}
	
	public enum StorageStat {
		UNMOUNTED,   //�洢��δ����
		NOENOUGH,    //û���㹻�Ĵ洢�ռ�
		AVAILABLE    //����
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
