package com.shwy.bestjoy.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.text.TextUtils;

import com.shwy.bestjoy.bjnotecommonlibrary.R;

public class DateUtils {

	private static DateUtils mInstance = new DateUtils();
	private Context mContext;
	/**活动日期格式yyyy-MM-dd*/
	public static  DateFormat TOPIC_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	/**活动时间格式hh:mm*/
	public static  DateFormat TOPIC_TIME_FORMAT = new SimpleDateFormat("HH:mm");
	/**交换活动创建时间格式yyyyMMdd*/
	public static  DateFormat TOPIC_CREATE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	public static  DateFormat TOPIC_CREATE_DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");
	public static  DateFormat TOPIC_SUBJECT_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private String day1, day2, day3, day4, day5, day6, day7;
	private String[] mMonthOfYear;
	
	private DateUtils(){};
	
	public static DateUtils getInstance() {
		return mInstance;
	}
	
	public void setContext(Context context) {
		mContext = context;
		initDayOfWeek();
		mMonthOfYear = mContext.getResources().getStringArray(R.array.monthOfYear);
	}
	
	private void initDayOfWeek() {
		if (day1 == null) {
			day1 = mContext.getString(R.string.monday);
			day2 = mContext.getString(R.string.tuesday);
			day3 = mContext.getString(R.string.wenesday);
			day4 = mContext.getString(R.string.thursday);
			day5 = mContext.getString(R.string.friday);
			day6 = mContext.getString(R.string.saturday);
			day7 = mContext.getString(R.string.sunday);
		}
	}
	
	private void initmonthOfYear() {
		if (day1 == null) {
			day1 = mContext.getString(R.string.monday);
			day2 = mContext.getString(R.string.tuesday);
			day3 = mContext.getString(R.string.wenesday);
			day4 = mContext.getString(R.string.thursday);
			day5 = mContext.getString(R.string.friday);
			day6 = mContext.getString(R.string.saturday);
			day7 = mContext.getString(R.string.sunday);
		}
	}
	
	public String getMonthOfYear(int monthIndex) {
		if (monthIndex < 0 || monthIndex > 11) {
			throw new java.lang.IndexOutOfBoundsException("monthIndex must be in range from 0 to 11");
		}
		return mMonthOfYear[monthIndex];
	}
	
	public String getWeekDay(Calendar c){
	   if (c == null){
	        return day1;
	   }
	  
	   if (Calendar.MONDAY == c.get(Calendar.DAY_OF_WEEK)){
	        return day1;
	   }
	   if (Calendar.TUESDAY == c.get(Calendar.DAY_OF_WEEK)){
	        return day2;
	   }
	   if (Calendar.WEDNESDAY == c.get(Calendar.DAY_OF_WEEK)){
	       return day3;
	   }
	   if (Calendar.THURSDAY == c.get(Calendar.DAY_OF_WEEK)){
	       return day4;
	   }
	   if (Calendar.FRIDAY == c.get(Calendar.DAY_OF_WEEK)){
	       return day5;
	   }
	   if (Calendar.SATURDAY == c.get(Calendar.DAY_OF_WEEK)){
	      return day6;
	   }
	   if (Calendar.SUNDAY == c.get(Calendar.DAY_OF_WEEK)){
	      return day7;
	   }
	  
	   return day1;
	}
	
	public static String getActivityDate(String dateStr) {
		if (TextUtils.isEmpty(dateStr)) return null;
		Date date = null;
		try {
			date = TOPIC_CREATE_DATE_TIME_FORMAT.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (date != null) {
			return TOPIC_SUBJECT_DATE_TIME_FORMAT.format(date);
		} else {
			return dateStr;
		}
	}
	
	public static String getPhotoTimestamp() {
		return null;
	}
}
