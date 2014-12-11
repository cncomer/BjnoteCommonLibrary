package com.shwy.bestjoy.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.provider.MediaStore;

public class ImageHelper {
	private Context mContext;
	private static final long MAX_BILL_BITMAP_SIZE = 1024 * 100; //100k

	private static final ImageHelper INSTANCE = new ImageHelper();
	private ImageHelper(){};
	
	public static ImageHelper getInstance() {
		return INSTANCE;
	}
	
	public void setContext(Context context) {
		mContext = context;
	}
	/**
	 * ��������Intent
	 * @param uri
	 * @return
	 */
	public static Intent createCaptureIntent(Uri uri) {
		Intent capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		capture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		return capture;
	}
	/**
	 * ��������ͼ��Intent
	 * @param uri
	 * @return
	 */
	public static Intent createGalleryIntent() {
		Intent intent = new Intent();
    	// ��Pictures����TypeΪimage/*
    	intent.setType("image/*");
    	intent.setAction(Intent.ACTION_GET_CONTENT);
    	return intent;
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
}
