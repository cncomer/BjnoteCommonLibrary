package com.shwy.bestjoy.utils;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateUtils {

	private static DateUtils mInstance = new DateUtils();
	private Context mContext;
	/**yyyy-MM-dd*/
	public static  DateFormat TOPIC_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	/**hh:mm*/
	public static  DateFormat TOPIC_TIME_FORMAT = new SimpleDateFormat("HH:mm");
	/**yyyyMMdd*/
	public static  DateFormat TOPIC_CREATE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	/**yyyyMMddHHmm*/
	public static  DateFormat TOPIC_CREATE_DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");
	/**yyyy-MM-dd HH:mm*/
	public static  DateFormat TOPIC_SUBJECT_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	/**yyyy/MM/dd HH:mm*/
	public static  DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	public static  DateFormat DATE_FULL_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private String day1, day2, day3, day4, day5, day6, day7;
	private String[] mMonthOfYear;
	
	private DateUtils(){};
	
	public static DateUtils getInstance() {
		return mInstance;
	}
	
	public void setContext(Context context) {
		mContext = context;
	}



	public static String toRelativeTimeSpanString(long created) {
		long now = System.currentTimeMillis();
		long difference = now - created;
		CharSequence text = (difference >= 0 && difference <= android.text.format.DateUtils.MINUTE_IN_MILLIS) ?
				"刚刚" :
				android.text.format.DateUtils.getRelativeTimeSpanString(
						created,
						now,
						android.text.format.DateUtils.MINUTE_IN_MILLIS,
						android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE);
		return text.toString();
	}
}
