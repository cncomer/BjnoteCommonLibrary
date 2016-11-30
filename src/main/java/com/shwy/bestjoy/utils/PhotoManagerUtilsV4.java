package com.shwy.bestjoy.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.BitmapCompat;
import android.support.v4.util.LruCache;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;


public class PhotoManagerUtilsV4 {
    private static final String TAG ="PhotoManagerUtilsV4";
    /** 接收到通知事件，参数为Bundle */
    private static PhotoManagerUtilsV4 INSTANCE = new PhotoManagerUtilsV4();

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



    // Get memory class of this device, exceeding this amount will throw an
    // OutOfMemory exception.
    final int memClass = ((ActivityManager) ComApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
    // Use 1/8th of the available memory for this memory cache.

    final int cacheSize = 1024 * 1024 * memClass / 8;

    /**
     * 以牺牲宝贵的应用内存为代价，内存缓存提供了快速的Bitmap访问方式。
     * LruCache类(可以在Support Library中获取并支持到API  Level 4以上，即1.6版本以上)是非常适合用作缓存Bitmap任务的，
     * 它将最近被引用到的对象存储在一个强引用的LinkedHashMap中，并且在缓存超过了指定大小之后将最近不常使用的对象释放掉。
     *
     * 注意：以前有一个非常流行的内存缓存实现是SoftReference(软引用)或者WeakReference(弱引用)的Bitmap缓存方案，
     * 然而现在已经不推荐使用了。自Android2.3版本(API Level 9)开始，垃圾回收器更着重于对软/弱引用的回收，
     * 这使得上述的方案相当无效。此外，Android 3.0(API Level 11)之前的版本中，
     * Bitmap的备份数据直接存储在native memery中,并不会合理的释放，很可能引起程序超出内存限制而崩溃。
     */
    private LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            // The cache size will be measured in bytes rather than number of items.
            return BitmapCompat.getAllocationByteCount(bitmap);
        }
    };


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

    //	private static final int HARD_CACHE_CAPACITY = 100;
//	// Hard cache, with a fixed maximum capacity and a life duration
//	// 0.75是加载因子为经验值，true则表示按照最近访问量的高低排序，false则表示按照插入顺序排序
//	private HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(MAX_CAPACITY, 0.75f, true){
//		protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
//			if (size() > MAX_CAPACITY) {// 当超过一级缓存阈值的时候，将老的值从一级缓存搬到二级缓存
//				mSecondLevelCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
//				return true;
//			}
//			return false;
//		}
//	};
    public void addBitmapToCache(String photoId, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (mMemoryCache) {
//				try {
//					sHardBitmapCache.put(photoId, bitmap);
//					mMemoryCache.put(photoId, bitmap);
//					DebugUtils.logPhotoUtils(TAG, "final step add bitmap to BitmapCache for photoId " + photoId);
//				} catch (OutOfMemoryError ex) {
//					// Oops, out of memory, clear cache
//					clearCache();
//				}
                if (getBitmapFromCache(photoId) == null) {
                    mMemoryCache.put(photoId, bitmap);
                }
            }
        } else {
            mMemoryCache.remove(photoId);
            DebugUtils.logD(TAG, " remove bitmap from BitmapCache for photoId " + photoId);
        }
    }

    public boolean removeBitmapFromCache(String photoId) {
        mMemoryCache.remove(photoId);
        return true;
    }
    public boolean addBitmapFromCache(String photoId, Bitmap bitmap) {
        mMemoryCache.put(photoId, bitmap);
        return true;
    }

    public Bitmap getBitmapFromCache(String photoId) {

        return mMemoryCache.get(photoId);
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
        mSecondLevelCache.clear();
    }

    public LruCache<String, Bitmap>  getHardBitmapcache() {
        return mMemoryCache;
    }

    private PhotoManagerUtilsV4() { }


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
                DebugUtils.logD(TAG, "Ok:removeTask " + token + " from TaskTokenMap");
                mAsyncTaskTokenMap.remove(token);
                return;
            }
//			if (tasks.contains(task)) {
            boolean removed = tasks.remove(task);
            if (removed) {
                DebugUtils.logD(TAG, "Ok:remove a task with token " + token + " id is " + task.mPhotoId);
            } else {
                DebugUtils.logD(TAG, "Failed:remove a task with token " + token + " id is " + task.mPhotoId);
            }
//			}

        }
    }

    /*public*/ void addTask(String token, AvatorAsyncTask task) {
        if (mAsyncTaskTokenMap.containsKey(token)) {
            LinkedList<AvatorAsyncTask> tasks = mAsyncTaskTokenMap.get(token);

            boolean added = tasks.add(task);
            if (added) {
                DebugUtils.logD(TAG, "Ok:add a task with token " + token + " id is " + task.mPhotoId);
            } else {
                DebugUtils.logD(TAG, "Failed:add a task with token " + token + " id is " + task.mPhotoId);
            }
        } else {
            DebugUtils.logD(TAG, "add a new token " + token + " in mAsyncTaskTokenMap ");
            mAsyncTaskTokenMap.put(token, new LinkedList<AvatorAsyncTask>());
            addTask(token, task);
        }
    }

    /*public*/ void cancel(String token) {
        DebugUtils.logD(TAG, "cancel():cancel all tasks with token " + token);
        if (mAsyncTaskTokenMap.containsKey(token)) {
            LinkedList<AvatorAsyncTask> tasks = mAsyncTaskTokenMap.get(token);
            for(AvatorAsyncTask task: tasks) {
                DebugUtils.logD(TAG, "cancel():cancel task with no is " + task.mPhotoId);
                task.cancel(true);
            }
            int count = tasks.size();
            if (count > 0) {
                tasks.clear();
                DebugUtils.logD(TAG, "cancel():has canceled " + count + " task");
            } else {
                DebugUtils.logD(TAG, "cancel():has no tasks to cancel for token " + token);
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
            if (!bitmapFileToCache.getParentFile().exists()) {
                bitmapFileToCache.getParentFile().mkdirs();
            }
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
            NetworkUtils.closeInputStream(is);
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
                DebugUtils.logD(TAG, "cancel existed unfinished AvatorAsyncTask for photoId " + photoId);
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
            if (!getFileToSave(photoId).exists()) {
                removeBitmapFromCache(photoId);
                avatar = null;
            }
            if (avatar != null && imageView != null) {
                DebugUtils.logD(TAG, "loadPhotoAsync load Bitmap from cache#photoid=" + photoId);
                imageView.setImageBitmap(avatar);
                if (loadCallback != null) {
                    loadCallback.onLoadSuccessed(photoId, imageView, avatar);
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
                imageView.setImageBitmap(avatar);

                if (loadCallback != null) {
                    loadCallback.onLoadSuccessed(photoId, imageView, avatar);
                }
            } else {
                internalLoadLocalPhotoAsync(token, imageView, photoId, defaultBitmap, photo, loadCallback);

            }
        }
    }

    /**异步载入本地图片文件*/
    private void internalLoadPhotoAsync(String token, ImageView imageView, String photoId, Bitmap defaultBitmap, byte[] photo, LoadCallback loadCallback) {
        DebugUtils.logD(TAG, "step 1 set default bitmap");
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
        DebugUtils.logD(TAG, "step 1 set default bitmap");
//		imageView.setImageBitmap(getDefaultBitmap(type));

        LoadLocalPhotoAsyncTask loadPhotoTask = new LoadLocalPhotoAsyncTask(imageView, token, photoId, photo, loadCallback);
//		AvatarDrawable avatorDrawable = new AvatarDrawable(loadPhotoTask);
        AvatarBitmapDrawable avatorDrawable = new AvatarBitmapDrawable(loadPhotoTask, defaultBitmap);
        if (imageView != null) {
            imageView.setImageDrawable(avatorDrawable);
        }
        loadPhotoTask.execute();
    }


    public static PhotoManagerUtilsV4 getInstance() {
        return INSTANCE;
    }
    /***
     * 如果存在映射，说明对于某一个PhotoId已经有下载任务在进行了，我们等待他完成就可以了
     */
    private static HashSet<String> mDownloadingMap = new HashSet<String>();

    private static Object mBlockDownloadLock = new Object();
    private static boolean mIsBlockDownload = false;
    abstract class  AvatorAsyncTask extends AsyncTaskCompat<Void, Void, ServiceResultObject> {
        protected String aToken;
        protected String mPhotoId;
        protected WeakReference<ImageView> imageViewReference;
        LoadCallback _loadCallback;
        private static final String TAG = PhotoManagerUtilsV4.TAG + ".AvatorAsyncTask";

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
        protected ServiceResultObject doInBackground(Void... arg0) {
            ServiceResultObject serviceResultObject = new ServiceResultObject();
            serviceResultObject.mStatusCode = 1;
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
                serviceResultObject.mStatusCode = -1;
                serviceResultObject.mStatusMessage = ComApplication.getInstance().getGeneralErrorMessage(e);
            }
            if (isCancelled()) {
                DebugUtils.logD(TAG, "current task is canceled with the photoID=" + mPhotoId);
                return serviceResultObject;
            }
            return serviceResultObject;
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            removeTask(aToken, this);
            ServiceResultObject serviceResultObject = new ServiceResultObject();
            serviceResultObject.mStatusCode = 0;
            serviceResultObject.mStatusMessage = ComApplication.getInstance().getString(R.string.tip_cancel_by_user);
            notifyCancelStatus(serviceResultObject.mStatusMessage);
            synchronized(mDownloadingMap) {
                if (mDownloadingMap.contains(mPhotoId)) {
                    boolean removed = mDownloadingMap.remove(mPhotoId);
                    DebugUtils.logD(TAG, "Task finish by canceled [in onCancelled()] for photoID=" + mPhotoId + ", remove PhotoId from mDownloadingMap, removed=" + removed);
                }
                mDownloadingMap.notifyAll();
            }
        }

        @Override
        protected void onPostExecute(ServiceResultObject serviceResultObject) {
            super.onPostExecute(serviceResultObject);
            Bitmap bitmap = null;
            if (serviceResultObject.isOpSuccessfully() &&
                    serviceResultObject.mObject != null
                    && serviceResultObject.mObject instanceof Bitmap) {
                bitmap = (Bitmap) serviceResultObject.mObject;
                if (imageViewReference != null) {
                    ImageView imageView = imageViewReference.get();
                    AvatorAsyncTask avatarAsyncTask = getAvatorAsyncTask(imageView);
                    if (this == avatarAsyncTask && imageView != null) {

                        if (_loadCallback != null) {
                            DebugUtils.logD(TAG, "onLoadSuccessed for photoId " + mPhotoId);
                            bitmap = _loadCallback.addToCache(bitmap);
                            imageView.setImageBitmap(bitmap);
                            _loadCallback.onLoadSuccessed(mPhotoId, imageView, bitmap);
                        } else {
                            DebugUtils.logD(TAG, "setImageBitmap for photoId " + mPhotoId);
                            imageView.setImageBitmap(bitmap);
                        }
                    }

                }

            } else {
                notifyErrorStatus(serviceResultObject.mStatusMessage);
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
        private static final String TAG = PhotoManagerUtilsV4.TAG + ".LoadPhotoAsyncTask";

        public LoadPhotoAsyncTask(ImageView imageView, String token, String photoId, byte[] photo, LoadCallback loadCallback) {
            super(imageView, token, photoId, loadCallback);
            lPhoto = photo;
        }

        private File getFileToSave() {
            return PhotoManagerUtilsV4.getFileToSave(mPhotoId);
        }

        private String getServiceUrl() {
            if (mServiceUrl == null) {
                mServiceUrl = PhotoManagerUtilsV4.getServiceUrl(mPhotoId);
            }
            return mServiceUrl;
        }

        @Override
        protected ServiceResultObject doInBackground(Void... params) {
            ServiceResultObject serviceResultObject = super.doInBackground(params);
            if (!serviceResultObject.isOpSuccessfully()) {
                return serviceResultObject;
            }
            InputStream is = null;
            Bitmap bitmap = null;
            File cachedBitmapFile = getFileToSave();
            if (cachedBitmapFile == null) {
                DebugUtils.logE(TAG, "error, LoadPhotoAsyncTask call getFileToSave() which returns null for mPhotoId=" + mPhotoId);
                serviceResultObject.mStatusCode = 0;
                serviceResultObject.mStatusMessage = ComApplication.getInstance().getString(R.string.tip_cant_create_cache_photo_file);
                return serviceResultObject;
            }
            DebugUtils.logD(TAG, "step 2 try to get avator from cached file " + cachedBitmapFile.getAbsolutePath());
            bitmap  = decodeFromCachedBitmapFile(cachedBitmapFile);
            if (bitmap == null && lPhoto != null) {
                DebugUtils.logD(TAG, "step 3 try to get avator from supplied byte array");
                bitmap = decodeByteArray(cachedBitmapFile, lPhoto);
            }
            if (this.isCancelled()) {
                if (bitmap != null) {
                    bitmap.recycle();
                    DebugUtils.logD(TAG, "bitmap.recycle() in bg1 for id " + mPhotoId);
                }
                serviceResultObject.mStatusCode = 0;
                serviceResultObject.mStatusMessage = ComApplication.getInstance().getString(R.string.tip_cancel_by_user);
                return serviceResultObject;
            }
            if (bitmap == null) {
                synchronized(mBlockDownloadLock) {
                    while(mIsBlockDownload) {
                        DebugUtils.logD(TAG, "BlockFeature>>current task is blocked downloading for photoID=" + mPhotoId);
                        try {
                            mBlockDownloadLock.wait();
                            DebugUtils.logD(TAG, "BlockFeature>>current blocked downloading task was notified to continue downloading for photoID=" + mPhotoId);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(isCancelled()) {
                    DebugUtils.logD(TAG, "BlockFeature>>current task was canceled for photoID=" + mPhotoId);
                    serviceResultObject.mStatusCode = 0;
                    serviceResultObject.mStatusMessage = ComApplication.getInstance().getString(R.string.tip_cancel_by_user);
                    return serviceResultObject;
                }
                String url = getServiceUrl();
                try {
                    DebugUtils.logD(TAG, "step 4 download bitmap");
                    HttpResponse respose = NetworkUtils.openContectionLockedV2(url, ComApplication.getInstance().getSecurityKeyValuesObject());
                    if (respose.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                        DebugUtils.logD(TAG, "download bitmap failed, can't find image on server-side for photoid " + mPhotoId);
                        serviceResultObject.mStatusCode = 0;
                        serviceResultObject.mStatusMessage = ComApplication.getInstance().getString(R.string.tip_no_existed_photo_in_service);
                        return serviceResultObject;
                    }
                    is = respose.getEntity().getContent();
                    if (is != null) {
                        DebugUtils.logD(TAG, "step 5 create the mm.p file using bitmap");
                        createCachedBitmapFile(is, cachedBitmapFile);
                        DebugUtils.logD(TAG, "step 6 try to get avator from cached mm.p file");
                        bitmap = decodeFromCachedBitmapFile(cachedBitmapFile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    serviceResultObject.mStatusCode = 0;
                    serviceResultObject.mStatusMessage = ComApplication.getInstance().getGeneralErrorMessage(e);
                } finally {
                    DebugUtils.logD(TAG, "finally() for url="+url + ", is=" + is + ", bitmap="+bitmap);
                    NetworkUtils.closeInputStream(is);
                }
            }
            if (this.isCancelled()) {
                if (bitmap != null)  {
                    bitmap.recycle();
                    bitmap = null;
                    DebugUtils.logD(TAG, "bitmap.recycle() in bg2 for id " + mPhotoId);
                }
            }
            serviceResultObject.mObject = bitmap;
            return serviceResultObject;
        }

    }


    class LoadLocalPhotoAsyncTask extends AvatorAsyncTask {
        private static final String TAG = PhotoManagerUtilsV4.TAG + ".LoadLocalPhotoAsyncTask";
        private byte[] lPhoto;

        public LoadLocalPhotoAsyncTask(ImageView imageView, String token, String photoId, byte[] photo, LoadCallback loadCallback) {
            super(imageView, token, photoId, loadCallback);
            lPhoto = photo;
        }

        private File getFileToSave() {
            return PhotoManagerUtilsV3.getFileToSave(mPhotoId);
        }


        @Override
        protected ServiceResultObject doInBackground(Void... params) {
            ServiceResultObject serviceResultObject = new ServiceResultObject();
            serviceResultObject.mStatusCode = 1;
            Bitmap bitmap = null;
            File cachedBitmapFile = getFileToSave();
            if (cachedBitmapFile == null) {
                DebugUtils.logE(TAG, "error, LoadLocalPhotoAsyncTask call getFileToSave() which returns null for " + mPhotoId);
                serviceResultObject.mStatusCode = 0;
                serviceResultObject.mStatusMessage = ComApplication.getInstance().getString(R.string.tip_cant_create_cache_photo_file);
            }
            DebugUtils.logD(TAG, "step 2 try to get avator from cached file " + cachedBitmapFile.getAbsolutePath());
            bitmap  = decodeFromCachedBitmapFile(cachedBitmapFile);
            if (bitmap == null && lPhoto != null) {
                DebugUtils.logD(TAG, "step 3 try to get avator from supplied byte array");
                bitmap = decodeByteArray(cachedBitmapFile, lPhoto);
            } else if (lPhoto == null ) {
                DebugUtils.logD(TAG, "skip step 3 that try to get avator from supplied null byte array");
            }
            if (this.isCancelled()) {
                if (bitmap != null) {
                    bitmap.recycle();
                    DebugUtils.logD(TAG, "bitmap.recycle() in bg1 for id " + mPhotoId);
                }
                serviceResultObject.mStatusCode = 0;
                serviceResultObject.mStatusMessage = ComApplication.getInstance().getString(R.string.tip_cancel_by_user);
            }
            serviceResultObject.mObject = bitmap;
            return serviceResultObject;
        }

    }


    public static String buildUrlAndLocalFilePathString(String url, String filePath) {
        StringBuilder sb = new StringBuilder(filePath);
        sb.append("|").append(url);
        return sb.toString();
    }

    public static void setBlockDownload(boolean block) {
        synchronized(mBlockDownloadLock) {
            mIsBlockDownload = block;
            if (!mIsBlockDownload) {
                mBlockDownloadLock.notifyAll();
            }
        }
    }
}
