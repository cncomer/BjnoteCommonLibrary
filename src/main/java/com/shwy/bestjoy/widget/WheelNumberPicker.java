package com.shwy.bestjoy.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shwy.bestjoy.R;

/**
 * Created by bestjoy on 16/10/14.
 */

public class WheelNumberPicker extends FrameLayout{
    private static final String TAG = "WheelNumberPicker";

    /**
     * The number of items show in the selector wheel.
     */
    private static final int SELECTOR_WHEEL_ITEM_COUNT = 5;

    /**
     * The index of the middle selector item.
     */
    private static final int SELECTOR_MIDDLE_ITEM_INDEX = SELECTOR_WHEEL_ITEM_COUNT / 2;

    /**
     * The duration of scrolling while snapping to a given position.
     */
    private static final int SNAP_SCROLL_DURATION = 300;

    /**
     * Constant for unspecified size.
     */
    private static final int SIZE_UNSPECIFIED = -1;


    private int minValue = 1;
    private int maxValue = 10;

    private int currentValue = 0;

    private String formater;


    private ListView listView;
    private TextView indicatorView;
    public WheelNumberPicker(Context context) {
        this(context, null);
    }

    public WheelNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.wheel_number_picker, this, true);
        listView = (ListView) this.findViewById(R.id.wheel_listview);
        setIndicatorView(R.layout.wheel_number_picker_indicator);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d(TAG, "onScrollStateChanged scrollState = " + scrollState);
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //看一下中间的View是否在正中心
                    int indicatorViewTop = indicatorView.getTop();

                    int firstVisiblePosition = listView.getFirstVisiblePosition();
                    TextView middleView = (TextView) listView.getChildAt(0+SELECTOR_MIDDLE_ITEM_INDEX);

                    int detaY = indicatorViewTop - middleView.getTop();
                    if (detaY > indicatorView.getMeasuredHeight()/2) {
                        //超过了一半，我们使用下一个
                        listView.setSelectionFromTop(firstVisiblePosition+1, 0);
                        onValueChanged(Integer.valueOf(middleView.getText().toString()) + 1);
                    } else if (detaY == 0) {
                        onValueChanged(Integer.valueOf(middleView.getText().toString()));
                    } else {
                        //没超过了一半，我们居中显示他
                        listView.setSelectionFromTop(firstVisiblePosition, 0);
                        onValueChanged(Integer.valueOf(middleView.getText().toString()));
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d(TAG, "onScroll firstVisibleItem=" + firstVisibleItem + ", visibleItemCount="+visibleItemCount);
            }
        });
    }


    private void onValueChanged(int selectedValue) {
        Log.d(TAG, "onValueChanged " + selectedValue);
        this.currentValue = selectedValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    /**
     * 设置取值期间
     * @param minValue
     * @param maxValue
     */
    public void setNumberValueDuration(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        initAdapter();
    }
    /**
     * 设置取值期间
     * @param currentValue
     */
    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
        setSelection();
    }

    public void setFormater(String formater) {
        this.formater = formater;
        invalidate();
    }

    private void setSelection() {
        if (currentValue > maxValue
                || currentValue < minValue) {
            listView.setSelection(0);
        } else {
            listView.setSelection(currentValue-minValue);
            onValueChanged(currentValue);
        }

    }

    public void setIndicatorView(TextView indicatorView) {
        if (this.indicatorView != null) {
            removeView(this.indicatorView);
        }
        this.indicatorView = indicatorView;
        addView(indicatorView);
    }

    public void setIndicatorView(int indicatorViewId) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        setIndicatorView((TextView) layoutInflater.inflate(indicatorViewId, this, false));
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

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int indicatorViewHeight = indicatorView.getMeasuredHeight();
        // Try greedily to fit the max width and height.
//        final int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(indicatorViewHeight, MeasureSpec.EXACTLY);
        Log.d(TAG, "onMeasure height=" + indicatorViewHeight * SELECTOR_WHEEL_ITEM_COUNT + ", width="+getMeasuredWidth() + ", listView height=" + listView.getMeasuredHeight());
        setMeasuredDimension(getMeasuredWidth(), indicatorViewHeight * SELECTOR_WHEEL_ITEM_COUNT);
    }


    private void initAdapter() {
        listView.setAdapter(new IntAdapter(getContext(), R.layout.wheel_number_picker_item));
        setSelection();
    }

    private class IntAdapter extends ArrayAdapter<String> {
        private int count = 0;

        public IntAdapter(Context context, int resource) {
            super(context, resource);
            count = maxValue - minValue+1 + SELECTOR_MIDDLE_ITEM_INDEX + SELECTOR_MIDDLE_ITEM_INDEX;
        }

        @Override
        public int getCount() {
            return count;
        }

//        @NonNull
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            TextView view = (TextView) super.getView(position, convertView, parent);
//            if (!TextUtils.isEmpty(view.getText().toString())) {
//                int currentItemRealValue = Integer.valueOf(view.getText().toString());
//                if (currentValue == currentItemRealValue && !TextUtils.isEmpty(formater)) {
//                    view.setText(String.format(formater, String.valueOf(currentItemRealValue)));
//                } else {
//                    view.setText(String.valueOf(currentItemRealValue));
//                }
//                view.setTag(currentItemRealValue);
//            }
//
//            return view;
//        }

        @Override
        public String getItem(int position) {
            if (position < SELECTOR_MIDDLE_ITEM_INDEX
                    || position > (getCount() - SELECTOR_MIDDLE_ITEM_INDEX-1)) {
                return "";
            }
            int currentItemRealValue = minValue+position-SELECTOR_MIDDLE_ITEM_INDEX;
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(currentItemRealValue));

            Log.d(TAG, "getItem position=" + position + ", value="+ sb.toString() + ", currentValue=" + currentValue);
            return sb.toString();
        }


    }

}
