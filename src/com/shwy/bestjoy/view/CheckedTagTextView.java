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

    public CheckedTagTextView(Context context) {
        this(context, null);
    }

    public CheckedTagTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckedTagTextView);
        checkedTextColor = typedArray.getColor(R.styleable.CheckedTagTextView_checkedTextColor, getCurrentTextColor());
        uncheckedTextColor = typedArray.getColor(R.styleable.CheckedTagTextView_uncheckedTextColor, getCurrentTextColor());

        checkedBackground = typedArray.getDrawable(R.styleable.CheckedTagTextView_checkedBackground);
        uncheckedBackground = typedArray.getDrawable(R.styleable.CheckedTagTextView_checkedBackground);

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
        checked = !checked;
        update();

    }

    @Override
    public boolean performClick() {
        toogle();
        return super.performClick();
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
