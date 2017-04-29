package com.shwy.bestjoy.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class DevicesUtils {

	private static final String TAG = "DevicesUtils";
	private static DevicesUtils INSTANCE = new DevicesUtils();
	private Context mContext;

	private DevicesUtils(){}

	public static final String DEVICE_TYPE = "Android";
	
	public static DevicesUtils getInstance() {
		return INSTANCE;
	}
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	public String getImei() {
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	/**
	 * 需要先连上wifi获取到
	 * @return
	 */
	public String getMac() {
		WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	/**
	 * 获取本地IPV4地址
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


	public String getIMSI() {
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();
	}

	public String getDeviceModelName() {
		return Build.MODEL;
	}


	/** * 判断是否包含SIM卡 * * @return 状态 */
	public boolean hasSimCard() {
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		int simState = tm.getSimState();
		boolean result = true;
		switch (simState) {
			case TelephonyManager.SIM_STATE_ABSENT:
				result = false; // 没有SIM卡
				break;
			case TelephonyManager.SIM_STATE_UNKNOWN:
				result = false;
				break;
		}
		Log.d(TAG, result ? "有SIM卡" : "无SIM卡");
		return result;
	}
	/** 判断SIM卡是否已经准备好了 */
	public boolean hasSimCardReady() {
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		int simState = tm.getSimState();
		boolean result = false;
		switch (simState) {
			case TelephonyManager.SIM_STATE_READY:
				result = true;
				break;
		}
		Log.d(TAG, result ? "SIM卡 is ready" : "SIM卡 isn't ready");
		return result;
	}
}
