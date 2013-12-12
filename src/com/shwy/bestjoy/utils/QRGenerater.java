package com.shwy.bestjoy.utils;

import java.util.Hashtable;

import android.app.PendingIntent.CanceledException;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRGenerater extends Thread {
	private static final String TAG = "QRGenerater";
	private static String[] contentFormats = new String[]{
		"%s",
		"%s",
		"MECARD:N:%s;TEL:%s;ORG:%s;TITLE:%s;URL:%s;;",
		"geo:%s,%s",
		"WIFI:T:%s;S:%s;P:%s;;"
	};
	
	public static final int URL_CONTENT_FORMAT = 0;
	public static final int EMAIL_CONTENT_FORMAT = 1;
	public static final int MECARD_CONTENT_FORMAT = 2;
	public static final int GEO_CONTENT_FORMAT = 3;
	public static final int WIFI_CONTENT_FORMAT = 4;
	/**为了可扩展性，如果增加编码类型，需要修改MIN和MAX的值，表示的是contentFormats的索引取值范围*/
	private static final int MIN_VALUE_FOR_CONTENT_FORMAT = 0;
	private static final int MAX_VALUE_FOR_CONTENT_FORMAT = 4;
	
	private static final int WHITE = 0xFFFFFFFF;
	private static final int BLACK = 0xFF000000;
	
	private static final int on = View.VISIBLE;
	private static final int off = View.GONE;
	
	private boolean isCancel = false;
	private int mQrContentFormat = 0;
	private String mQrContent;
	/**
	 *A class which wraps a 2D array of bytes. The default usage is signed. 
	 *If you want to use it as a unsigned container, it's up to you to do byteValue & 0xff 
	 *at each location. JAVAPORT: The original code was a 2D array of ints, but since it only
	 * ever gets assigned -1, 0, and 1, I'm going to use less memory and go with bytes.
	 */
	private BitMatrix byteMatrix;
	private Bitmap mBitmap;
	
	private int mHeight=300;
	private int mWidth=300;
	
	public static interface QRGeneratorFinishListener {
		void onQRGeneratorFinish(Bitmap bitmap);
	}
	
	private QRGeneratorFinishListener mQRGeneratorFinishListener;
	
	public void setQRGeneratorFinishListener(QRGeneratorFinishListener listener) {
		mQRGeneratorFinishListener = listener;
	}
	
	public QRGenerater(String content) {
		if (DebugUtils.DEBUG_QRGEN) Log.v(TAG, "create QRThread for " + content);
		mQrContent = content;
	}
	
	public void setDimens(int height, int width) {
		DebugUtils.logD(TAG, "setDimens height " + height + " width "  + width);
		mHeight = height;
		mWidth = width;
	}
	
	public void setCancelStatus(boolean isCancel) {
		this.isCancel = isCancel;
	}
	
	public static String getContentFormat(int contentFormatType) {
		if (contentFormatType < MIN_VALUE_FOR_CONTENT_FORMAT || URL_CONTENT_FORMAT > MAX_VALUE_FOR_CONTENT_FORMAT) {
			throw new IllegalArgumentException("contentFormatType must be in the range from " + MIN_VALUE_FOR_CONTENT_FORMAT + " to " + MAX_VALUE_FOR_CONTENT_FORMAT );
		}
		return contentFormats[contentFormatType];
	}
	
	
	@Override
	public void run() {
		if (DebugUtils.DEBUG_QRGEN) {
			Log.v(TAG, "start thread");
			Log.v(TAG, "encoding " + mQrContent);
		}
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		try {
			Hashtable<EncodeHintType,Object> hints = new Hashtable<EncodeHintType,Object>(2);
		      hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			byteMatrix = qrCodeWriter.encode(
					        mQrContent,
							BarcodeFormat.QR_CODE, 
							mWidth,
							mHeight, 
							hints);
			int width = byteMatrix.getWidth();
			int height = byteMatrix.getHeight();
			int[] pixels = new int[width * height];
		    // All are 0, or black, by default
		    for (int y = 0; y < height; y++) {
		      int offset = y * width;
		      for (int x = 0; x < width; x++) {
		    	if(isCancel)throw new CanceledException();
		        pixels[offset + x] = byteMatrix.get(x, y) ? BLACK : WHITE;
		      }
		    }

		    mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		    mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		    
		    if (DebugUtils.DEBUG_QRGEN) Log.v(TAG, "end thread");
		} catch (WriterException e) {
			e.printStackTrace(); 
			mBitmap = null;
		} catch (CanceledException e) {
			e.printStackTrace();
			mBitmap = null;
		}
		if (mQRGeneratorFinishListener != null) mQRGeneratorFinishListener.onQRGeneratorFinish(mBitmap);
	}
	
	public Bitmap getBitmap() {
		return mBitmap;
	}
	
}
