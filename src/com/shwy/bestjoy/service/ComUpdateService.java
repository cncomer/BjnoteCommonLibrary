package com.shwy.bestjoy.service;

import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.ComPreferencesManager;
import com.shwy.bestjoy.utils.DateUtils;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;
import com.shwy.bestjoy.utils.NetworkUtils;
import com.shwy.bestjoy.utils.SecurityUtils.SecurityKeyValuesObject;
import com.shwy.bestjoy.utils.ServiceAppInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * 
 * @author chenkai
 *
 */
public abstract class ComUpdateService extends Service implements ComConnectivityManager.ConnCallback{
	private static String TAG = "UpdateService";
	private static final boolean DEBUG = false;
	public static final String PKG_NAME = "com.shwy.bestjoy.utils";
	
	/**强制检查更新*/
	public static final String ACTION_UPDATE_CHECK_FORCE = PKG_NAME + "intent.ACTION_UPDATE_CHECK_FORCE";
	/**开始检查更新*/
	public static final String ACTION_UPDATE_CHECK = PKG_NAME + ".intent.ACTION_UPDATE_CHECK";
	/**用户强制立即检查更新*/
	public static final String ACTION_UPDATE_CHECK_FORCE_BY_USER = PKG_NAME + ".intent.ACTION_UPDATE_CHECK_FORCE_BY_USER";
	/**自动检查开始了*/
	public static final String ACTION_UPDATE_CHECK_AUTO = PKG_NAME + ".intent.ACTION_UPDATE_CHECK_AUTO";
	/**开始下载*/
	public static final String ACTION_DOWNLOAD_START = PKG_NAME + ".intent.ACTION_DOWNLOAD_START";
	/**结束下载*/
	public static final String ACTION_DOWNLOAD_END = PKG_NAME + ".intent.ACTION_DOWNLOAD_END";
	/**下载进度*/
	public static final String ACTION_DOWNLOAD_PROGRESS = PKG_NAME + ".intent.ACTION_DOWNLOAD_PROGRESS";
	/**没有网络*/
	public static final String ACTION_UNAVAILABLE_NETWORK = PKG_NAME + ".intent.ACTION_UNAVAILABLE_NETWORK";
	
	public static final int MSG_CHECK_UPDATE = 1000;
	/**开始下载*/
	public static final int MSG_DOWNLOAD_START = 1001;
	/**结束下载*/
	public static final int MSG_DOWNLOAD_END = 1002;
	
	public static final long UPDATE_DURATION_PER_HOUR = 1 * 60 * 60 * 1000; //1小时检查一次
	public static final long UPDATE_DURATION_PER_DAY = 24 * 60 * 60 * 1000; //1天检查一次
	public static final long UPDATE_DURATION_PER_WEEK = 7 * 24 * 60 * 60 * 1000; //7天检查一次
	
	//表示自动更新检查是否正在运行
	public boolean mIsCheckUpdateRuinning = false;
	/**表示服务是否正在运行*/
	public boolean mIsServiceRuinning = false;
	protected Handler mWorkServiceHandler, mHandler;
	
	/**下载结束广播。当接到该广播的时候，我们解析字段Intents.EXTRA_RESULT， false表示下载取消了，true表示下载完成*/
	protected Intent mDownloadEndIntent;
	protected Intent mNoNetworkIntent, mDownloadStartIntent, mDownloadProgressIntent;
	
	public static enum TYPE {
		IDLE,
		DOWNLOADING,
		SUCCESS
	};
	protected TYPE mCurrentType;
	
	protected ServiceAppInfo mServiceAppInfo, mDatabaseServiceAppInfo;
	
	protected void checkUpdateAction() {
		if(checkUpdate()){
			//检查app
			Intent intent = getUpdateActivity();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			DebugUtils.logD(TAG, "checkUpdateAction startActivity intent=" + intent);
			startActivity(intent);
		} else if (checkDeviceDatabaseUpdate()) {
			//检查数据库
			updateDeviceDatabase();
		}
	}
	/**得到更新下载Activity Intent*/
	public abstract Intent getUpdateActivity();
	public abstract SecurityKeyValuesObject getSecurityKeyValuesObject();
	/**得到数据库版本号*/
	public abstract int getDeviceDatabaseVersion();
	/**安装数据库*/
	public abstract void installDeviceDatabase();
	
	/**是否需要更新检查*/
	protected boolean needUpdateCheck(String action) {
		long currentTime = System.currentTimeMillis();
		DebugUtils.logD(TAG, "onServiceIntent currentTime" + DateUtils.TOPIC_SUBJECT_DATE_TIME_FORMAT.format(new Date(currentTime)));
		long lastUpdateCheckTime = mServiceAppInfo.getLatestCheckTime();
		DebugUtils.logD(TAG, "onServiceIntent lastUpdateCheckTime" + DateUtils.TOPIC_SUBJECT_DATE_TIME_FORMAT.format(new Date(lastUpdateCheckTime)));
		boolean needCheckUpdate = false;
		//正常Wifi情况下，我们每一天都会检查一次是否有更新
		if (ComConnectivityManager.getInstance().isWifiConnected()) {
			if (Intent.ACTION_USER_PRESENT.equals(action)) {
				needCheckUpdate = currentTime - lastUpdateCheckTime > UPDATE_DURATION_PER_HOUR;
			} else {
				needCheckUpdate = currentTime - lastUpdateCheckTime > UPDATE_DURATION_PER_DAY;
			}
		} else if (ComConnectivityManager.getInstance().isMobileConnected()) {
			needCheckUpdate = currentTime - lastUpdateCheckTime > UPDATE_DURATION_PER_WEEK;
		} else {
			DebugUtils.logD(TAG, "connectivity is not connected.");
			return false;
		}
		
		if (ACTION_UPDATE_CHECK_FORCE.equals(action)) {
			DebugUtils.logD(TAG, "force updating....");
			needCheckUpdate = true;
		}
		return needCheckUpdate;
	}

	protected ServiceAppInfo getServiceAppInfo(String token) {
		return new ServiceAppInfo(this, token);
	}
	@Override
	public void onCreate() {
		super.onCreate();
		DebugUtils.logD(TAG, "onCreate");
		mNoNetworkIntent = new Intent(ACTION_UNAVAILABLE_NETWORK);
		mDownloadStartIntent = new Intent(ACTION_DOWNLOAD_START);
		mDownloadEndIntent = new Intent(ACTION_DOWNLOAD_END);
		mDownloadProgressIntent = new Intent(ACTION_DOWNLOAD_PROGRESS);
		
		mIsServiceRuinning = true;
		
		mServiceAppInfo = getServiceAppInfo(getPackageName());
		mDatabaseServiceAppInfo = getServiceAppInfo(getPackageName() + ".db");
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (!overrideHandleMessage(msg)) {
					super.handleMessage(msg);
				}
			}
		};
		HandlerThread workThread = new HandlerThread("UpdateWorkService", Process.THREAD_PRIORITY_BACKGROUND);
		workThread.start();
		Looper looper = workThread.getLooper();
		mWorkServiceHandler = new Handler(looper) {

			@Override
			public void handleMessage(Message msg) {
				if (!overrideHandleMessage(msg)) {
					super.handleMessage(msg);
				}
			}
		};
		//当网络状态改变的时候我们需要判断今天是否已经进行过版本检查，如果进行过，那么wifi可用的时候再次进行一次检查
		ComConnectivityManager.getInstance().addConnCallback(this);
	}
	
	protected boolean overrideHandleMessage(Message msg) {
		switch(msg.what) {
		case MSG_CHECK_UPDATE:
			checkUpdateAction();
			return true;
		case MSG_DOWNLOAD_START:
			downloadLocked(mServiceAppInfo.buildExternalDownloadAppFile());
			return true;
		}
		return false;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.v(TAG, "onStart from intent " + intent);
		if (intent != null) {
			if (ComConnectivityManager.getInstance().isConnected()) {
				String action = intent.getAction();
				onServiceIntent(action, intent);
			} else {
				sendBroadcast(mNoNetworkIntent);
			}
			
		}
	}
	protected boolean onServiceIntent(String action) {
		if (ACTION_UPDATE_CHECK.equals(action)
				|| Intent.ACTION_BOOT_COMPLETED.equals(action)
				|| Intent.ACTION_USER_PRESENT.equals(action)
				|| ACTION_UPDATE_CHECK_FORCE.equals(action)) {

			boolean needCheckUpdate = needUpdateCheck(action);

			if (!DEBUG && !needCheckUpdate) {
				DebugUtils.logD(TAG, "need not updating check, time is not enough long");
				return false;
			}
			if (mWorkServiceHandler.hasMessages(MSG_CHECK_UPDATE)) {
				DebugUtils.logD(TAG, "mWorkServiceHandler is running checkupdate, so just ignore");
				return false;
			}
			mWorkServiceHandler.sendEmptyMessage(MSG_CHECK_UPDATE);
			return true;
		} else if (ACTION_DOWNLOAD_START.equals(action)) {
			//开始下载，如果下载任务正在进行了，那么
			synchronized(mDownloadTaskLocked) {
				if (!mIsDownloadTaskRunning) {
					mWorkServiceHandler.sendEmptyMessage(MSG_DOWNLOAD_START);
				} else {
					DebugUtils.logD(TAG, "Download task is running, so we just ignore");
				}
			}
		} else if (ACTION_DOWNLOAD_END.equals(action)) {
			synchronized(mDownloadTaskLocked) {
				if (mIsDownloadTaskRunning) {
					mIsDownloadTaskRunning = false;
				}
			}
		}
		return false;
	}
	protected boolean onServiceIntent(String action, Intent intent) {
		boolean handled = onServiceIntent(action);
		return handled;
	}


	//判断是否需要更新
	protected boolean checkUpdate(){
		DebugUtils.logD(TAG, "start update checking......." + mServiceAppInfo.mToken);
		mIsCheckUpdateRuinning = true;
		boolean needUpdate = false;
        try {
            InputStream is = NetworkUtils.openContectionLocked(mServiceAppInfo.getServiceUrl(), getSecurityKeyValuesObject());
            ServiceAppInfo newServiceAppInfo = ServiceAppInfo.parse(this, mServiceAppInfo.mToken, NetworkUtils.getContentFromInput(is));

            if (newServiceAppInfo != null) {
                SharedPreferences prefs = ComPreferencesManager.getInstance().mPreferManager;
                if (mServiceAppInfo.mVersionCode == -1) {
                    //第一次更新检查，我们需要标记为当前的版本号
                    mServiceAppInfo.mVersionCode = prefs.getInt(ComPreferencesManager.KEY_LATEST_VERSION, 0);
                }

                DebugUtils.logD(TAG, "APK updateCheckTime = " + DateUtils.TOPIC_SUBJECT_DATE_TIME_FORMAT.format(new Date(newServiceAppInfo.mCheckTime)));
                DebugUtils.logD(TAG, "APK currentVersionCode = " + mServiceAppInfo.mVersionCode);
                DebugUtils.logD(TAG, "APK newVersionCode = " + newServiceAppInfo.mVersionCode);

                needUpdate = newServiceAppInfo.mVersionCode > mServiceAppInfo.mVersionCode;
                mServiceAppInfo.copyFromServiceAppInfo(newServiceAppInfo);
                mServiceAppInfo.save();
               if (needUpdate) {
                   DebugUtils.logD(TAG, "APK save mServiceAppInfo = " + mServiceAppInfo.toString());
               }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            mIsCheckUpdateRuinning = false;
        }

		DebugUtils.logD(TAG, "end update app checking......." + mServiceAppInfo.mToken);
		return needUpdate;
	}
	
	//判断是否需要更新
	protected boolean checkDeviceDatabaseUpdate(){
			DebugUtils.logD(TAG, "start update DB checking......." + mDatabaseServiceAppInfo.mToken);
			mIsCheckUpdateRuinning = true;
			boolean needUpdate = false;
            try {
                InputStream is = NetworkUtils.openContectionLocked(mDatabaseServiceAppInfo.getServiceUrl(), getSecurityKeyValuesObject());
                ServiceAppInfo newServiceAppInfo = ServiceAppInfo.parse(this, mDatabaseServiceAppInfo.mToken, NetworkUtils.getContentFromInput(is));

                if (newServiceAppInfo != null) {
                    int currentVersion = getDeviceDatabaseVersion();
                    DebugUtils.logD(TAG, "DB updateCheckTime = " + DateUtils.TOPIC_SUBJECT_DATE_TIME_FORMAT.format(new Date(mDatabaseServiceAppInfo.mCheckTime)));
                    DebugUtils.logD(TAG, "DB currentVersionCode = " + currentVersion);
                    DebugUtils.logD(TAG, "DB newVersionCode = " + newServiceAppInfo.mVersionCode);
                    needUpdate = newServiceAppInfo.mVersionCode > currentVersion;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                mIsCheckUpdateRuinning = false;
            }
			DebugUtils.logD(TAG, "end update DB checking......." + mDatabaseServiceAppInfo.mToken);
			return needUpdate;
		}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mIsServiceRuinning = false;
		ComConnectivityManager.getInstance().removeConnCallback(this);
		Log.v(TAG, "onDestroy");
	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	@Override
	public void onConnChanged(ComConnectivityManager cm) {
		 onServiceIntent(ACTION_UPDATE_CHECK);
	}
	
	
	private boolean mIsDownloadTaskRunning = false;
	private Object mDownloadTaskLocked = new Object();
	
	private void downloadLocked(File file) {
		synchronized(mDownloadTaskLocked) {
			mIsDownloadTaskRunning =  true;
		}
		InputStream is = null;
		long count = 0;
		long total = 0;
		try {
			HttpResponse response = NetworkUtils.openContectionLockedV2(mServiceAppInfo.mApkUrl, getSecurityKeyValuesObject());
			if(response.getStatusLine().getStatusCode() != 200) {
				throw new IOException("StatusCode!=200");
			}
            HttpEntity entity = response.getEntity();  
            total = entity.getContentLength();
            is = entity.getContent();
			if (is != null) {
				 FileOutputStream fileOutputStream = new FileOutputStream(file);  
                 byte[] buf = new byte[4096];  
                 int ch = -1;
                 while ((ch = is.read(buf)) != -1) {
                	 if (!mIsDownloadTaskRunning) {
                		 fileOutputStream.flush();
                		 fileOutputStream.close();
                		 mDownloadEndIntent.putExtra(Intents.EXTRA_RESULT, false);
                		 DebugUtils.logD(TAG, "sendBroadcast for downloadTask is cancelde");
                		 sendBroadcast(mDownloadEndIntent);
                		 throw new CanceledException("Download task is canceled");
                	 }
                 	count += ch;
                    fileOutputStream.write(buf, 0, ch);
                    if (count % 4096 == 0) {
                    	fileOutputStream.flush();
                    }
                    publishProgress((int) (count * 100 / total), total);
                 } 
                 if(count == total) {
                	 publishProgress(100, total);
                 }
                 fileOutputStream.flush();
                 fileOutputStream.close();
                 mDownloadEndIntent.putExtra(Intents.EXTRA_RESULT, true);
        		 DebugUtils.logD(TAG, "sendBroadcast for downloadTask is finished");
        		 sendBroadcast(mDownloadEndIntent);
             } 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			NetworkUtils.closeInputStream(is);
			synchronized(mDownloadTaskLocked) {
				mIsDownloadTaskRunning =  false;
			}
		}
	}
	
	private void publishProgress(int progress, long size) {
		DebugUtils.logD(TAG, "sendBroadcast for downloadTask is updating progress " + progress + ", size is " + size);
		mDownloadProgressIntent.putExtra(Intents.EXTRA_PROGRESS, progress);
		mDownloadProgressIntent.putExtra(Intents.EXTRA_PROGRESS_MAX, size);
		sendBroadcast(mDownloadProgressIntent);
	}
	
	private void updateDeviceDatabase() {
		DebugUtils.logD(TAG, "enter updateDeviceDatabase()");
		InputStream is = null;
		OutputStream out = null;
		try {
			DebugUtils.logD(TAG, "start download " + mDatabaseServiceAppInfo.mApkUrl);
	        is = NetworkUtils.openContectionLocked(mDatabaseServiceAppInfo.mApkUrl, getSecurityKeyValuesObject());
	        if (is != null) {
	        	out = new FileOutputStream(mDatabaseServiceAppInfo.buildLocalDownloadAppFile());
	        	byte[] buf = new byte[4096];
                int ch = -1;
                while ((ch = is.read(buf)) != -1) {
                	out.write(buf, 0, ch);
               	 }
                out.flush();
	        }
	        NetworkUtils.closeOutStream(out);
	        NetworkUtils.closeInputStream(is);
	        DebugUtils.logD(TAG, "save to " + mDatabaseServiceAppInfo.buildLocalDownloadAppFile().getAbsolutePath());
	        mDatabaseServiceAppInfo.save();
	        installDeviceDatabase();
        } catch (ClientProtocolException e) {
	        e.printStackTrace();
        } catch (IOException e) {
	        e.printStackTrace();
        }
	}
	
	/**
	 * 开始下载任务，需要提供要下载的apk的版本号，如果已经有正在下载的任务，
	 * @param context
	 * @param downloadedVersionCode
	 */
	public static void startDownloadTask(Context context, String downloadedVersionCode) {
		Intent intent = new Intent(context, ComUpdateService.class);
		intent.setAction(ACTION_DOWNLOAD_START);
		intent.putExtra(Intents.EXTRA_ID, downloadedVersionCode);
		context.startService(intent);
	}
	
	public static void startUpdateServiceOnAppLaunch(Context context) {
		Intent service = new Intent(context, ComUpdateService.class);
		service.setAction(ACTION_UPDATE_CHECK);
		context.startService(service);
	}
	public static void startUpdateServiceOnBootCompleted(Context context) {
		Intent service = new Intent(context, ComUpdateService.class);
		service.setAction(Intent.ACTION_BOOT_COMPLETED);
		context.startService(service);
	}
	public static void startUpdateServiceOnUserPresent(Context context) {
		Intent service = new Intent(context, ComUpdateService.class);
		service.setAction(Intent.ACTION_USER_PRESENT);
		context.startService(service);
	}
	public static void startUpdateServiceForce(Context context) {
		Intent service = new Intent(context, ComUpdateService.class);
		service.setAction(ACTION_UPDATE_CHECK_FORCE);
		context.startService(service);
	}
}
