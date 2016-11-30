package com.shwy.bestjoy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.shwy.bestjoy.R;

/**
 * Created by bestjoy on 16/10/14.
 */

public class WheelObjectPicker extends FrameLayout{
    private static final String TAG = "WheelNumberPicker";

    /**
     * The number of items show in the selector wheel.
     */
    private static int SELECTOR_WHEEL_ITEM_COUNT = 5;

    /**
     * The index of the middle selector item.
     */
    private static int SELECTOR_MIDDLE_ITEM_INDEX = SELECTOR_WHEEL_ITEM_COUNT / 2;

    /**
     * The duration of scrolling while snapping to a given position.
     */
    private static final int SNAP_SCROLL_DURATION = 300;

    /**
     * Constant for unspecified size.
     */
    private static final int SIZE_UNSPECIFIED = -1;


    private int selectItemPosition = 0;

    private ListView listView;
    private View indicatorView;
    private WheelObjectPickerItemPositionSelectListener wheelObjectPickerItemPositionSelectListener;
    private WheelObjectPickerAdapterWrapper wheelObjectPickerAdapterWrapper;
    public WheelObjectPicker(Context context) {
        this(context, null);
    }

    public WheelObjectPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WheelObjectPicker);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        int indicatorViewId = typedArray.getResourceId(R.styleable.WheelObjectPicker_wheelIndicatorView, R.layout.wheel_number_picker_indicator);
        layoutInflater.inflate(R.layout.wheel_number_picker, this, true);
        setIndicatorView(indicatorViewId);

        setVisibleCount(typedArray.getInt(R.styleable.WheelObjectPicker_wheelVisibleItemCount, 5));

        typedArray.recycle();
        listView = (ListView) this.findViewById(R.id.wheel_listview);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d(TAG, "onScrollStateChanged scrollState = " + scrollState);
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //看一下中间的View是否在正中心
                    int indicatorViewTop = indicatorView.getTop();

                    int firstVisiblePosition = listView.getFirstVisiblePosition();
                    View middleView = (View) listView.getChildAt(0+SELECTOR_MIDDLE_ITEM_INDEX);

                    int detaY = indicatorViewTop - middleView.getTop();
                    if (detaY > indicatorView.getMeasuredHeight()/2) {
                        //超过了一半，我们使用下一个
                        listView.setSelectionFromTop(firstVisiblePosition+1, 0);
                        onPositionSelected(firstVisiblePosition + 1);
                    } else if (detaY == 0) {
                        onPositionSelected(firstVisiblePosition);
                    } else {
                        //没超过了一半，我们居中显示他
                        listView.setSelectionFromTop(firstVisiblePosition, 0);
                        onPositionSelected(firstVisiblePosition);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d(TAG, "onScroll firstVisibleItem=" + firstVisibleItem + ", visibleItemCount="+visibleItemCount);
            }
        });
    }


    private void onPositionSelected(int selectItemPosition) {
        Log.d(TAG, "onPositionSelected " + selectItemPosition);
        this.selectItemPosition = selectItemPosition;
        if (wheelObjectPickerItemPositionSelectListener != null) {
            Object itemObject = null;
            if (wheelObjectPickerAdapterWrapper != null) {
                itemObject = wheelObjectPickerAdapterWrapper.unWrap().getItem(selectItemPosition);
            }
            wheelObjectPickerItemPositionSelectListener.onPositionSelected(this, selectItemPosition, itemObject);
        }
    }

    public int getSelectItemPosition() {
        return selectItemPosition;
    }

    public void setIndicatorView(View indicatorView) {
        if (this.indicatorView != null) {
            removeView(this.indicatorView);
        }
        this.indicatorView = indicatorView;
        addView(indicatorView);
    }

    public void setIndicatorView(int indicatorViewId) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        setIndicatorView(layoutInflater.inflate(indicatorViewId, this, false));
    }

    /**
     * 设置可显示的Item个数
     */
    public void setVisibleCount(int itemCount) {
        SELECTOR_WHEEL_ITEM_COUNT = itemCount;
        SELECTOR_MIDDLE_ITEM_INDEX = SELECTOR_WHEEL_ITEM_COUNT / 2;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final int msrdWdth = getMeasuredWidth();
        final int msrdHght = getMeasuredHeight();
        if (indicatorView != null) {
            // indicatorView centered vertically.
            final int inptTxtMsrdHght = indicatorView.getMeasuredHeight();
            final int inptTxtTop = (msrdHght - inptTxtMsrdHght) / 2;
            final int inptTxtBottom = inptTxtTop + inptTxtMsrdHght;
            indicatorView.layout(0, inptTxtTop, indicatorView.getMeasuredWidth(), inptTxtBottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        if (indicatorView != null) {
            int indicatorViewHeight = indicatorView.getMeasuredHeight();
            // Try greedily to fit the max width and height.
//        final int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(indicatorViewHeight, MeasureSpec.EXACTLY);
            Log.d(TAG, "onMeasure height=" + indicatorViewHeight * SELECTOR_WHEEL_ITEM_COUNT + ", width="+getMeasuredWidth() + ", listView height=" + listView.getMeasuredHeight());

//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(indicatorViewHeight * SELECTOR_WHEEL_ITEM_COUNT + (SELECTOR_WHEEL_ITEM_COUNT-1)*listView.getDividerHeight(), MeasureSpec.EXACTLY);
//            setMeasuredDimension(getMeasuredWidth(), indicatorViewHeight * SELECTOR_WHEEL_ITEM_COUNT + (SELECTOR_WHEEL_ITEM_COUNT-1)*listView.getDividerHeight());
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }


    public void setAdapter(WheelObjectPickerAdapterWrapper wheelObjectPickerAdapterWrapper) {
        this.wheelObjectPickerAdapterWrapper = wheelObjectPickerAdapterWrapper;
        if (wheelObjectPickerAdapterWrapper != null) {
            BaseAdapter baseAdapter = wheelObjectPickerAdapterWrapper.unWrap();
            if (baseAdapter.getCount()>0) {
                for (int index=0;index<SELECTOR_MIDDLE_ITEM_INDEX;index++) {
                    listView.addHeaderView(wheelObjectPickerAdapterWrapper.getHeaderView(index), null, false);
                    listView.addFooterView(wheelObjectPickerAdapterWrapper.getFooterView(index), null, false);
                }
                listView.setFooterDividersEnabled(false);
                listView.setHeaderDividersEnabled(false);
            }
            listView.setAdapter(baseAdapter);
            resetSelection();
        } else {
            listView.setAdapter(null);
        }

    }

    public void resetSelection() {
        post(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(0);
                if (listView.getAdapter() != null) {
                    onPositionSelected(0);
                }
            }
        });
    }

    public void setWheelObjectPickerItemPositionSelectListsner(WheelObjectPickerItemPositionSelectListener wheelObjectPickerItemPositionSelectListener) {
        this.wheelObjectPickerItemPositionSelectListener = wheelObjectPickerItemPositionSelectListener;
    }


    public static abstract class WheelObjectPickerAdapterWrapper {
        private BaseAdapter baseAdapter;
        public WheelObjectPickerAdapterWrapper(BaseAdapter baseAdapter) {
            this.baseAdapter = baseAdapter;
        }

        public BaseAdapter unWrap() {
            return baseAdapter;
        }

        public abstract View getHeaderView(int position);
        public abstract View getFooterView(int position);
    }

    public static interface WheelObjectPickerItemPositionSelectListener {
        public void onPositionSelected(WheelObjectPicker wheelObjectPicker, int selectItemPosition, Object itemObject);
    }
}
