package com.shwy.bestjoy.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * version 版本号  从1开始
 * date 日期  
 * importance 重要程度  重要程度分为0和1，0表示强制更新, 1表示普通，其他的暂时不支持
 * size 更新大小 
 * apk 更新apk的地址，如果是default表示的是默认路径http://www.bjnote.com/down4/bjnote.apk
 * note 更新说明 如果是http开头的，会以网页的形式显示
 * @author chenkai
 *
 */
public class ServiceAppInfo implements Parcelable, IServiceAppInfo{
	 /**最近一次执行过自动检测的时间*/

    
	public static final String DEFAULT_UPDATE_FILE_URL="http://www.mingdown.com/mobile/getVersion.ashx?app=";
	
	
	public int mVersionCode = 0;
	public String mReleaseDate ;
	/**更新补丁的重要程度，如果是0表示必须更新并安装，否则无法继续使用；默认是1表示可选择安装更新也可以不选择。*/
	public int mImportance = IMPORTANCE_OPTIONAL;
	public String mSizeStr;
	public String mApkUrl;
	public String mReleaseNote;
	
	public String mVersionName = "";
	
	public long mCheckTime;
	
	public String mToken;
	public SharedPreferences mPreferences;
	public Context mContext;
    public String mMD5 = "";

	private String mUpdateServiceUrl;
    
	public ServiceAppInfo(Context context, String token) {
		mToken = token;
		mContext = context;
		init();
	}
	
	public ServiceAppInfo(Context context) {
		//默认是app信息
		mToken = context.getPackageName();
		mContext = context;
		init();
	}

	public void init() {
		mPreferences = mContext.getSharedPreferences(mToken, Context.MODE_PRIVATE);
		read();
	}

	public static ServiceAppInfo parse(Context context, String token, String content) {
		ServiceAppInfo serviceAppInfo = null;
		try {
			JSONObject json = new JSONObject(content);
			serviceAppInfo = new ServiceAppInfo(context, token);
			serviceAppInfo.mVersionCode = json.getInt(KEY_VERSION_CODE);
			serviceAppInfo.mReleaseDate = json.getString(KEY_DATE);
			serviceAppInfo.mImportance = json.getInt(KEY_IMPORTANCE);
			serviceAppInfo.mSizeStr = json.getString(KEY_SIZE);
			serviceAppInfo.mApkUrl = json.getString(KEY_APK);
			serviceAppInfo.mReleaseNote = json.getString(KEY_NOTE);
			serviceAppInfo.mVersionName = json.optString(KEY_VERSION_NAME, String.valueOf(serviceAppInfo.mVersionCode));
			serviceAppInfo.mMD5 = json.optString(KEY_MD5, "");
			serviceAppInfo.mCheckTime = System.currentTimeMillis();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return serviceAppInfo;
	}

	public String getServiceUrl() {
		if (mUpdateServiceUrl == null) {
			StringBuilder sb = new StringBuilder(DEFAULT_UPDATE_FILE_URL);
			sb.append(mToken);
			return sb.toString();
		} else {
			return mUpdateServiceUrl;
		}

	}

	public void setServiceUrl(String serviceUrl) {
		mUpdateServiceUrl = serviceUrl;
	}
	
	public String buildReleasenote() {
		return "";
	}
	public void save() {
		mPreferences.edit()
		.putLong(KEY_SERVICE_APP_INFO_CHECK_TIME, mCheckTime)
		.putInt(KEY_SERVICE_APP_INFO_VERSION_CODE, mVersionCode)
		.putString(KEY_SERVICE_APP_INFO_VERSION_NAME, mVersionName)
		.putString(KEY_SERVICE_APP_INFO_RELEASENOTE, mReleaseNote)
		.putString(KEY_SERVICE_APP_INFO_APK_URL, mApkUrl)
		.putString(KEY_SERVICE_APP_INFO_APK_SIZE, mSizeStr)
		.putString(KEY_SERVICE_APP_INFO_RELEASEDATE, mReleaseDate)
		.putInt(KEY_SERVICE_APP_INFO_IMPORTANCE, mImportance)
        .putString(KEY_SERVICE_APP_INFO_MD5, mMD5)
		.commit();
		updateLatestCheckTime(mCheckTime);
	}
	
	public void read() {
		
		mCheckTime = mPreferences.getLong(KEY_SERVICE_APP_INFO_CHECK_TIME, -1l);
		if (mCheckTime == -1l) {
			DebugUtils.logD("AppInfo", "read mCheckTime from preferences " + mCheckTime);
		}
		mVersionCode = mPreferences.getInt(KEY_SERVICE_APP_INFO_VERSION_CODE, -1);
		mVersionName = mPreferences.getString(KEY_SERVICE_APP_INFO_VERSION_NAME, "");
		mReleaseNote = mPreferences.getString(KEY_SERVICE_APP_INFO_RELEASENOTE, "");
		mApkUrl = mPreferences.getString(KEY_SERVICE_APP_INFO_APK_URL, "");
		
		mSizeStr = mPreferences.getString(KEY_SERVICE_APP_INFO_APK_SIZE, "");
		mReleaseDate = mPreferences.getString(KEY_SERVICE_APP_INFO_RELEASEDATE, "");
		mImportance = mPreferences.getInt(KEY_SERVICE_APP_INFO_IMPORTANCE, IMPORTANCE_OPTIONAL);

        mMD5 = mPreferences.getString(KEY_SERVICE_APP_INFO_MD5, "");
	}
	
	public boolean hasChecked() {
		return mCheckTime > 0;
	}
	
	/**每次都要从配置文件中读取最后检查更新时间*/
	public long getLatestCheckTime() {
		return mPreferences.getLong(ServiceAppInfo.KEY_SERVICE_APP_INFO_CHECK_TIME, 0);
	}
	
	public boolean updateLatestCheckTime(long time) {
		return mPreferences.edit().putLong(ServiceAppInfo.KEY_SERVICE_APP_INFO_CHECK_TIME, time).commit();
	}

	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mVersionCode);
		dest.writeString(mReleaseDate);
		dest.writeInt(mImportance);
		dest.writeString(mSizeStr);
		dest.writeString(mApkUrl);
		dest.writeString(mReleaseNote);
		dest.writeString(mVersionName);
		dest.writeString(mToken);

        dest.writeString(mMD5);
	}
	
    public File buildLocalDownloadAppFile() {
    	StringBuilder sb = new StringBuilder("apk_");
    	sb.append(String.valueOf(mVersionCode))
    	.append(mToken)
    	.append(".temp");
        File root = mContext.getFilesDir();
        if (!root.exists()) {
            root.mkdirs();
        }
        return new File(root, sb.toString());
    }

    public File buildExternalDownloadAppFile() {
    	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		return null;
    	}
    	StringBuilder sb = new StringBuilder("apk_");
    	sb.append(String.valueOf(mVersionCode))
    	.append(mToken)
    	.append(".temp");

        File root = new File(Environment.getExternalStorageDirectory(), mContext.getPackageName());

        root = new File(root, ".download");
        if (!root.exists()) {
            root.mkdirs();
        }
    	return new File(root, sb.toString());
    }

  //add by chenkai, 20140618, updating check end
    
    
    public void copyFromServiceAppInfo(ServiceAppInfo serviceAppInfo) {
        mVersionCode = serviceAppInfo.mVersionCode;
        mCheckTime = serviceAppInfo.mCheckTime;
        mVersionName = serviceAppInfo.mVersionName;
        mReleaseNote = serviceAppInfo.mReleaseNote;
        mApkUrl = serviceAppInfo.mApkUrl;

        mSizeStr = serviceAppInfo.mSizeStr;
        mImportance = serviceAppInfo.mImportance;
        mReleaseDate = serviceAppInfo.mReleaseDate;
        mMD5 = serviceAppInfo.mMD5;
    }
	
	
}
