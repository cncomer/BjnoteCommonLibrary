package com.shwy.bestjoy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.shwy.bestjoy.ComApplication;
import com.shwy.bestjoy.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class PhotoManagerUtilsV3 {
	private static final String TAG ="PhotoManagerUtilsV3";
	/** 接收到通知事件，参数为Bundle */
	private static PhotoManagerUtilsV3 INSTANCE = new PhotoManagerUtilsV3();

	private Context mContext;
	private static float MAX_RESULT_IMAGE_SIZE = 140f;
	private float mCurrentImageSize = MAX_RESULT_IMAGE_SIZE;

	public static final String DEFAULT_TOKEN = TAG + "_default_token";


	private LinkedHashMap<String, LinkedList<AvatorAsyncTask>> mAsyncTaskTokenMap = new LinkedHashMap<String, LinkedList<AvatorAsyncTask>>(20) {
		@Override
		protected boolean removeEldestEntry(Entry<String, LinkedList<AvatorAsyncTask>> eldest) {
			boolean over = size() >= 20;
			if (over) {
				cancel(eldest.getKey());
			}
			return over;
		}
	};
	
	private static final int MAX_CAPACITY = 20;

	public static interface LoadCallback {
		public void onLoadSuccessed(String photoid, ImageView imageview, Bitmap bitmap);
		/**要添加到Cache中，可以对src做特殊处理*/
		public Bitmap addToCache(Bitmap src);
		public void onLoadCanceled(String photoid, ImageView imageview, String cancelMessage);
		public void onLoadFailed(String photoid, ImageView imageview, String errorMessage);
		public void onLoadProgressChanged(String photoid, ImageView imageview, int currentSize, int maxSize);
	}

	public static class LoadCallbackImpl implements LoadCallback{

		@Override
		public void onLoadSuccessed(String photoid, ImageView imageview, Bitmap bitmap) {

		}

		@Override
		public Bitmap addToCache(Bitmap src) {
			return src;
		}

		@Override
		public void onLoadCanceled(String photoid, ImageView imageview, String cancelMessage) {

		}

		@Override
		public void onLoadFailed(String photoid, ImageView imageview, String errorMessage) {

		}

		@Override
		public void onLoadProgressChanged(String photoid, ImageView imageview, int currentSize, int maxSize) {

		}
	}

	private ConcurrentHashMap<String, SoftReference<Bitmap>> mSecondLevelCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();

	private static final int HARD_CACHE_CAPACITY = 100;
	// Hard cache, with a fixed maximum capacity and a life duration
	// 0.75是加载因子为经验值，true则表示按照最近访问量的高低排序，false则表示按照插入顺序排序
	private HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(MAX_CAPACITY, 0.75f, true){
		protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
			if (size() > MAX_CAPACITY) {// 当超过一级缓存阈值的时候，将老的值从一级缓存搬到二级缓存
				mSecondLevelCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			}
			return false;
		}
	};
    public void addBitmapToCache(String photoId, Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (sHardBitmapCache) {
				try {
					sHardBitmapCache.put(photoId, bitmap);
					DebugUtils.logPhotoUtils(TAG, "final step add bitmap to BitmapCache for photoId " + photoId);
				} catch (OutOfMemoryError ex) {
					// Oops, out of memory, clear cache
					clearCache();
				}
			}
		} else {
			sHardBitmapCache.remove(photoId);
			DebugUtils.logPhotoUtils(TAG, " remove bitmap from BitmapCache for photoId " + photoId);
		}
    }

	public boolean removeBitmapFromCache(String photoId) {
		sHardBitmapCache.remove(photoId);
		return true;
	}
	public boolean addBitmapFromCache(String photoId, Bitmap bitmap) {
		sHardBitmapCache.put(photoId, bitmap);
		return true;
	}
 
    public Bitmap getBitmapFromCache(String photoId) {

		if (photoId == null) {
			return null;
		}
		// First try the hard reference cache
		synchronized (sHardBitmapCache) {
			Bitmap bitmap = sHardBitmapCache.get(photoId);
			if (bitmap != null) {
				// Bitmap found in hard cache
				// Move element to first position, so that it is removed last
				sHardBitmapCache.remove(photoId);
				if (bitmap != null) {
					sHardBitmapCache.put(photoId, bitmap);
					DebugUtils.logPhotoUtils(TAG, "get bitmap from HardBitmapCache for photoId " + photoId);
					return bitmap;
				}

			}
			bitmap = getFromSecondLevelCache(photoId);//从二级缓存中拿
			if (bitmap != null) {
				sHardBitmapCache.put(photoId, bitmap);
				DebugUtils.logPhotoUtils(TAG, "get bitmap from getFromSecondLevelCache for photoId " + photoId);
				return bitmap;
			}
		}

		return null;
    }

	private Bitmap getFromSecondLevelCache(String photoId) {
		Bitmap bitmap = null;
		SoftReference<Bitmap> softReference = mSecondLevelCache.get(photoId);
		if (softReference != null) {
			bitmap = softReference.get();
			if (bitmap == null) {// 由于内存吃紧，软引用已经被gc回收了
				mSecondLevelCache.remove(photoId);
			}
		}
		return bitmap;
	}

	public Drawable getDrawableFromCache(String photoId) {
		Bitmap avatar = getBitmapFromCache(photoId);
		if (avatar != null) {
			return new BitmapDrawable(avatar);
		}
		return null;
	}

	public void clearCache() {
		sHardBitmapCache.clear();
		mSecondLevelCache.clear();
	}

	public HashMap<String, Bitmap> getHardBitmapcache() {
		return sHardBitmapCache;
	}
	
	private PhotoManagerUtilsV3() { }
	
	
	public void setContext(Context context) {
		if (mContext == null) {
			mContext = context;
		}
	}
	
	public void setAvatorSize(int width, int height) {
		mCurrentImageSize = Math.min(width, height);
	}
	
	public void requestToken(String token) {
		if (mAsyncTaskTokenMap.containsKey(token)) {
		    cancel(token);
		} else {
			mAsyncTaskTokenMap.put(token, new LinkedList<AvatorAsyncTask>());
		}
	}
	
	public void releaseToken(String token) {
		if (mAsyncTaskTokenMap.containsKey(token)) {
			cancel(token);
			mAsyncTaskTokenMap.remove(token);
		} 
	}
	
	/*public*/ AvatorAsyncTask findTask(String token, String NO, boolean remove) {
		if (mAsyncTaskTokenMap.containsKey(token)) {
			LinkedList<AvatorAsyncTask> tasks = mAsyncTaskTokenMap.get(token);
			for(AvatorAsyncTask task : tasks) {
				if (task.match(token, NO)) {
					//����mAsyncTaskTokenMap�ҵ���һ��ƥ�䣬removeΪtrue������Ҫ�Ƴ���
					if (remove) tasks.remove(task);
					return task;
				}
			}
		} 
		return null;
	}
	
	/*public*/ void removeTask(String token, AvatorAsyncTask task) {
		if (mAsyncTaskTokenMap.containsKey(token)) {
			LinkedList<AvatorAsyncTask> tasks = mAsyncTaskTokenMap.get(token);
			if (tasks != null && tasks.size() == 0) {
				DebugUtils.logW(TAG, "Ok:removeTask " + token + " from TaskTokenMap");
				mAsyncTaskTokenMap.remove(token);
				return;
			}
//			if (tasks.contains(task)) {
				boolean removed = tasks.remove(task);
				if (removed) {
					DebugUtils.logPhotoUtils(TAG, "Ok:remove a task with token " + token + " id is " + task.mPhotoId);
				} else {
					DebugUtils.logPhotoUtils(TAG, "Failed:remove a task with token " + token + " id is " + task.mPhotoId);
				}
//			}
			
		} 
	}
	
	/*public*/ void addTask(String token, AvatorAsyncTask task) {
		if (mAsyncTaskTokenMap.containsKey(token)) {
			LinkedList<AvatorAsyncTask> tasks = mAsyncTaskTokenMap.get(token);
			
			boolean added = tasks.add(task);
			if (added) {
				DebugUtils.logPhotoUtils(TAG, "Ok:add a task with token " + token + " id is " + task.mPhotoId);
			} else {
				DebugUtils.logPhotoUtils(TAG, "Failed:add a task with token " + token + " id is " + task.mPhotoId);
			}
		} else {
			DebugUtils.logPhotoUtils(TAG, "add a new token " + token + " in mAsyncTaskTokenMap ");
			mAsyncTaskTokenMap.put(token, new LinkedList<AvatorAsyncTask>());
			addTask(token, task);
		}
	}
	
	/*public*/ void cancel(String token) {
		DebugUtils.logPhotoUtils(TAG, "cancel():cancel all tasks with token " + token);
		if (mAsyncTaskTokenMap.containsKey(token)) {
			 LinkedList<AvatorAsyncTask> tasks = mAsyncTaskTokenMap.get(token);
			 for(AvatorAsyncTask task: tasks) {
				 DebugUtils.logPhotoUtils(TAG, "cancel():cancel task with no is " + task.mPhotoId);
				 task.cancel(true);
			 }
			 int count = tasks.size();
			 if (count > 0) {
				 tasks.clear();
				 DebugUtils.logPhotoUtils(TAG, "cancel():has canceled " + count + " task");
			 } else {
				 DebugUtils.logPhotoUtils(TAG, "cancel():has no tasks to cancel for token " + token);
			 }
			 
			 
		}
	}
	
	public Bitmap decodeFromInputStream(InputStream is) {
		if (is == null) return null;
		try {
			Bitmap bitmap =  BitmapFactory.decodeStream(is, null, null);
			return bitmap;
		} catch (OutOfMemoryError oom) {
			oom.printStackTrace();
			return null;
		}
		
	}
	
	public Bitmap decodeByteArray(File bitmapFileToCache, byte[] byteArray) {
		if (byteArray == null) return null;
		try {
			Bitmap bitmap =  BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
			createCachedBitmapFile(bitmapFileToCache, bitmap);
			return bitmap;
		} catch (OutOfMemoryError oom) {
			oom.printStackTrace();
			return null;
		}
		
	}
	
	public Bitmap decodeFromCachedBitmapFile(File bitmapFile) {
		Bitmap bitmap = null;
		if (!bitmapFile.exists()) {
			return null;
		}
		try {
			bitmap =  BitmapUtils.decodeResourceInSampleSize(bitmapFile);
			return bitmap;
		} catch (OutOfMemoryError oom) {
			oom.printStackTrace();
			return null;
		}
	}
	
	public static boolean createCachedBitmapFile(File bitmapFileToCache, Bitmap bitmap) {
		if (bitmapFileToCache.exists()) {
			bitmapFileToCache.delete();
		}
		try {
			return bitmap.compress(CompressFormat.PNG, 100, new FileOutputStream(bitmapFileToCache));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	public static void createCachedBitmapFile(InputStream is, File bitmapFileToCache) {
		byte[] buffer = new byte[4096];
		int read = 0;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(bitmapFileToCache);
			read = is.read(buffer);
			while(read != -1) {
				fos.write(buffer, 0, read);
				read = is.read(buffer);
			}
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			NetworkUtils.closeOutStream(fos);
			
		}
	}
	

	class AvatarDrawable extends ColorDrawable {
		 
        private final WeakReference<AvatorAsyncTask> avatarTaskReference;
 
        public AvatarDrawable(AvatorAsyncTask avatarAsyncTask) {
            super(Color.WHITE);
            avatarTaskReference = new WeakReference<AvatorAsyncTask>(avatarAsyncTask);
        }
 
        public AvatorAsyncTask getAvatorAsyncTask() {
            return avatarTaskReference.get();
        }
    }
	
	class AvatarBitmapDrawable extends BitmapDrawable {
		 
        private final WeakReference<AvatorAsyncTask> avatarTaskReference;

        public AvatarBitmapDrawable(AvatorAsyncTask avatarAsyncTask, Bitmap defaultBitmap) {
        	super(defaultBitmap);
            avatarTaskReference = new WeakReference<AvatorAsyncTask>(avatarAsyncTask);

        }
 
        public AvatorAsyncTask getAvatorAsyncTask() {
            return avatarTaskReference.get();
        }

		@Override
		public void draw(Canvas canvas) {
			super.draw(canvas);
		}
        
        
    }
	
	 public static AvatorAsyncTask getAvatorAsyncTask(ImageView imageView) {
	        if (imageView != null) {
	            Drawable drawable = imageView.getDrawable();
	            if (drawable == null) {
	            	return null;
	            }
	            if (drawable instanceof AvatarDrawable) {
	            	AvatarDrawable avatorDrawable = (AvatarDrawable) drawable;
	                return avatorDrawable.getAvatorAsyncTask();
	            } else if (drawable instanceof AvatarBitmapDrawable) {
	            	AvatarBitmapDrawable avatorDrawable = (AvatarBitmapDrawable) drawable;
	                return avatorDrawable.getAvatorAsyncTask();
	            }
	        }
	        return null;
	 }
 
    public static boolean cancelPotentialDownload(String photoId, ImageView imageView) {
    	AvatorAsyncTask avatarAsyncTask = getAvatorAsyncTask(imageView);
 
        if (avatarAsyncTask != null) {
            if ( !photoId.equals(avatarAsyncTask.mPhotoId) ) {
            	DebugUtils.logPhotoUtils(TAG, "cancel existed unfinished AvatorAsyncTask for photoId " + photoId);
            	avatarAsyncTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }
    
    /**异步载入图片，可能会需要从服务器上下载*/
	public void loadPhotoAsync(ImageView imageView, String photoId, byte[] photo, Bitmap defaultBitmap) {
		loadPhotoAsync(DEFAULT_TOKEN, imageView, photoId, photo, defaultBitmap, null);
	}
	public void loadPhotoAsync(ImageView imageView, String photoId, byte[] photo, Bitmap defaultBitmap, LoadCallback loadCallback) {
		loadPhotoAsync(DEFAULT_TOKEN, imageView, photoId, photo, defaultBitmap, loadCallback);
	}
	public void loadPhotoAsync(String token, ImageView imageView, String photoId, byte[] photo, Bitmap defaultBitmap) {
		loadPhotoAsync(token, imageView, photoId, photo, defaultBitmap, null);
	}
    /**异步载入图片，可能会需要从服务器上下载*/
	public void loadPhotoAsync(String token, ImageView imageView, String photoId, byte[] photo, Bitmap defaultBitmap, LoadCallback loadCallback) {
		if (cancelPotentialDownload(photoId, imageView)) {
            Bitmap avatar = getBitmapFromCache(photoId);
            if (avatar != null && imageView != null) {
				DebugUtils.logD(TAG, "loadPhotoAsync load Bitmap from cache#photoid=" + photoId);
				if (loadCallback != null) {
					loadCallback.onLoadSuccessed(photoId, imageView, avatar);
				} else {
					imageView.setImageBitmap(avatar);
				}
            } else {
            	internalLoadPhotoAsync(token, imageView, photoId, defaultBitmap, photo, loadCallback);
            }
	    }
	}
	/**异步载入本地图片文件*/
	public void loadLocalPhotoAsync(String token, ImageView imageView, String photoId, Bitmap defaultBitmap, byte[] photo) {
		loadLocalPhotoAsync(token, imageView, photoId, defaultBitmap, photo, null);
	}
	/**异步载入本地图片文件*/
	public void loadLocalPhotoAsync(String token, ImageView imageView, String photoId, Bitmap defaultBitmap, byte[] photo, LoadCallback loadCallback) {
		if (cancelPotentialDownload(photoId, imageView)) {
			
            Bitmap avatar = getBitmapFromCache(photoId);
            if (avatar != null && imageView != null) {
				DebugUtils.logD(TAG, "loadPhotoAsync load Bitmap from cache#photoid=" + photoId);
				if (loadCallback != null) {
					loadCallback.onLoadSuccessed(photoId, imageView, avatar);
				} else {
					imageView.setImageBitmap(avatar);
				}
            } else {
            	internalLoadLocalPhotoAsync(token, imageView, photoId, defaultBitmap, photo, loadCallback);
            	
            }
	    }
	}
	
	/**异步载入本地图片文件*/
	private void internalLoadPhotoAsync(String token, ImageView imageView, String photoId, Bitmap defaultBitmap, byte[] photo, LoadCallback loadCallback) {
		DebugUtils.logPhotoUtils(TAG, "step 1 set default bitmap");
//		imageView.setImageBitmap(getDefaultBitmap(type));
		
		LoadPhotoAsyncTask loadPhotoTask = new LoadPhotoAsyncTask(imageView, token, photoId, photo, loadCallback);
//		AvatarDrawable avatorDrawable = new AvatarDrawable(loadPhotoTask);
		AvatarBitmapDrawable avatorDrawable = new AvatarBitmapDrawable(loadPhotoTask, defaultBitmap);
        if (imageView != null) {
            imageView.setImageDrawable(avatorDrawable);
        }
		loadPhotoTask.execute();
	}
	/**异步载入本地图片文件*/
	private void internalLoadLocalPhotoAsync(String token, ImageView imageView, String photoId, Bitmap defaultBitmap, byte[] photo, LoadCallback loadCallback) {
		DebugUtils.logPhotoUtils(TAG, "step 1 set default bitmap");
//		imageView.setImageBitmap(getDefaultBitmap(type));
		
		LoadLocalPhotoAsyncTask loadPhotoTask = new LoadLocalPhotoAsyncTask(imageView, token, photoId, photo, loadCallback);
//		AvatarDrawable avatorDrawable = new AvatarDrawable(loadPhotoTask);
		AvatarBitmapDrawable avatorDrawable = new AvatarBitmapDrawable(loadPhotoTask, defaultBitmap);
        if (imageView != null) {
            imageView.setImageDrawable(avatorDrawable);
        }
		loadPhotoTask.execute();
	}
	
	
	public static PhotoManagerUtilsV3 getInstance() {
		return INSTANCE;
	}
	/***
	 * 如果存在映射，说明对于某一个PhotoId已经有下载任务在进行了，我们等待他完成就可以了
	 */
	private static HashSet<String> mDownloadingMap = new HashSet<String>();
	abstract class  AvatorAsyncTask extends AsyncTaskCompat<Void, Void, Bitmap> {
		protected String aToken;
		protected String mPhotoId;
		protected WeakReference<ImageView> imageViewReference;
		LoadCallback _loadCallback;
		private static final String TAG = PhotoManagerUtilsV3.TAG + ".AvatorAsyncTask";
		
		public AvatorAsyncTask(ImageView imageView, String token, String photoId, LoadCallback loadCallback) {
			imageViewReference = new WeakReference<ImageView>(imageView);
			mPhotoId = photoId;
			aToken = token;
			_loadCallback = loadCallback;
			addTask(aToken, this);
		}
		
		
		public void setTokenAndNo(String token, String photoId) {
			aToken = token;
			mPhotoId = photoId;
		}

		public String getToken() {
			return aToken;
		}
		public String getPhotoId() {
			return mPhotoId;
		}
		
		public boolean match(String token, String photoId) {
			return !TextUtils.isEmpty(aToken) && aToken.equals(token) ||
					!TextUtils.isEmpty(mPhotoId) && mPhotoId.equals(photoId) ||
					aToken == null && token==null;
		}
		
		protected void notifyErrorStatus(String message) {
			ImageView imageView = null;
			if (imageViewReference != null) {
				imageView = imageViewReference.get();
			}
			if (_loadCallback != null) {
				_loadCallback.onLoadFailed(mPhotoId, imageView, message);
			}
		}

		protected void notifyCancelStatus(String message) {
			ImageView imageView = null;
			if (imageViewReference != null) {
				imageView = imageViewReference.get();
			}
			if (_loadCallback != null) {
				_loadCallback.onLoadCanceled(mPhotoId, imageView, message);
			}
		}
		
		@Override
		protected Bitmap doInBackground(Void... arg0) {
			try {
				synchronized(mDownloadingMap) {
					while (!isCancelled() && mDownloadingMap.contains(mPhotoId)) {
						DebugUtils.logD(TAG, "other task is running with the same photoID=" + mPhotoId + ", so just wait.......");
						mDownloadingMap.wait();
					}
					DebugUtils.logD(TAG, "current task add into DownloadingMap for photoID=" + mPhotoId);
					mDownloadingMap.add(mPhotoId);
				}
			} catch (InterruptedException e) {
				DebugUtils.logD(TAG, "current task is Interrupted for photoID=" + mPhotoId);
				e.printStackTrace();
				notifyCancelStatus(e.getMessage());
				return null;
			}
			if (isCancelled()) {
				DebugUtils.logD(TAG, "current task is canceled with the photoID=" + mPhotoId);
				return null;
			}
			return null;
		}
		@Override
		protected void onCancelled() {
			super.onCancelled();
			removeTask(aToken, this);
			notifyCancelStatus("task canceled for photoID="+ mPhotoId);
			synchronized(mDownloadingMap) {
				if (mDownloadingMap.contains(mPhotoId)) {
					boolean removed = mDownloadingMap.remove(mPhotoId);
					DebugUtils.logD(TAG, "Task finish by canceled [in onCancelled()] for photoID=" + mPhotoId + ", remove PhotoId from mDownloadingMap, removed=" + removed);
				}
				mDownloadingMap.notifyAll();
			}
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			if (bitmap != null) {
				 if (imageViewReference != null) {
					 DebugUtils.logPhotoUtils(TAG, "onPostExecute imageViewReference !=null, photoId " + mPhotoId);
					ImageView imageView = imageViewReference.get();
					AvatorAsyncTask avatarAsyncTask = getAvatorAsyncTask(imageView);
					 DebugUtils.logPhotoUtils(TAG, "onPostExecute imageView=" + imageView+",avatarAsyncTask="+avatarAsyncTask+", photoId " + mPhotoId);
					if (this == avatarAsyncTask && imageView != null) {
						if (_loadCallback != null) {
							DebugUtils.logPhotoUtils(TAG, "onLoadSuccessed for photoId " + mPhotoId);
							bitmap = _loadCallback.addToCache(bitmap);
							_loadCallback.onLoadSuccessed(mPhotoId, imageView, bitmap);
						} else {
							DebugUtils.logPhotoUtils(TAG, "setImageBitmap for photoId " + mPhotoId);
							imageView.setImageBitmap(bitmap);
						}
					}

				 }

            }
			addBitmapToCache(mPhotoId, bitmap);
			removeTask(aToken, this);
			synchronized(mDownloadingMap) {
				if (mDownloadingMap.contains(mPhotoId)) {
					boolean removed = mDownloadingMap.remove(mPhotoId);
					DebugUtils.logD(TAG, "Task finished for photoID=" + mPhotoId + ", remove PhotoId from mDownloadingMap, removed=" + removed);
				}
				mDownloadingMap.notifyAll();
			}
		}
		
	}
	
	public static File getFileToSave(String photoId) {
		String[] paras = photoId.split("\\|");
		return new File(paras[0]);
	}
	
	public static String getServiceUrl(String photoId) {
		String[] paras = photoId.split("\\|");
		return paras[1];
	}
	
	class LoadPhotoAsyncTask extends AvatorAsyncTask {
		private byte[] lPhoto;
		private String mServiceUrl = null;
		private static final String TAG = PhotoManagerUtilsV3.TAG + ".LoadPhotoAsyncTask";
		
		public LoadPhotoAsyncTask(ImageView imageView, String token, String photoId, byte[] photo, LoadCallback loadCallback) {
			super(imageView, token, photoId, loadCallback);
			lPhoto = photo;
		}
		
		private File getFileToSave() {
			return PhotoManagerUtilsV3.getFileToSave(mPhotoId);
		}
		
		private String getServiceUrl() {
			if (mServiceUrl == null) {
				mServiceUrl = PhotoManagerUtilsV3.getServiceUrl(mPhotoId);
			}
			return mServiceUrl;
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			super.doInBackground(params);
			InputStream is = null;
			Bitmap bitmap = null;
			File cachedBitmapFile = getFileToSave();
			if (cachedBitmapFile == null) {
				DebugUtils.logE(TAG, "error, LoadPhotoAsyncTask call getFileToSave() which returns null for mPhotoId=" + mPhotoId);
				notifyErrorStatus("Can't access cachedBitmapFile for photoid=" + mPhotoId);
				return null;
			}
			DebugUtils.logPhotoUtils(TAG, "step 2 try to get avator from cached file " + cachedBitmapFile.getAbsolutePath());
			bitmap  = decodeFromCachedBitmapFile(cachedBitmapFile);
		    if (bitmap == null && lPhoto != null) {
				DebugUtils.logPhotoUtils(TAG, "step 3 try to get avator from supplied byte array");
				bitmap = decodeByteArray(cachedBitmapFile, lPhoto);
			}
		    if (this.isCancelled()) {
				if (bitmap != null) {
					bitmap.recycle();
					DebugUtils.logPhotoUtils(TAG, "bitmap.recycle() in bg1 for id " + mPhotoId);
				}
				return null;
			}
			if (bitmap == null) {
				String url = getServiceUrl();
				try {
					DebugUtils.logPhotoUtils(TAG, "step 4 download bitmap");
					HttpResponse respose = NetworkUtils.openContectionLockedV2(url, ComApplication.getInstance().getSecurityKeyValuesObject());
					if (respose.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
						DebugUtils.logPhotoUtils(TAG, "download bitmap failed, can't find image on server-side for photoid " + mPhotoId);
						notifyErrorStatus(ComApplication.getInstance().getString(R.string.tip_no_existed_photo_in_service));
					    return null;
					}
					is = respose.getEntity().getContent();
					if (is != null) {
						DebugUtils.logPhotoUtils(TAG, "step 5 create the mm.p file using bitmap");
						createCachedBitmapFile(is, cachedBitmapFile);
						DebugUtils.logPhotoUtils(TAG, "step 6 try to get avator from cached mm.p file");
						bitmap = decodeFromCachedBitmapFile(cachedBitmapFile);
					}
				} catch (Exception e) {
					e.printStackTrace();
					notifyErrorStatus(ComApplication.getInstance().getGeneralErrorMessage(e));
				} finally {
					DebugUtils.logPhotoUtils(TAG, "finally() for path="+url + ", is=" + is + ", bitmap="+bitmap);
					NetworkUtils.closeInputStream(is);
				}
			}
			if (this.isCancelled()) {
				if (bitmap != null)  {
					bitmap.recycle();
					bitmap = null;
					DebugUtils.logPhotoUtils(TAG, "bitmap.recycle() in bg2 for id " + mPhotoId);
				}
			}
			return bitmap;
		}
		
	}
	
	
	class LoadLocalPhotoAsyncTask extends AvatorAsyncTask {
		private static final String TAG = PhotoManagerUtilsV3.TAG + ".LoadLocalPhotoAsyncTask";
		private byte[] lPhoto;
		
		public LoadLocalPhotoAsyncTask(ImageView imageView, String token, String photoId, byte[] photo, LoadCallback loadCallback) {
			super(imageView, token, photoId, loadCallback);
			lPhoto = photo;
		}
		
		private File getFileToSave() {
			return PhotoManagerUtilsV3.getFileToSave(mPhotoId);
		}
		

		@Override
		protected Bitmap doInBackground(Void... params) {
			Bitmap bitmap = null;
			File cachedBitmapFile = getFileToSave();
			if (cachedBitmapFile == null) {
				DebugUtils.logE(TAG, "error, LoadLocalPhotoAsyncTask call getFileToSave() which returns null for " + mPhotoId);
				return null;
			}
			DebugUtils.logPhotoUtils(TAG, "step 2 try to get avator from cached file " + cachedBitmapFile.getAbsolutePath());
			bitmap  = decodeFromCachedBitmapFile(cachedBitmapFile);
		    if (bitmap == null && lPhoto != null) {
				DebugUtils.logPhotoUtils(TAG, "step 3 try to get avator from supplied byte array");
				bitmap = decodeByteArray(cachedBitmapFile, lPhoto);
			} else if (lPhoto == null ) {
				DebugUtils.logPhotoUtils(TAG, "skip step 3 that try to get avator from supplied null byte array");
			}
		    if (this.isCancelled()) {
				if (bitmap != null) {
					bitmap.recycle();
					DebugUtils.logPhotoUtils(TAG, "bitmap.recycle() in bg1 for id " + mPhotoId);
				}
				return null;
			}
			return bitmap;
		}
		
	}


	public static String buildUrlAndLocalFilePathString(String url, String filePath) {
		StringBuilder sb = new StringBuilder(filePath);
		sb.append("|").append(url);
		return sb.toString();
	}
}
