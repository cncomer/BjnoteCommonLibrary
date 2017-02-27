package com.shwy.bestjoy.utils;

import android.content.Context;

import com.shwy.bestjoy.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	private static DateUtils mInstance = new DateUtils();
	private Context mContext;
	public static  DateFormat DATE_YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");
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
	/**MM:dd HH:mm*/
	public static  DateFormat HOUR_MINUTE_TIME_FORMAT = new SimpleDateFormat("HH:mm");
	private String day1, day2, day3, day4, day5, day6, day7;
	private String[] mMonthOfYear;
	
	private DateUtils(){}
	
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


	/**
	 * 验证时间戳是否在maxDurationDay有效期内
	 * @param timestampInMillis  时间戳
	 * @param maxDurationDay  有效期,单位是天
     * @return
     */
	public static boolean verifyDate(long timestampInMillis, int maxDurationDay) {
		Calendar today = Calendar.getInstance();
		today.setTime(new Date());
		Calendar last = Calendar.getInstance();
		last.setTimeInMillis(timestampInMillis);

		boolean oldDay = today.get(Calendar.DAY_OF_YEAR) - last.get(Calendar.DAY_OF_YEAR) >= maxDurationDay;
		//是旧文件，并且不是同一天，这主要是针对0点不是旧文件，日期不同步问题
		return oldDay;
	}

	public static boolean verifyTime(long timestampInMillis, long maxDurationTime) {
		Calendar today = Calendar.getInstance();
		today.setTime(new Date());
		Calendar last = Calendar.getInstance();
		last.setTimeInMillis(timestampInMillis);

		boolean oldTime = today.getTimeInMillis() - last.getTimeInMillis() >= maxDurationTime;
		return oldTime;
	}



	private static final long MINUTE_SECONDS = 60;
	private static final long HOUR_SECONDS = MINUTE_SECONDS*60;
	private static final long DAY_SECONDS = HOUR_SECONDS*24;
	private static final long YEAR_SECONDS = DAY_SECONDS*365;

	/***
	 * 当前时间距离上次时间已过多久， 如xx分钟、xx小时、xx天等
	 * @param nowMilliseconds
	 * @param oldMilliseconds
     * @return
     */
	public String toRelativeTimeString(long nowMilliseconds, long oldMilliseconds) {
		long passed = (nowMilliseconds-oldMilliseconds) /1000;//转为秒
		if (passed > YEAR_SECONDS) {
			return passed/YEAR_SECONDS + mContext.getString(R.string.unit_year);
		} else if (passed > DAY_SECONDS) {
			return passed/DAY_SECONDS+mContext.getString(R.string.unit_day);
		} else if (passed > HOUR_SECONDS) {
			return passed/HOUR_SECONDS+mContext.getString(R.string.unit_hour);
		} else if (passed > MINUTE_SECONDS) {
			return passed/MINUTE_SECONDS+mContext.getString(R.string.unit_minute);
		} else {
			return passed+mContext.getString(R.string.unit_second);
		}
	}

}
