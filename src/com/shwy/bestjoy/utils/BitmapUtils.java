package com.shwy.bestjoy.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {
	private static final String TAG = "BitmapUtils";
	private Context mContext;
	private static final long MAX_BILL_BITMAP_SIZE = 1920 * 1440; //100k
	
	private static int mScreenWidth, mScreenHeight;

	private static BitmapUtils mInstance = new BitmapUtils();
	
	private BitmapUtils(){};
	
	public static BitmapUtils getInstance() {
		return mInstance;
	}
	
	public void setContext(Context context) {
		mContext = context;
		
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		mScreenHeight = dm.heightPixels;
		mScreenWidth = dm.widthPixels;
	}

	public static Bitmap scaleBitmapFile(File bitmapFile, int width, int height) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		if (bitmapFile.length() > MAX_BILL_BITMAP_SIZE) {
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), opt);
			int srcWidth = opt.outWidth;
			int srcHeight = opt.outHeight;
			float scaleWidth = 1.0f;
            float scaleHeight =1.0f;
			
			if(srcWidth > width || srcHeight > height) {
				scaleWidth = 1.0f * srcWidth / width;
				scaleHeight = 1.0f * srcHeight / height;
			}
			opt.inJustDecodeBounds = false;
			opt.inSampleSize = (int) Math.max(scaleWidth, scaleHeight);
			Bitmap scaleBitmap = BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), opt);
			Matrix matrix = new Matrix();
			matrix.setScale(scaleWidth, scaleHeight);
			return Bitmap.createBitmap(scaleBitmap, 0, 0, srcWidth, srcHeight, matrix, false);
		} else {
			return BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), opt);
		}
		
	}

	public static Bitmap getSuitedBitmap(Resources resources, int id, int width, int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		try {
			options.inJustDecodeBounds = true;
			Bitmap tmp = BitmapFactory.decodeResource(resources, id, options);
			if (tmp != null) {
				tmp.recycle();
				tmp = null;
			}

			Log.d(TAG, "extractThumbNail: round=" + width + "x" + height);
			final double beY = options.outHeight * 1.0 / height;
			final double beX = options.outWidth * 1.0 / width;
			Log.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
			options.inSampleSize = (int) (beY < beX ? beX : beY);
			if (options.inSampleSize <= 1) {
				options.inSampleSize = 1;
			}

			// NOTE: out of memory error
			while (options.outHeight * options.outWidth / options.inSampleSize > MAX_BILL_BITMAP_SIZE) {
				options.inSampleSize++;
			}

			int newHeight = height;
			int newWidth = width;
			if (beY < beX) {
				newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
			} else {
				newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
			}

			options.inJustDecodeBounds = false;

			Log.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + options.outWidth + "x" + options.outHeight + ", sample=" + options.inSampleSize);
			Bitmap bm = BitmapFactory.decodeResource(resources, id, options);
			if (bm == null) {
				Log.e(TAG, "bitmap decode failed");
				return null;
			}

			Log.i(TAG, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
			final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
			if (scale != null) {
				bm.recycle();
				bm = scale;
			}

			return bm;

		} catch (final OutOfMemoryError e) {
			Log.e(TAG, "decode bitmap failed: " + e.getMessage());
			options = null;
		}

		return null;
	}
	
	public static Bitmap scaleBitmapFile(Bitmap src, int width, int height) {
		if (src != null) {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			int srcWidth = src.getWidth();
			int srcHeight = src.getHeight();
			float scale = 1.0f;
			
			if(srcWidth > width || srcHeight > height) {
				float scaleWidth = 1.0f * width / srcWidth;
				float scaleHeight = 1.0f * height / srcHeight;
				scale = Math.min(scaleWidth, scaleHeight);
			}
			opt.inJustDecodeBounds = false;
			Matrix matrix = new Matrix();
			matrix.setScale(scale, scale);
			Bitmap bitmap = Bitmap.createBitmap(src, 0, 0, srcWidth, srcHeight, matrix, false);
			src.recycle();
			return bitmap;
		}
		return null;
		
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {
	             final int heightRatio = Math.round((float) height/ (float) reqHeight);
	             final int widthRatio = Math.round((float) width / (float) reqWidth);
	             inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	    return inSampleSize;
	}
	
	public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;

	    return BitmapFactory.decodeFile(filePath, options);
	}
	
	
	public static Bitmap rotateBitmap(Bitmap bitmap, float rotateDegree) {
		Matrix matrix = new Matrix();
		matrix.postRotate(rotateDegree);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
		bitmap.recycle();
		return newBitmap;
	}
	
	public static String bitmapToString(Bitmap bitmap, int quality) {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
	        byte[] b = baos.toByteArray();
	        return Base64.encodeToString(b, Base64.DEFAULT);
	}
	
	public static boolean bitmapToFile(Bitmap bitmap, File toSave, int quality) {
		if (bitmap != null) {
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(toSave);
				return bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}
	
	public static Bitmap getXBitmap(Bitmap mask, Bitmap dest) {
		Bitmap bitmap = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setFilterBitmap(false);
		paint.setAntiAlias(true);
		canvas.drawBitmap(dest, 0, 0, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawBitmap(mask, 0, 0, paint);
		return bitmap;
	}

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
	
	public static Bitmap getScaledBitmap(Bitmap src) {
		int srcHeight = src.getHeight();
		int srcWidth = src.getWidth();
		float sx = 1.0f;
		if (mScreenWidth > 0) {
			sx = 1.0f * mScreenWidth / srcWidth;
		}
		
		Matrix matrix = new Matrix();
		matrix.postScale(sx, sx);
		return Bitmap.createBitmap(src, 0, 0, srcWidth, srcHeight, matrix, true);
	}



	private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;
	public static Bitmap extractThumbNail(final String path, final int height, final int width, final boolean crop) {
		Assert.assertTrue(path != null && !path.equals("") && height > 0 && width > 0);

		BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			options.inJustDecodeBounds = true;
			Bitmap tmp = BitmapFactory.decodeFile(path, options);
			if (tmp != null) {
				tmp.recycle();
				tmp = null;
			}

			Log.d(TAG, "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop);
			final double beY = options.outHeight * 1.0 / height;
			final double beX = options.outWidth * 1.0 / width;
			Log.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
			options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
			if (options.inSampleSize <= 1) {
				options.inSampleSize = 1;
			}

			// NOTE: out of memory error
			while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
				options.inSampleSize++;
			}

			int newHeight = height;
			int newWidth = width;
			if (crop) {
				if (beY > beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			} else {
				if (beY < beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			}

			options.inJustDecodeBounds = false;

			Log.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + options.outWidth + "x" + options.outHeight + ", sample=" + options.inSampleSize);
			Bitmap bm = BitmapFactory.decodeFile(path, options);
			if (bm == null) {
				Log.e(TAG, "bitmap decode failed");
				return null;
			}

			Log.i(TAG, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
			final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
			if (scale != null) {
				bm.recycle();
				bm = scale;
			}

			if (crop) {
				final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1, (bm.getHeight() - height) >> 1, width, height);
				if (cropped == null) {
					return bm;
				}

				bm.recycle();
				bm = cropped;
				Log.i(TAG, "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
			}
			return bm;

		} catch (final OutOfMemoryError e) {
			Log.e(TAG, "decode bitmap failed: " + e.getMessage());
			options = null;
		}

		return null;
	}


	public static Bitmap decodeResourceInSampleSize(Resources resources, int drawableId) {

		BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			options.inJustDecodeBounds = true;
			Bitmap tmp = BitmapFactory.decodeResource(resources, drawableId, options);
			if (tmp != null) {
				tmp.recycle();
				tmp = null;
			}

			options.inSampleSize = 1;
			// NOTE: out of memory error
			while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
				options.inSampleSize++;
			}

			options.inJustDecodeBounds = false;

			Bitmap bm = BitmapFactory.decodeResource(resources, drawableId, options);
			if (bm == null) {
				Log.e(TAG, "bitmap decode failed");
				return null;
			}
			return bm;

		} catch (final OutOfMemoryError e) {
			Log.e(TAG, "decode bitmap failed: " + e.getMessage());
			options = null;
		}

		return null;
	}

	public static Bitmap decodeResourceInSampleSize(File file) {

		BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			options.inJustDecodeBounds = true;
			Bitmap tmp = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
			if (tmp != null) {
				tmp.recycle();
				tmp = null;
			}

			options.inSampleSize = 1;
			// NOTE: out of memory error
			while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
				options.inSampleSize++;
			}

			options.inJustDecodeBounds = false;

			Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
			if (bm == null) {
				Log.e(TAG, "bitmap decode failed");
				return null;
			}
			return bm;

		} catch (final OutOfMemoryError e) {
			Log.e(TAG, "decode bitmap failed: " + e.getMessage());
			options = null;
		}

		return null;
	}
}
