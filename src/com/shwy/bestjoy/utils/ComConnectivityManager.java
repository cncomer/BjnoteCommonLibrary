package com.shwy.bestjoy.utils;

import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;

import com.shwy.bestjoy.bjnotecommonlibrary.R;

public class ComConnectivityManager {
	private static final String TAG = "ConnectivityManager";
	private static final ComConnectivityManager INSTANCE = new ComConnectivityManager();
	
	private Context mContext;
	private ConnectivityManager mCm;
	private List<ConnCallback> mConnCallbackList = new LinkedList<ConnCallback>();
	public static interface ConnCallback {
		//网络改变了
		public void onConnChanged(ComConnectivityManager cm);
	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
		        NetworkInfo mobileInfo = mCm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
		        NetworkInfo wifiInfo = mCm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
		        NetworkInfo activeInfo = mCm.getActiveNetworkInfo();  
		        DebugUtils.logD(TAG, "mobile:"+mobileInfo.isConnected() + 
		        		"\n"+"wifi:"+wifiInfo.isConnected() + 
		        		"\n"+"active:"+activeInfo.getTypeName());
		        synchronized(mConnCallbackList){
		        	for(ConnCallback callback : mConnCallbackList) {
		        		callback.onConnChanged(INSTANCE);
			        }
		        }
			}
		}
		
	};

	private ComConnectivityManager() {}
	
	public void setContext(Context context) {
		if (mContext == null) {
			mContext = context;
			mCm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			startConnectivityMonitor();
		}
	}
	
	public static ComConnectivityManager getInstance() {
		return INSTANCE;
	}
	
	public void addConnCallback(ConnCallback callback) {
		if (mContext == null) {
			throw new RuntimeException("You must call setContext()");
		}
		synchronized(mConnCallbackList) {
			if (!mConnCallbackList.contains(callback)) {
				mConnCallbackList.add(callback);
			}
		}
	}
	
	public void removeConnCallback(ConnCallback callback) {
		synchronized(mConnCallbackList) {
			if (mConnCallbackList.contains(callback)) {
				mConnCallbackList.remove(callback);
			}
		}
	}
	
	public boolean isWifiConnected() {
		NetworkInfo activeInfo = mCm.getActiveNetworkInfo(); 
		return activeInfo.isConnected() && activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}
	
	public boolean isMobileConnected() {
		NetworkInfo activeInfo = mCm.getActiveNetworkInfo(); 
		return activeInfo.isConnected() && activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
	}
	
	public boolean isConnected() {
		NetworkInfo activeInfo = mCm.getActiveNetworkInfo(); 
		return activeInfo.isConnected();
	}
	
	private void startConnectivityMonitor() {
		IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
		mContext.registerReceiver(mBroadcastReceiver, filter);
	}
    private void endConnectivityMonitor() {
    	mContext.unregisterReceiver(mBroadcastReceiver);
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
    public AlertDialog onCreateNoNetworkDialog() {
    	return new AlertDialog.Builder(mContext)
    	.setTitle(R.string.dialog_no_network_title)
    	.setMessage(R.string.dialog_no_network_message)
    	.setPositiveButton(android.R.string.ok, null)
    	.create();
    }
}
