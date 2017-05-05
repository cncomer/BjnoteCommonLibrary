package com.shwy.bestjoy.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;

import com.shwy.bestjoy.R;

/**
 * Created by bestjoy on 15/7/28.
 */
public class DrawableTextView extends android.support.v7.widget.AppCompatTextView implements Checkable{
    private int mDrawableHeight;
    private int mDrawableWidth;

    private Drawable[] mDrawables;

    private boolean checked = false;

    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };

    public DrawableTextView(Context context) {
        super(context);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            //View从API Level 11才加入setLayerType方法
            //关闭硬件加速
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

    }

    public DrawableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            //View从API Level 11才加入setLayerType方法
            //关闭硬件加速
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView);

        mDrawableHeight = typedArray.getDimensionPixelSize(R.styleable.DrawableTextView_drawableHeight, 0);
        mDrawableWidth = typedArray.getDimensionPixelSize(R.styleable.DrawableTextView_drawableWidth, 0);

        mDrawables = this.getCompoundDrawables();

        reinitCompoundDrawables();

        setChecked(typedArray.getBoolean(R.styleable.DrawableTextView_checked, false));

        typedArray.recycle();
    }

    private void reinitCompoundDrawables() {

        if (mDrawableHeight > 0 && mDrawableWidth >0) {
            Rect bound = new Rect(0,0,mDrawableWidth,mDrawableHeight);
            ColorStateList colorStateList = getTextColors();
           for(int index=0;index<4;index++) {
               if (mDrawables[index] != null) {
                   mDrawables[index].setBounds(bound);
                   if (colorStateList != null) {
                       Drawable drawable = DrawableCompat.wrap(mDrawables[index]);
                       // We can now set a tint
                       // ...or a tint list
                       DrawableCompat.setTintList(drawable, colorStateList);
                       // ...and a different tint mode
                       DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);

                       mDrawables[index] = drawable;
                   }
               }
           }
        }
        super.setCompoundDrawables(mDrawables[0],mDrawables[1],mDrawables[2],mDrawables[3]);
    }

    @Override
    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }

    @Override

    protected int[] onCreateDrawableState(int extraSpace) {

        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }

        return drawableState;

    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top,
                                     @Nullable Drawable right, @Nullable Drawable bottom) {
        if (mDrawables == null) {
            mDrawables = new Drawable[4];
        }
        mDrawables[0] = left;
        mDrawables[1] = top;
        mDrawables[2] = right;
        mDrawables[3] = bottom;

        reinitCompoundDrawables();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
