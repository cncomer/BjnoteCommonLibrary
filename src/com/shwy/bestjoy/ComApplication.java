package com.shwy.bestjoy;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.shwy.bestjoy.utils.AlertDialogWrapper;
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.ComPreferencesManager;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.DevicesUtils;
import com.shwy.bestjoy.utils.FilesUtils;
import com.shwy.bestjoy.utils.SecurityUtils;
import com.shwy.bestjoy.utils.SecurityUtils.SecurityKeyValuesObject;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class ComApplication extends Application{


	private static final String TAG ="ComApplication";
	/**对于不同的保修卡，我们只要确保该变量为正确的应用包名即可*/
    public Handler mHandler;

    public InputMethodManager mImMgr;
	public DisplayMetrics mDisplayMetrics;

    public Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();
        Log.d(TAG, "onCreate()");
        mContext = this;
		mHandler = new Handler();
		mImMgr = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

		//用于屏幕适配
		mDisplayMetrics = this.getResources().getDisplayMetrics();
		Log.d(TAG, mDisplayMetrics.toString());
		Log.d(TAG, getDeviceInfo(this));
		//注册会用到IMEI号
		DevicesUtils.getInstance().setContext(this);
		ComPreferencesManager.getInstance().setContext(this);

	}
	
	public Handler getGlobalHandler() {
		return mHandler;
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		ComConnectivityManager.getInstance().endConnectivityMonitor();
	}
	
	
	public void showMessageAsync(final int resId) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mContext, resId, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public void showMessageAsync(final String msg) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public void showMessageAsync(final int resId, final int length) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mContext, resId, resId).show();
			}
		});
	}
	
	public void showMessageAsync(final String msg, final int length) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mContext, msg, length).show();
			}
		});
	}
	
	public void showShortMessageAsync(final int msgId, final int toastId) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mContext, msgId, toastId).show();
			}
		});
	}
	
	public void showMessage(int resId) {
		Toast.makeText(mContext, resId, Toast.LENGTH_LONG).show();
	}
	
	public void showMessage(String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}
	public void showMessage(String msg, int length) {
		Toast.makeText(mContext, msg, length).show();
	}
	
	public void showMessage(int resId, int length) {
		Toast.makeText(mContext, resId, length).show();
	}
	
	public void showShortMessage(int resId) {
		showMessage(resId, Toast.LENGTH_SHORT);
	}
	public void showShortMessage(String message) {
		showMessage(message, Toast.LENGTH_SHORT);
	}
	
	public void postAsync(Runnable runnable){
		mHandler.post(runnable);
	}
	public void postDelay(Runnable runnable, long delayMillis){
		mHandler.postDelayed(runnable, delayMillis);
	}
	
	public void removeRunnable(Runnable runnable) {
		mHandler.removeCallbacks(runnable);
	}

    /***
     * 显示通常的网络连接错误
     * @return
     */
    public String getGernalNetworkError() {
        return this.getString(R.string.msg_gernal_network_error);
    }

    public String getNetworkError(String statusCode) {
        return this.getString(R.string.msg_network_error_statue, statusCode);
    }
	public void showUnsupportMessage() {
    	showMessage(R.string.msg_unsupport_operation);
    }
	
	//add by chenkai, 20131123, security support begin
    private SecurityKeyValuesObject mSecurityKeyValuesObject;
    public SecurityKeyValuesObject getSecurityKeyValuesObject() {
    	if (mSecurityKeyValuesObject == null) {
    		//Here, we need to notice.
    		new Exception("warnning getSecurityKeyValuesObject() return null").printStackTrace();
    	}
    	return mSecurityKeyValuesObject;
    }
    public void setSecurityKeyValuesObject(SecurityKeyValuesObject securityKeyValuesObject) {
    	mSecurityKeyValuesObject = securityKeyValuesObject;
    }
    
  //add by chenkai, 20131208, updating check begin
    public File buildLocalDownloadAppFile(String pkgName, int downloadedVersionCode) {
    	StringBuilder sb = new StringBuilder(pkgName);
    	sb.append(String.valueOf(downloadedVersionCode))
    	.append(".apk");
    	return new File(getExternalStorageRoot(".download"), sb.toString());
    }


    public boolean hasExternalStorage() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 返回files/dirName/filename， 如果dirName和fileName都为null, 返回files目录
     * @param dirName 如果为null, 返回files/filename
     * @param fileName 如果为null, 返回files/dirName
     * @return
     */
    public File getAppFile(String dirName, String fileName) {
        File root = getFilesDir();
        checkDirAndMkdirs(root);
        if (dirName != null) {
            root = new File(root, dirName);
            checkDirAndMkdirs(root);
        }
        if (fileName != null) {
            root = new File(root, fileName);
        }
        return root;
    }

    /**
     * 得到包路径下files/cache/dirName/fileName文件,如果dirName为null,那么返回cache/fileName文件
     * @param dirName
     * @param fileName
     * @return
     */
    public File getCachedFile(String dirName, String fileName) {
        File cache = getAppFile(FilesUtils.DIR_ACCOUNTS_ROOT, FilesUtils.DIR_ACCOUNTS_CACHE);
        checkDirAndMkdirs(cache);

        if (dirName != null) {
            cache =  new File(cache, dirName);
            checkDirAndMkdirs(cache);
        }

        if (fileName != null) {
            cache =  new File(cache, fileName);
        }
        return cache;
    }

    /**
     * 得到包路径下cache/dirName/fileName文件,如果dirName为null,那么返回cache/fileName文件
     * @param fileName
     * @return
     */
    public File getAppCachedFile(String dirName, String fileName) {
        File cache = getCacheDir();
        checkDirAndMkdirs(cache);

        if (dirName != null) {
            cache =  new File(cache, dirName);
            checkDirAndMkdirs(cache);
        }

        if (fileName != null) {
            cache =  new File(cache, fileName);
        }
        return cache;
    }

    /**
     * return mnt/sdcard/xxx/type目录,如果type为空，则返回mnt/sdcard/xxx/目录
     * 返回SD卡的应用根目录，type为子目录名字， 如download、.download
     * @param type
     * @return
     */
    public File getExternalStorageRoot(String type) {
    	if (!hasExternalStorage()) {
    		return null;
    	}
        File root = new File(Environment.getExternalStorageDirectory(), getPackageName());
    	if (TextUtils.isEmpty(type)) {
            checkDirAndMkdirs(root);
            return root;
    	}
    	File typeDir = new File(root, type);
        checkDirAndMkdirs(typeDir);
    	return typeDir;
    }

    /**
     * return mnt/sdcard/pkgName/cache目录、cache下的目录或者文件， 如果dirName、fileName均为null, 则返回cache目录
     * @param dirName 子目录
     * @param fileName 文件名
     * */
    public File getExternalStorageCache(String dirName, String fileName) {
    	if (!hasExternalStorage()) {
    		return null;
    	}
    	File root =  getExternalStorageRoot("cache");
        checkDirAndMkdirs(root);

        if (dirName != null) {
            //需要创建目录
            root = new File(root, dirName);
            checkDirAndMkdirs(root);
        }

        if (fileName != null) {
            root = new File(root, fileName);
        }
    	return root;
    }

    /**
     * return mnt/sdcard/pkgName/file、file下的目录或者文件， 如果dirName、fileName均为null, 则返回cache目录
     * @param dirName 子目录
     * @param fileName 文件名
     * */
    public File getExternalStorageFile(String dirName, String fileName) {
        if (!hasExternalStorage()) {
            return null;
        }
        File root =  getExternalStorageRoot("files");
        checkDirAndMkdirs(root);

        if (dirName != null) {
            //需要创建目录
            root = new File(root, dirName);
            checkDirAndMkdirs(root);
        }

        if (fileName != null) {
            root = new File(root, fileName);
        }
        return root;
    }

    /**
     * 获取缓存文件，优先缓存在sd卡中
     * SD存在，调用{@link #getExternalStorageCache}，否则调用{@link #getCachedFile}
     * @param dirName  目录名，可以为null
     * @param fileName 文件名，可以为null
     * @return
     */
    public File getExternalOrInternalCache(String dirName, String fileName) {
        if (!hasExternalStorage()) {
            return getCachedFile(dirName, fileName);
        } else {
            return getExternalStorageCache(dirName, fileName);
        }
    }

    /**
     * 获取files下的缓存文件，优先缓存在sd卡中
     * SD存在，调用{@link #getExternalStorageFile}，否则调用{@link #getCachedFile}
     * @param dirName  目录名，可以为null
     * @param fileName 文件名，可以为null
     * @return
     */
    public File getExternalOrInternalFile(String dirName, String fileName) {
        if (!hasExternalStorage()) {
            return getAppFile(dirName, fileName);
        } else {
            return getExternalStorageFile(dirName, fileName);
        }
    }

    /***
     * 确保目录存在,如果不存在会创建该目录
     * @return
     */
    public boolean checkDirAndMkdirs(File dir) {
        boolean exsited = false;
        if (!dir.exists()) {
            exsited = dir.mkdirs();
        } else {
            exsited = true;
        }
        return exsited;
    }

    /**提示没有SD卡可用*/
    public void showNoSDCardMountedMessage() {
    	showMessage(R.string.msg_sd_unavailable);
    }
    //add by chenkai, for Usage, 2013-06-05 end
    
    public void hideInputMethod(IBinder token) {
    	if (mImMgr != null) {
    		mImMgr.hideSoftInputFromWindow(token, 0);
    	}
    }

	public static String getDeviceInfo(Context context) {
	    try{
	        JSONObject json = new JSONObject();
	        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	  
	        String device_id = tm.getDeviceId();
	      
	        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	          
	        String mac = wifi.getConnectionInfo().getMacAddress();
	        json.put("mac", mac);
	      
	       if(TextUtils.isEmpty(device_id) ){
	            device_id = mac;
	       }
	      
	      if( TextUtils.isEmpty(device_id) ){
	           device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
	      }
	      
	      json.put("device_id", device_id);
	      return json.toString();
	    }catch(Exception e){
	      e.printStackTrace();
	    }
	    return null;
	}

    /**
     * 拷贝内容到剪贴簿
     */
    ClipboardManager mClipboardManager = null;
    public void copyToClipboard(CharSequence content) {
        if (mClipboardManager == null) {
            mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        }
        if (mClipboardManager != null) {
            mClipboardManager.setText(content);
        }

    }

    /**
     * 获取剪贴簿的内容
     * @return
     */
    public CharSequence pasteFromClipboard() {
        if (mClipboardManager == null) {
            mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        }
        if (mClipboardManager != null) {
            return mClipboardManager.getText();
        }

        return null;
    }

    /**
     * 创建使用移动网络提示对话框构建器
     * @return
     */
    public AlertDialogWrapper onCreateMobileConfirmDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_use_mobile_confirm, null);
        AlertDialogWrapper buildWrapper = new AlertDialogWrapper(context);
        buildWrapper.mBuilder.setTitle(R.string.dialog_use_mobile_title);
        buildWrapper.setView(view);
        return buildWrapper;
    }

    /**
     * 创建使用移动网络提示对话框构建器
     * @return
     */
    public AlertDialog onCreateNoNetworkDialog(Context context) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_no_network_title)
                .setMessage(R.string.dialog_no_network_message)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }


    /**
     * src添加DES加密
     * @param src
     * @param password 如果为null, 不进行加密， 8的倍数
     */
    public static String desEnCrypto(String src, String password) {
        if (TextUtils.isEmpty(password)) {
            return src;
        }
        try {
            int len = password.length();
            if (len > 8) {
                password = password.substring(0, 8);
            } else {
                StringBuilder sb = new StringBuilder(password);
                for (int index = len; index < 8; index++) {
                    sb.append("w");
                }
                password = sb.toString();
            }
            DebugUtils.logD(TAG, "desEnCrypto src=" + src + ",password=" + password);
            return SecurityUtils.DES.enCrypto(src.getBytes("utf-8"), password);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return src;
    }

    /**
     * DES解密
     * @param src
     * @param password 如果为null, 不进行加密， 8的倍数
     */
    public static String desDeCrypto(String src, String password) {
        if (TextUtils.isEmpty(password)) {
            return src;
        }
        try {
            int len = password.length();
            if (len > 8) {
                password = password.substring(0, 8);
            } else {
                StringBuilder sb = new StringBuilder(password);
                for (int index = len; index < 8; index++) {
                    sb.append("w");
                }
                password = sb.toString();
            }
            DebugUtils.logD(TAG, "desDeCrypto src=" + src + ",password=" + password);
            return SecurityUtils.DES.deCrypto(src, password);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return src;
    }
                  
}
