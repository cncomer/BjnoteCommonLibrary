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
import java.util.List;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.telephony.SmsMessage;

import com.shwy.bestjoy.bjnotecommonlibrary.R;

/**
 * This class provides the constants to use when sending an Intent to Barcode Scanner.
 * These strings are effectively API and cannot be changed.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class Intents {
	public static final String EXTRA_NAME="name";
	public static final String EXTRA_TEL="tel";
	public static final String EXTRA_BID="bid";
	public static final String EXTRA_EMAIL="email";
	public static final String EXTRA_ADDRESS="address";
	public static final String EXTRA_ORG="org";
	public static final String EXTRA_CLOUDURL="cloudurl";
	public static final String EXTRA_TITLE="title";
	public static final String EXTRA_NOTE="note";
	public static final String EXTRA_TYPE="type";
	public static final String EXTRA_PASSWORD="password";
	public static final String EXTRA_TOPIC_ID="topic_id";
	public static final String EXTRA_MD="md";
	public static final String EXTRA_QMD="qmd";
	public static final String EXTRA_ID="_id";
	public static final String EXTRA_SHOW_DOWNLOAD="show_download";
	
	public static final String EXTRA_POSITION = "position";
	public static final String EXTRA_PHOTOID = "photoid";
	public static final String EXTRA_UPDATE = "update";
	
	public static final String EXTRA_URI = "uri";
	
	//商品额外数据 begin
	/**商品21位编码*/
	public static final String EXTRA_PD="PD";
	/**商品服务器自增号*/
	public static final String EXTRA_SID="SID";
	/**型号索引*/
	public static final String EXTRA_KY="KY";
	/**商品编号*/
	public static final String EXTRA_SN="SN";
	/**商品型号*/
	public static final String EXTRA_MODEL="NO";
	/**商品品牌*/
	public static final String EXTRA_BRAND="BRAND";
	/**商品是否已经登记*/
	public static final String EXTRA_HAS_REGISTERED="hasRegistered";
	public static final String EXTRA_DATE="date";
	/**本地发票文件路径*/
	public static final String EXTRA_BILL="bill";
	/**本地商品预览图文件路径*/
	public static final String EXTRA_AVATOR="avator";
	/**销售员MD*/
	public static final String EXTRA_SMD="smd";
	/**销售员MM*/
	public static final String EXTRA_SMM="smm";
	public static final String EXTRA_SMMV="smm";
	public static final String EXTRA_MMV="avator";
	/**商品保修年*/
	public static final String EXTRA_WY="wy";
	public static final String EXTRA_GOODS_TEL="goods_tel";
	public static final String EXTRA_GOODS_NAME="goods_name";
	public static final String EXTRA_GOODS_ADDRESS="goods_address";
	public static final String EXTRA_SCAN_TASK="scan_task";
	//商品额外数据 end
	public static final String EXTRA_PROGRESS="progress";
	public static final String EXTRA_RESULT="result";
	public static final String EXTRA_PROGRESS_MAX="progress_max";
	
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
    		context.startActivity(intent);
    		try {
    			context.startActivity(intent);
    		} catch (ActivityNotFoundException e) {
    			AlertDialog.Builder builder = new AlertDialog.Builder(context);
    			builder.setMessage(R.string.msg_intent_failed);
    			builder.setPositiveButton(android.R.string.ok, null);
    			builder.show();
    		}
    	}
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
     * 检查是否安装了Google Map
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
     * 调用地图定位位置
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
}
