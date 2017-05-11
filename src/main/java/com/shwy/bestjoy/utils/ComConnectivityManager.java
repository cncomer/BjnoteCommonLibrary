package com.shwy.bestjoy.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
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


	/**
	 * 获取本机当前的ip地址
	 * @return
	 */
	public String getIpAddress() {
		String ip = "";
		NetworkInfo mobileNetworkInfo = mCm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetworkInfo = mCm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mobileNetworkInfo.isConnected()) {
			ip = getLocalIpAddress();
		}else if(wifiNetworkInfo.isConnected()) {
			WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ipAddress = wifiInfo.getIpAddress();
			ip = intToIp(ipAddress);
			System.out.println("wifi_ip地址为------"+ip);
		}
		DebugUtils.logD(TAG, "本地ip="+ip);

		return ip;
	}

	/**
	 * 获取本机PV4地址
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			String ipv4;
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						ipv4=inetAddress.getHostAddress();
						if (InetAddressUtils.isIPv4Address(ipv4)) {
							return ipv4;
						}
					}
				}
			}
		} catch (SocketException ex) {
			DebugUtils.logE(TAG, "WifiPreference IpAddress " + ex.toString());
		}

		return null;
	}

	public static String intToIp(int ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}
}
