package com.shwy.bestjoy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.shwy.bestjoy.R;

/**
 * Created by bestjoy on 16/7/15.
 */
public class CheckedTagTextView extends TextView{
    private boolean checked = false;

    private int checkedTextColor, uncheckedTextColor;
    private Drawable checkedBackground, uncheckedBackground;
    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changed.
     */
    public static interface OnTagCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param checkedTagTextView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onTagCheckedChanged(CheckedTagTextView checkedTagTextView, boolean isChecked);
    }
    private OnTagCheckedChangeListener checkedChangeListener;

    public void setTagCheckedChangeListener(OnTagCheckedChangeListener onCheckedChangeListener) {
        checkedChangeListener = onCheckedChangeListener;
    }

    public CheckedTagTextView(Context context) {
        this(context, null);
    }

    public CheckedTagTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckedTagTextView);
        checkedTextColor = typedArray.getColor(R.styleable.CheckedTagTextView_checkedTextColor, getCurrentTextColor());
        uncheckedTextColor = typedArray.getColor(R.styleable.CheckedTagTextView_uncheckedTextColor, getCurrentTextColor());

        checkedBackground = typedArray.getDrawable(R.styleable.CheckedTagTextView_checkedBackground);
        uncheckedBackground = typedArray.getDrawable(R.styleable.CheckedTagTextView_uncheckedBackground);

        if (checkedBackground == null) {
            checkedBackground = getBackground();
        }

        if (uncheckedBackground == null) {
            uncheckedBackground = getBackground();
        }
        update();
    }

    public void setCheckedTextColor(int checkedTextColor) {
        this.checkedTextColor = checkedTextColor;
        invalidate();
    }

    public void setUncheckedTextColor(int uncheckedTextColor) {
        this.uncheckedTextColor = uncheckedTextColor;
        invalidate();
    }

    public void setCheckedBackground(Drawable checkedBackground) {
        this.checkedBackground = checkedBackground;
        invalidate();
    }

    public void setUncheckedBackground(Drawable uncheckedBackground) {
        this.uncheckedBackground = uncheckedBackground;
        invalidate();
    }

    public void toogle() {
        setChecked(!checked);
    }

    @Override
    public boolean performClick() {
        toogle();
        return super.performClick();
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        update();
        if (checkedChangeListener != null) {
            checkedChangeListener.onTagCheckedChanged(this, checked);
        }
    }

    /**
     * 清空选中状态
     */
    public void clearChecked() {
        checked = false;
        update();
    }

    public boolean isChecked() {
        return checked;
    }

    private void update() {
        if (checked) {
            //表示选中
            setBackgroundDrawable(checkedBackground);
            setTextColor(checkedTextColor);
        } else {
            setBackgroundDrawable(uncheckedBackground);
            setTextColor(uncheckedTextColor);
        }
    }
}
