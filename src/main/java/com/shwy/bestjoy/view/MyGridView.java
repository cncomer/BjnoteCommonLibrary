package com.shwy.bestjoy.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

public class MyGridView extends GridView {

    protected int mRequestedNumColumns;
    protected int mRequestedColumnWidth;
    public MyGridView(Context context, AttributeSet attrs) { 
        super(context, attrs); 
    } 

    public MyGridView(Context context) { 
        super(context); 
    } 

    public MyGridView(Context context, AttributeSet attrs, int defStyle) { 
        super(context, attrs, defStyle); 
    } 

    @Override 
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getAdapter() == null) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        } else {
            int maxWidth = measureWidthByChilds() * mRequestedNumColumns + getHorizontalSpacing() * (mRequestedNumColumns-1) + getPaddingLeft() + getPaddingRight();

            int lineCount = getAdapter().getCount() / mRequestedNumColumns;
            if (getAdapter().getCount() % mRequestedNumColumns > 0) {
                lineCount++;
            }
            int maxHeight = measureHeightByChilds() * lineCount + getVerticalSpacing() * (lineCount-1) + getPaddingTop() + getPaddingBottom();

            super.onMeasure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY));
        }

    }

    @Override
    public int getHorizontalSpacing() {
        return super.getHorizontalSpacing();
    }

    @Override
    public int getVerticalSpacing() {
        return super.getVerticalSpacing();
    }


    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
        mRequestedNumColumns = numColumns;
    }

    public int measureWidthByChilds() {
        int maxWidth = 0;
        View view = null;
        for (int i = 0; i < getAdapter().getCount(); i++) {
            view = getAdapter().getView(i, view, this);
            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            if (view.getMeasuredWidth() > maxWidth){
                maxWidth = view.getMeasuredWidth();
            }
        }
        return Math.max(maxWidth, getColumnWidth());
    }

    @Override
    public void setColumnWidth(int columnWidth) {
        super.setColumnWidth(columnWidth);
        mRequestedColumnWidth = columnWidth;
    }

    public int getColumnWidth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return super.getColumnWidth();
        }
        return mRequestedColumnWidth;
    }

    public int measureHeightByChilds() {
        int maxHeight = 0;
        View view = null;
        for (int i = 0; i < getAdapter().getCount(); i++) {
            view = getAdapter().getView(i, view, this);
            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            if (view.getMeasuredHeight() > maxHeight){
                maxHeight = view.getMeasuredHeight();
            }
        }
        return maxHeight;
    }
}
