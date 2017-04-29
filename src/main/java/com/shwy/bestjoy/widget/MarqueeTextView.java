package com.shwy.bestjoy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.shwy.bestjoy.utils.DebugUtils;

import java.util.Date;


/**
 * Created by bestjoy on 17/1/11.
 */

public class MarqueeTextView extends android.support.v7.widget.AppCompatTextView implements Runnable {

    private static final String TAG = "MarqueeTextView";
    private int currentScrollX;// 当前滚动的位置
    private boolean isStop = false;
    private int textWidth;

    private int stepDistance = 2;

    private boolean repeat = false;


    private int marqueeTextIndex;
    private String[] marqueeTexts;
    /**间隔时间，毫秒*/
    private long stepInterval = 60;

    private long lastTranslateTime = -1;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initTextWidth();
        setStepDistance(stepDistance);
    }

    public void setStepDistance(int stepDistance) {
        this.stepDistance = (int) (getContext().getResources().getDisplayMetrics().density * stepDistance);
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }
    public void setStepInterval(long stepInterval) {
        this.stepInterval = stepInterval;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        reset();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (textWidth == 0) {
            return;
        }
        canvas.save();
        super.onDraw(new Canvas());
        canvas.translate(currentScrollX, 0);
        getLayout().draw(canvas);
        canvas.restore();
    }

    /**
     * 获取文字宽度
     */
    private void initTextWidth() {
        Paint textPaint = new TextPaint();
        textPaint.setTextSize(getTextSize());
        String str = this.getText().toString();
        if (TextUtils.isEmpty(str)) {
            textWidth = 0;
        } else {
            textWidth = (int) textPaint.measureText(str);
        }
    }

    @Override
    public void run() {
        long nowTranslateTime = new Date().getTime();
        DebugUtils.logD(TAG, "run duration " + (nowTranslateTime - lastTranslateTime));
        currentScrollX -= stepDistance;// 滚动速度
        invalidate();
        if (isStop) {
            removeCallbacks(this);
            return;
        }
        lastTranslateTime = nowTranslateTime;
        if (currentScrollX < -textWidth) {
            removeCallbacks(this);
            DebugUtils.logD(TAG, "currentScrollX =" + currentScrollX+", textWidth=" +textWidth);
            notifyMarqueeFinish(marqueeTextIndex);
        } else {
            postDelayed(this, stepInterval);
        }

    }

    // 开始滚动
    public void startScroll() {
        isStop = false;
        removeCallbacks(this);
        lastTranslateTime = new Date().getTime();
        post(this);
    }

    // 停止滚动
    public void stopScroll() {
        isStop = true;
    }

    // 从头开始滚动
    public void startFor0() {
        reset();
        startScroll();
    }

    public void setMarqueeText(String text) {
        marqueeTextIndex = 0;
        setMarqueeTextInternal(text);

    }

    private void setMarqueeTextInternal(String text) {
        DebugUtils.logD(TAG , "setMarqueeTextInternal " + text + ", marqueeTextIndex="+marqueeTextIndex);
        super.setText(text);
        initTextWidth();
        if (TextUtils.isEmpty(text)) {
            stopScroll();
        } else {
            startFor0();
        }

    }

    private void reset() {
        int pos = getWidth();
        currentScrollX = pos;
    }


    protected void notifyMarqueeFinish(int marqueeTextIndex) {
        DebugUtils.logD(TAG , "notifyMarqueeFinish marqueeTextIndex="+marqueeTextIndex);
        if (marqueeTexts != null) {
            if (marqueeTexts.length-1 > marqueeTextIndex) {
                this.marqueeTextIndex++;
            } else {
                this.marqueeTextIndex = 0;
            }
            setMarqueeTextInternal(marqueeTexts[this.marqueeTextIndex]);
        } else {
            if (repeat) {
                removeCallbacks(this);
                startFor0();
            }
        }
    }

    public void setMarqueeTexts(String[] text) {
        marqueeTexts = text;
        marqueeTextIndex = 0;
        if (marqueeTexts != null && marqueeTexts.length > 0) {
            setMarqueeTextInternal(marqueeTexts[marqueeTextIndex]);
        }

    }

    public String[] getMarqueeTexts() {
        return marqueeTexts;
    }
}
