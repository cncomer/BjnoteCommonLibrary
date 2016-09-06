/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shwy.bestjoy.utils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.SmsMessage;

import com.shwy.bestjoy.R;

/**
 * This class provides the constants to use when sending an Intent to Barcode Scanner.
 * These strings are effectively API and cannot be changed.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class Intents {
	public static final String EXTRA_NAME="extra_name";
	public static final String EXTRA_TEL="extra_tel";
	public static final String EXTRA_BID="extra_bid";
	public static final String EXTRA_EMAIL="extra_email";
	public static final String EXTRA_ADDRESS="extra_address";
	public static final String EXTRA_ORG="extra_org";
	public static final String EXTRA_CLOUDURL="extra_cloudurl";
	public static final String EXTRA_TITLE="extra_title";
	public static final String EXTRA_NOTE="extra_note";
	public static final String EXTRA_TYPE="extra_type";
	public static final String EXTRA_SOURCE="extra_source";
	public static final String EXTRA_TARGET="extra_target";
	public static final String EXTRA_PASSWORD="extra_password";
	public static final String EXTRA_TOPIC_ID="extra_topic_id";
	public static final String EXTRA_MD="extra_md";
	public static final String EXTRA_QMD="extra_qmd";
	public static final String EXTRA_ID="extra_id";
	public static final String EXTRA_SHOW_DOWNLOAD="extra_show_download";
	
	public static final String EXTRA_POSITION = "extra_position";
	public static final String EXTRA_PHOTOID = "extra_photoid";
	public static final String EXTRA_UPDATE = "extra_update";
	/**查询参数*/
	public static final String EXTRA_QUERY="extra_query";
	public static final String EXTRA_ONLY_QUERY_VALUE="extra_only_query_value";
	public static final String EXTRA_JSON_QUERY="extra_json_query";
	public static final String EXTRA_URI = "extra_uri";

	public static final String EXTRA_DATA_TYPE="extra_data_type";
	
	//��Ʒ������� begin
	/**��Ʒ21λ����*/
	public static final String EXTRA_PD="extra_PD";
	/**��Ʒ������������*/
	public static final String EXTRA_SID="extra_SID";
	/**�ͺ�����*/
	public static final String EXTRA_KY="extra_KY";
	/**��Ʒ���*/
	public static final String EXTRA_SN="extra_SN";
	/**��Ʒ�ͺ�*/
	public static final String EXTRA_MODEL="extra_NO";
	/**��ƷƷ��*/
	public static final String EXTRA_BRAND="extra_BRAND";
	/**��Ʒ�Ƿ��Ѿ��Ǽ�*/
	public static final String EXTRA_HAS_REGISTERED="extra_hasRegistered";
	public static final String EXTRA_DATE="extra_date";
	/**���ط�Ʊ�ļ�·��*/
	public static final String EXTRA_BILL="extra_bill";
	/**������ƷԤ��ͼ�ļ�·��*/
	public static final String EXTRA_AVATOR="extra_avator";
	/**����ԱMD*/
	public static final String EXTRA_SMD="extra_smd";
	/**����ԱMM*/
	public static final String EXTRA_SMM="extra_smm";
	public static final String EXTRA_SMMV="extra_smm";
	public static final String EXTRA_MMV="extra_avator";
	/**��Ʒ������*/
	public static final String EXTRA_WY="extra_wy";
	public static final String EXTRA_GOODS_TEL="extra_goods_tel";
	public static final String EXTRA_GOODS_NAME="extra_goods_name";
	public static final String EXTRA_GOODS_ADDRESS="extra_goods_address";
	public static final String EXTRA_SCAN_TASK="extra_scan_task";
	//��Ʒ������� end
	public static final String EXTRA_PROGRESS="extra_progress";
	public static final String EXTRA_RESULT="extra_result";
	public static final String EXTRA_PROGRESS_MAX="extra_progress_max";

	/**主题*/
	public static final String EXTRA_THEME = "extra_theme";
	
	public static final class MonitorService {
		public static final String ACTION_START_MONITOR = "com.shwy.bestjoy.bjnote.client.startmonitor";
	}
  
	private Intents() {
    }

  public static final class Scan {
    /**
     * Send this intent to open the Barcodes app in scanning mode, find a barcode, and return
     * the results.
     */
    public static final String ACTION = "com.google.zxing.client.android.SCAN";
    
    /**
     * By default, sending Scan.ACTION will decode all barcodes that we understand. However it
     * may be useful to limit scanning to certain formats. Use Intent.putExtra(MODE, value) with
     * one of the values below ({@link #PRODUCT_MODE}, {@link #ONE_D_MODE}, {@link #QR_CODE_MODE}).
     * Optional.
     *
     * Setting this is effectively shorthnad for setting explicit formats with {@link #SCAN_FORMATS}.
     * It is overridden by that setting.
     */
    public static final String MODE = "SCAN_MODE";

    /**
     * Comma-separated list of formats to scan for. The values must match the names of
     * {@link com.google.zxing.BarcodeFormat}s, such as {@link com.google.zxing.BarcodeFormat#EAN_13}.
     * Example: "EAN_13,EAN_8,QR_CODE"
     *
     * This overrides {@link #MODE}.
     */
    public static final String SCAN_FORMATS = "SCAN_FORMATS";

    /**
     * @see com.google.zxing.DecodeHintType#CHARACTER_SET
     */
    public static final String CHARACTER_SET = "CHARACTER_SET";

    /**
     * Decode only UPC and EAN barcodes. This is the right choice for shopping apps which get
     * prices, reviews, etc. for products.
     */
    public static final String PRODUCT_MODE = "PRODUCT_MODE";

    /**
     * Decode only 1D barcodes (currently UPC, EAN, Code 39, and Code 128).
     */
    public static final String ONE_D_MODE = "ONE_D_MODE";

    /**
     * Decode only QR codes.
     */
    public static final String QR_CODE_MODE = "QR_CODE_MODE";
    
    /**
     * Decode only Data Matrix codes.
     */
    public static final String DATA_MATRIX_MODE = "DATA_MATRIX_MODE";

    /**
     * If a barcode is found, Barcodes returns RESULT_OK to onActivityResult() of the app which
     * requested the scan via startSubActivity(). The barcodes contents can be retrieved with
     * intent.getStringExtra(RESULT). If the user presses Back, the result code will be
     * RESULT_CANCELED.
     */
    public static final String RESULT = "SCAN_RESULT";

    /**
     * Call intent.getStringExtra(RESULT_FORMAT) to determine which barcode format was found.
     * See Contents.Format for possible values.
     */
    public static final String RESULT_FORMAT = "SCAN_RESULT_FORMAT";

    /**
     * Setting this to false will not save scanned codes in the history.
     */
    public static final String SAVE_HISTORY = "SAVE_HISTORY";

    private Scan() {
    }
  }

  public static final class Encode {
    /**
     * Send this intent to encode a piece of data as a QR code and display it full screen, so
     * that another person can scan the barcode from your screen.
     */
    public static final String ACTION = "com.shwy.bestjoy.android.ENCODE";

    /**
     * The data to encode. Use Intent.putExtra(DATA, data) where data is either a String or a
     * Bundle, depending on the type and format specified. Non-QR Code formats should
     * just use a String here. For QR Code, see Contents for details.
     */
    public static final String DATA = "ENCODE_DATA";

    /**
     * The type of data being supplied if the format is QR Code. Use
     * Intent.putExtra(TYPE, type) with one of Contents.Type.
     */
    public static final String TYPE = "ENCODE_TYPE";
    
    /**
     * The barcode format to be displayed. If this isn't specified or is blank, 
     * it defaults to QR Code. Use Intent.putExtra(FORMAT, format), where
     * format is one of Contents.Format. 
     */
    public static final String FORMAT = "com.google.zxing.client.android.ENCODE_FORMAT";

    private Encode() {
    }
  }

  public static final class SearchBookContents {
    /**
     * Use Google Book Search to search the contents of the book provided.
     */
    public static final String ACTION = "com.google.zxing.client.android.SEARCH_BOOK_CONTENTS";

    /**
     * The book to search, identified by ISBN number.
     */
    public static final String ISBN = "ISBN";

    /**
     * An optional field which is the text to search for.
     */
    public static final String QUERY = "QUERY";

    private SearchBookContents() {
    }
  }
  
  public static final class WifiConnect {
	    /**
	     * Internal intent used to trigger connection to a wi-fi network.
	     */
	    public static final String ACTION = "com.shwy.bestjoy.intent.action.WIFI_CONNECT";

	    /**
	     * The network to connect to, all the configuration provided here.
	     */
	    public static final String SSID = "SSID";

	    /**
	     * The network to connect to, all the configuration provided here.
	     */
	    public static final String TYPE = "TYPE";

	    /**
	     * The network to connect to, all the configuration provided here.
	     */
	    public static final String PASSWORD = "PASSWORD";

	    private WifiConnect() {
	    }
	  }

  public static final class Share {
    /**
     * Give the user a choice of items to encode as a barcode, then render it as a QR Code and
     * display onscreen for a friend to scan with their phone.
     */
    public static final String ACTION = "com.google.zxing.client.android.SHARE";

    private Share() {
    }
  }
  
  public static final class History {

	    public static final String ITEM_NUMBER = "ITEM_NUMBER";

	    private History() {
	    }
	  }
  
  /**
   * Read the PDUs out of an {@link #SMS_RECEIVED_ACTION} or a
   * {@link #DATA_SMS_RECEIVED_ACTION} intent.
   *
   * @param intent the intent to read from
   * @return an array of SmsMessages for the PDUs
   */
  public static SmsMessage[] getMessagesFromIntent(
          Intent intent) {
      Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
      byte[][] pduObjs = new byte[messages.length][];

      for (int i = 0; i < messages.length; i++) {
          pduObjs[i] = (byte[]) messages[i];
      }
      byte[][] pdus = new byte[pduObjs.length][];
      int pduCount = pdus.length;
      SmsMessage[] msgs = new SmsMessage[pduCount];
      for (int i = 0; i < pduCount; i++) {
          pdus[i] = pduObjs[i];
          msgs[i] = SmsMessage.createFromPdu(pdus[i]);
      }
      return msgs;
  }
  
    public static final void dialPhone(Context context, String phoneNumber) {
		launchIntent(context, new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ phoneNumber)));
	}
    public static final void callPhone(Context context, String phoneNumber) {
		launchIntent(context, new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ phoneNumber)));
	}
  
    public static final void openURL(Context context, String url) {
	    launchIntent(context, new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	}
    
    public static final void launchIntent(Context context, Intent intent) {
    	if (intent != null) {
    		try {
				if (!(context instanceof Activity)) {
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				}
    			context.startActivity(intent);
    		} catch (ActivityNotFoundException e) {
    			AlertDialog.Builder builder = new AlertDialog.Builder(context);
    			builder.setMessage(R.string.s_msg_intent_failed);
    			builder.setPositiveButton(android.R.string.ok, null);
    			builder.show();
    		}
    	}
    }
    
    public static final void share(Context context, String title, String content) {
    	Intent intent = new Intent();
    	intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/*");                   
        intent.putExtra(Intent.EXTRA_TEXT, content);
        context.startActivity(Intent.createChooser(intent, title));
	}

	public static final void sendSms(Context context, String targetPhone, String content) {
		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + targetPhone));
		intent.putExtra("sms_body", content);
		context.startActivity(intent);
	}
    
    public static final void install(Context context, File file) {
    	Intent i = new Intent(); 
        i.setAction(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
        context.startActivity(i);
    }
    /**
     * ����Ƿ�װ��Google Map
     * @param context
     * @return
     */
    public static boolean checkGoogleMap(Context context){
    	boolean isInstallGMap = false;
    	List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
    	for (int i = 0; i < packs.size(); i++) {
    	    PackageInfo p = packs.get(i);
			if (p.versionName == null) { // system packages
			     continue;
			}
	    	if ("com.google.android.apps.maps".equals(p.packageName)) {
	    	    isInstallGMap = true;
	    	    break;
	    	}
    	}
    	return isInstallGMap;
    }
    
    /**
     * ���õ�ͼ��λλ��
     * @param context
     * @param location
     */
    public static void location(Context context, String location) {
    	Intent intent = null;
    	if (checkGoogleMap(context)) {
    		intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + location));
    	} else {
    		intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ditu.google.cn/maps?hl=zh&mrt=loc&q=" + location));
    	}
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    	context.startActivity(intent);
    }
    
    /**
     * http://ditu.google.cn/maps?hl=zh&mrt=loc&q=@31.27986,121.49864
     * @param context
     * @param location
     */
    public static void locationGoogleMap(Context context, String location) {
    	Intent intent = null;
    	if (checkGoogleMap(context)) {
    		intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + location));
    	} else {
    		intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ditu.google.cn/maps?hl=zh&mrt=loc&q=" + location));
    	}
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    	context.startActivity(intent);
    }
    /**
     * 如果手机端已经安装了百度地图，我们调用百度地图，否则调用网页端百度地图
     * 
     * 
     * 移动app调起百度地图举例，参考http://developer.baidu.com/map/wiki/index.php?title=uri/api/android
     * 
     * intent = Intent.getIntent("intent://map/marker?location=40.047669,116.313082&title=我的位置&content=百度奎科大厦&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end"); 
	   startActivity(intent); //启动调用 
     * 
     * 网页端参数说明请参考 http://developer.baidu.com/map/index.php?title=uri/api/web
     * 
     * http://api.map.baidu.com/marker?location=39.916979519873,116.41004950566&coord_type=bd09ll&title=我的位置&content=百度奎科大厦&output=html&src=appName
     * @param context
     * @param location lat<纬度>,lng<经度> 39.916979519873,116.41004950566&title=我的位置&content=百度奎科大厦&src=appName
     */
    public static void locationBaiduMap(Context context, String location) {
		Intent intent = null;
		if (isAppInstalled(context, "com.baidu.BaiduMap")) {
			try {
				intent = Intent.parseUri("intent://map/marker?location=" + location + "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", Intent.URI_INTENT_SCHEME);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.map.baidu.com/marker?location=" + location + "&output=html" + "&coord_type=bd09ll"));
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		context.startActivity(intent);
	}

	public static void locationBaiduMapForAdressName(Context context, String addressName) {
		Intent intent = null;
		if (isAppInstalled(context, "com.baidu.BaiduMap")) {
			try {
				intent = Intent.parseUri("intent://map/geocoder?address=" + addressName + "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", Intent.URI_INTENT_SCHEME);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.map.baidu.com/geocoder?address=" + addressName+"&output=html"));
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		context.startActivity(intent);
	}

	/**
	 * 导航
	 * //移动APP调起Android百度地图方式举例
	 intent = Intent.getIntent("intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving&region=西安&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");

	 http://api.map.baidu.com/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving&region=西安&output=html&src=yourCompanyName|yourAppName
	 * @param context
	 * @param origin 起点名称或经纬度，或者可同时提供名称和经纬度，此时经纬度优先级高，将作为导航依据，名称只负责展示。
	 * @param mode  导航模式，固定为transit、driving、walking，分别表示公交、驾车和步行
     */
	public static void directionBaiduMap(Context context, String origin, String destination, String mode, String src) {
		Intent intent = null;
		String para = "origin=" + origin + "&destination=" + destination + "&mode=" + mode + "&src=" + src;
		if (isAppInstalled(context, "com.baidu.BaiduMap")) {
			try {
				intent = Intent.parseUri("intent://map/direction?" + para + "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", Intent.URI_INTENT_SCHEME);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.map.baidu.com/direction?" + para + "&output=html"));
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		context.startActivity(intent);
	}
    
    public static boolean isAppInstalled(Context context, String pkgName) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }


	public static final void viewImage(Context context, File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "image/*");
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}
}
