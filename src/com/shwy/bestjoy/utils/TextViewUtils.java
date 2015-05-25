package com.shwy.bestjoy.utils;

import android.text.TextPaint;
import android.widget.EditText;
import android.widget.TextView;

public class TextViewUtils {

	/**
	 * 设置粗体
	 * @param v
	 * @param text
	 */
	public static void setBoldText(TextView v, String text) {
		 TextPaint tp = v.getPaint();
         tp.setFakeBoldText(true);
         v.setText(text);
	}
	
	/**
	 * 设置粗体
	 * @param v
	 */
	public static void setBoldText(TextView v) {
		TextPaint tp = v.getPaint();
		tp.setFakeBoldText(true);
	}


	public static void setFakeEnable(EditText v, boolean enable) {
		v.setFocusable(enable);
		v.setFocusableInTouchMode(enable);
		v.setCursorVisible(enable);
	}
}
