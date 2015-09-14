package com.shwy.bestjoy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.shwy.bestjoy.R;


/**
 * Created by bestjoy on 15/7/28.
 */
public class DrawableTextView extends TextView {
    private int mDrawableHeight;
    private int mDrawableWidth;

    private Drawable[] mDrawables;

    public DrawableTextView(Context context) {
        super(context);

    }

    public DrawableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView);

        mDrawableHeight = typedArray.getDimensionPixelSize(R.styleable.DrawableTextView_drawableHeight, 0);
        mDrawableWidth = typedArray.getDimensionPixelSize(R.styleable.DrawableTextView_drawableWidth, 0);

        typedArray.recycle();
        reinitCompoundDrawables();
    }

    private void reinitCompoundDrawables() {
        mDrawables = this.getCompoundDrawables();
        if (mDrawableHeight > 0 && mDrawableWidth >0) {
            Rect bound = new Rect(0,0,mDrawableWidth,mDrawableHeight);
           for(int index=0;index<4;index++) {
               if (mDrawables[index] != null) {
                   mDrawables[index].setBounds(bound);
               }
           }
        }
        setCompoundDrawables(mDrawables[0],mDrawables[1],mDrawables[2],mDrawables[3]);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
