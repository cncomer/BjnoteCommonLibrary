package com.shwy.bestjoy.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class BitmapUtils {

	/**�����ͼ��ת��ΪԲ�ǵ�*/
	public static Bitmap createRoundBitmap(Bitmap src, float roundPx) {
		Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(out);
		Rect rect = canvas.getClipBounds();
		RectF rectf = new RectF(rect);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.WHITE);
		canvas.drawRoundRect(rectf, roundPx, roundPx, paint);
		paint.setXfermode( new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(src, 0f, 0f, paint);
		return out;
	}
	
	private static BitmapUtils mInstance = new BitmapUtils();
	private Context mContext;
	
	private BitmapUtils(){};
	
	public static BitmapUtils getInstance() {
		return mInstance;
	}
	
	public void setContext(Context context) {
		mContext = context;
	}

	public static Bitmap[] getSuitedBitmaps(Context context, int[] resIds, int w, int h) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap[] bitmaps = new Bitmap[resIds.length];
		Resources res = context.getResources();
		int index = 0;
		for(int id:resIds) {
			// Decode image bounds
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(res, id, options);
			int sampleSize = 1;
			if (options.outHeight > h || options.outWidth > w) {
				if (options.outHeight > options.outWidth) {
					sampleSize = options.outHeight / h;
				} else {
					sampleSize = options.outWidth / w;
				}
			}
			
			options.inJustDecodeBounds = false;
			options.inSampleSize = sampleSize;
			bitmaps[index] = BitmapFactory.decodeResource(res, id, options);
			index++;
		}
		
		return bitmaps;
	}
}
