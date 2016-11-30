package com.shwy.bestjoy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.shwy.bestjoy.R;
import com.shwy.bestjoy.utils.DebugUtils;


/**
 * Created by bestjoy on 15/7/28.
 */
public class DrawableImageView extends ImageView {
    private static final String TAG = "DrawableImageView";
    private int mSrcDrawableHeight;
    private int mSrcDrawableWidth;

    private Drawable mDrawable;
    private Rect mDrawableRect;

    public DrawableImageView(Context context) {
        super(context);

    }

    public DrawableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawableImageView);

        mSrcDrawableHeight = typedArray.getDimensionPixelSize(R.styleable.DrawableImageView_srcDrawableHeight, 0);
        mSrcDrawableWidth = typedArray.getDimensionPixelSize(R.styleable.DrawableImageView_srcDrawableWidth, 0);

        typedArray.recycle();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable background) {
        mDrawable = background;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (mDrawable == null) {
            return; // couldn't resolve the URI
        }

        if (mDrawableRect == null) {
            mDrawableRect = new Rect(0,0,mSrcDrawableWidth, mSrcDrawableHeight);
            mDrawable.setBounds(mDrawableRect);
        }
        int saveCount = canvas.getSaveCount();
        canvas.save();
        int vwidth = getWidth();
        int vheight = getHeight();
        DebugUtils.logD(TAG, "vwidth="+vwidth+ ", vheight=" + vheight + ", mSrcDrawableWidth="+mSrcDrawableWidth+ ", mSrcDrawableHeight="+mSrcDrawableHeight);
        canvas.translate((vwidth - mSrcDrawableWidth) * 0.5f, (vheight - mSrcDrawableHeight) * 0.5f);
        mDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);

    }
}
