package com.shwy.bestjoy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Checkable;

import com.shwy.bestjoy.R;

/**
 * Created by bestjoy on 16/7/15.
 */
public class CheckedTextView extends android.support.v7.widget.AppCompatTextView implements Checkable {
    private boolean checked = false;

    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changed.
     */
    public static interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param checkedTagTextView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onCheckedChanged(CheckedTextView checkedTagTextView, boolean isChecked);
    }
    private OnCheckedChangeListener checkedChangeListener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        checkedChangeListener = onCheckedChangeListener;
    }

    public CheckedTextView(Context context) {
        this(context, null);
    }

    public CheckedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckedTextView);

        boolean checked = typedArray.getBoolean(R.styleable.CheckedTextView_checked, false);
        setChecked(checked);
        typedArray.recycle();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    public void toggle() {
        setChecked(!checked);
    }

    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            if (checkedChangeListener != null) {
                checkedChangeListener.onCheckedChanged(this, checked);
            }
            refreshDrawableState();
        }

    }

    public boolean isChecked() {
        return checked;
    }
}
