package com.shwy.bestjoy.utils;

import android.util.Log;

public class DebugUtils {

	public static final boolean DEBUG_LIFE = false;
	public static final boolean DEBUG_DECODE_THREAD = false;
	public static final boolean DEBUG_DOWNLAOD_THREAD = false;
	public static final boolean DEBUG_BJFILE = false;
	public static final boolean DEBUG_QRGEN = false;
	public static final boolean DEBUG_ADD_CONTACT = true;
	public static final boolean DEBUG = true;
	public static final boolean DEBUG_NETWORK = true;
	public static final boolean DEBUG_VCARD_PARSE = false;
	
	public static final boolean CACHEDEBUG = false;
	public static final boolean DEBUG_SMS = true;
	
	public static final boolean DEBUG_PHOTOMANAGER = true;
	
	private static final boolean DEBUG_PROVIDER = true;
	
	private static final boolean DEBUG_CONTACT_DOWNLOAD = true;
	/**�Ƿ��ӡɾ���ļ�����Ϣ*/
	public static final boolean DEBUG_DELETE_FILES = true;
	
	public static final void logD(String TAG, String MSG) {
		if (DEBUG) {
			Log.d(TAG, MSG==null?"":MSG);
		}
	}
	
	public static final void logNetworkOp(String TAG, String MSG) {
		if (false) {
			Log.d(TAG, MSG==null?"":MSG);
		}
	}
	
	public static final void logLife(String TAG, String MSG) {
		if (DEBUG_LIFE) {
			Log.w(TAG, MSG==null?"":MSG);
		}
	}
	
	public static final void logE(String TAG, String MSG) {
	    Log.e(TAG, ""+MSG);
	}
    public static final void logW(String TAG, String MSG) {
        Log.w(TAG, ""+MSG);
    }
	
	public static final void logVcardParse(String TAG, String MSG) {
		if (DEBUG_VCARD_PARSE) {
			Log.w(TAG, MSG==null?"":MSG);
		}
	}
	
	public static final void logExchangeBCParse(String TAG, String MSG) {
		if (true) {
			Log.w(TAG, MSG==null?"":MSG);
		}
	}
	
	public static final void logExchangeBC(String TAG, String MSG) {
		if (false) {
			Log.w(TAG, MSG==null?"":MSG);
		}
	}
	
	public static final void logSms(String TAG, String MSG) {
		if (DEBUG_SMS) {
			Log.w(TAG, MSG==null?"":MSG);
		}
	}
	
	public static final void logDeleteFiles(String TAG, String MSG) {
		if (DEBUG_DELETE_FILES) {
			Log.d(TAG, MSG==null?"":MSG);
		}
	}
	
	public static final void logPhotoUtils(String tag, String msg) {
		if (DEBUG_PHOTOMANAGER) Log.w(tag, msg==null?"":msg);
	}
	
	public static final void logProvider(String tag, String msg) {
		if (DEBUG_PROVIDER) Log.w(tag, msg==null?"":msg);
	}
	
	public static final void logContactAsyncDownload(String tag, String msg) {
		if (DEBUG_CONTACT_DOWNLOAD) Log.w(tag, msg==null?"":msg);
	}

	public static void main(String[] args) {
		logD("hello", null);
	}
}
