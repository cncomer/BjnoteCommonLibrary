package com.shwy.bestjoy.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import java.util.LinkedList;
import java.util.List;

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
		        DebugUtils.logD(TAG, "mobile:"+(mobileInfo != null ? mobileInfo.isConnected() : "unsupport") + 
		        		"\n"+"wifi:"+ (wifiInfo != null ? wifiInfo.isConnected() : "unsupport") + 
		        		"\n"+"active:"+ (activeInfo != null ? activeInfo.getTypeName():"none"));
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
			throw new RuntimeException("You must call ComConnectivityManager.getInstance().setContext() in Application.onCreate()");
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
		return activeInfo != null && activeInfo.isConnected() && activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}
	
	public boolean isMobileConnected() {
		NetworkInfo activeInfo = mCm.getActiveNetworkInfo(); 
		return activeInfo != null && activeInfo.isConnected() && activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
	}
	
	public boolean isConnected() {
		NetworkInfo activeInfo = mCm.getActiveNetworkInfo(); 
		return activeInfo != null && activeInfo.isConnected();
	}
	
	public void startConnectivityMonitor() {
		IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
		mContext.registerReceiver(mBroadcastReceiver, filter);
	}
    public void endConnectivityMonitor() {
    	if (mContext != null) {
    		mContext.unregisterReceiver(mBroadcastReceiver);
    	}
	}
}
