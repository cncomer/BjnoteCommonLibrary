package com.shwy.bestjoy.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class BitmapUtils {

	/**将给定的图形转换为圆角的*/
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
}
