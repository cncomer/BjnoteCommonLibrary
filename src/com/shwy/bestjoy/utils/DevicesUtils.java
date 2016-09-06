package com.shwy.bestjoy.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

public class DevicesUtils {

	private static final String TAG = "DevicesUtils";
	private static DevicesUtils INSTANCE = new DevicesUtils();
	private Context mContext;

	private DevicesUtils(){};
	
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
	
	public String getIMSI() {
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();
	}

	public String getDeviceModelName() {
		return Build.MODEL;
	}
}
