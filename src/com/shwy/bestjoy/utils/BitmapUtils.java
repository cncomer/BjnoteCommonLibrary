package com.shwy.bestjoy.utils;

import android.content.Context;
import android.content.Intent;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.shwy.bestjoy.ComApplication;

import junit.framework.Assert;

import net.bither.util.NativeUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class BitmapUtils {
	private static final String TAG = "BitmapUtils";
	private Context mContext;

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

	public static Intent createCaptureIntent(Uri uri) {
		Intent capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		capture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		return capture;
	}
	public static Intent createGalleryIntent() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		return intent;
	}

	public static Bitmap scaleBitmapFile(File bitmapFile, int width, int height) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		if (bitmapFile.length() > MAX_DECODE_PICTURE_SIZE) {
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), opt);
//			int srcWidth = opt.outWidth;
//			int srcHeight = opt.outHeight;
//			float scaleWidth = 1.0f;
//            float scaleHeight =1.0f;
//
//			if(srcWidth > width || srcHeight > height) {
//				scaleWidth = 1.0f * srcWidth / width;
//				scaleHeight = 1.0f * srcHeight / height;
//			}
			opt.inSampleSize = calculateInSampleSize(opt, width, height);
			opt.inJustDecodeBounds = false;
			Bitmap scaleBitmap = BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), opt);
			return ComThumbnailUtils.extractThumbnail(scaleBitmap, width, height, ComThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		} else {
			Bitmap scaleBitmap = BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), opt);
			return ComThumbnailUtils.extractThumbnail(scaleBitmap, width, height, ComThumbnailUtils.OPTIONS_RECYCLE_INPUT);
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
			while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
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
	
	public static Bitmap scaleBitmap(Bitmap source, int width, int height) {
		return ComThumbnailUtils.extractThumbnail(source, width, height);
		
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

//	    if (height > reqHeight || width > reqWidth) {
//			if (width > height) {
//				inSampleSize = Math.round((float)height / (float)reqHeight);
//			} else {
//				inSampleSize = Math.round((float)width / (float)reqWidth);
//			}
//	    }
		Log.d(TAG, "extractThumbNail: round=" + width + "x" + height);
		final double beY = options.outHeight * 1.0 / height;
		final double beX = options.outWidth * 1.0 / width;
		Log.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
		inSampleSize = (int) (beY < beX ? beX : beY);
		if (inSampleSize <= 1) {
			inSampleSize = 1;
		}
		int newHeight = height;
		int newWidth = width;
		// NOTE: out of memory error
		while (options.outHeight * options.outWidth / inSampleSize > MAX_DECODE_PICTURE_SIZE) {
			inSampleSize++;
		}
	    return inSampleSize;
	}
	
	public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
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

//	/**
//	 * @deprecated 请使用bitmapToString(File file)
//	 * @param bitmap
//	 * @param quality
//	 * @return
//	 */
//	public static String bitmapToString(Bitmap bitmap, int quality) {
//	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//	        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
//	        byte[] b = baos.toByteArray();
//	        return Base64.encodeToString(b, Base64.DEFAULT);
//	}
	public static String bitmapToString(File file) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileInputStream is = null;
		byte[] buffer = new byte[4096];
		int size;
		try {
			is = new FileInputStream(file);
			size = is.read(buffer);
			while (size >= 0) {
				baos.write(buffer, 0, size);
				size = is.read(buffer);
			}
			baos.flush();
			byte[] b = baos.toByteArray();
			baos.close();
			return Base64.encodeToString(b, Base64.DEFAULT);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			NetworkUtils.closeInputStream(is);
		}
		return null;
	}

	public static boolean bitmapToFile(Bitmap bitmap, File toSave, int quality) {
		if (bitmap != null) {
			//如果是64位，jpegbither库现在还不支持，我们使用自带的压缩
			if (ComApplication.getSystemProperty("ro.product.cpu.abi", "").contains("arm64-v8a")
					|| ComApplication.getSystemProperty("ro.product.cpu.abilist64", "").length() > 0
					|| ComApplication.getSystemProperty("ro.product.cpu.abilist", "").contains("arm64-v8a")) {
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(toSave);
					return bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					NetworkUtils.closeOutStream(out);
				}
			} else {
				return NativeUtil.compressBitmap(bitmap, quality, bitmap.getWidth(), bitmap.getHeight(), toSave.getAbsolutePath(), true);
			}

		}
		return false;
	}

	/**
	 *
	 * @param bitmap
	 * @param toSave
	 * @param quality  质量
	 * @param reqWidth 指定保存文件的图片宽度
	 * @param reqHeight 指定保存文件的图片高度
	 * @return
	 */
	public static boolean bitmapToFile(Bitmap bitmap, File toSave, int quality, int reqWidth, int reqHeight) {
		if (bitmap != null) {
			Bitmap bp = ComThumbnailUtils.extractThumbnail(bitmap, reqWidth, reqHeight);
			return bitmapToFile(bp, toSave, quality);
		}
		return false;
	}
	
	public static Bitmap getXBitmap(Bitmap mask, Bitmap dest) {
		Bitmap bitmap = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setFilterBitmap(true);
		paint.setAntiAlias(true);
		Rect dist = new Rect(0,0,mask.getWidth(),mask.getHeight());
		canvas.drawBitmap(dest, null, dist, paint);
		
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



	private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440; // 100k
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


	public static void recycleImageViewSrcBitmap(ImageView imageView) {
		Drawable drawable = imageView.getDrawable();
		imageView.setImageDrawable(null);
		if (drawable instanceof BitmapDrawable) {
			recycleBitmap(((BitmapDrawable) drawable).getBitmap());
		}
	}

	/**
	 * On Android 2.3.3 (API level 10) and lower, using recycle() is recommended.
	 * If you're displaying large amounts of bitmap data in your app,
	 * you're likely to run into OutOfMemoryError errors.
	 * The recycle() method allows an app to reclaim memory as soon as possible.
	 * @param bitmap
	 */
	public static void recycleBitmap(Bitmap bitmap) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
			}
		}
	}


	/**
	 * A helper function to return the byte usage per pixel of a bitmap based on its configuration.
	 */
	public static int getBytesPerPixel(Bitmap.Config config) {
		if (config == Bitmap.Config.ARGB_8888) {
			return 4;
		} else if (config == Bitmap.Config.RGB_565) {
			return 2;
		} else if (config == Bitmap.Config.ARGB_4444) {
			return 2;
		} else if (config == Bitmap.Config.ALPHA_8) {
			return 1;
		}
		return 1;
	}

	/**
	 * this method determines whether a candidate bitmap satisfies the size criteria to be used for inBitmap
	 * @param candidate
	 * @param targetOptions
	 * @return
	 */
	public static boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// From Android 4.4 (KitKat) onward we can re-use if the byte size of
			// the new bitmap is smaller than the reusable bitmap candidate
			// allocation byte count.
			int width = targetOptions.outWidth / targetOptions.inSampleSize;
			int height = targetOptions.outHeight / targetOptions.inSampleSize;
			int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
			return byteCount <= candidate.getAllocationByteCount();
		}

		// On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
		return candidate.getWidth() == targetOptions.outWidth
				&& candidate.getHeight() == targetOptions.outHeight
				&& targetOptions.inSampleSize == 1;
	}
}
