package com.shwy.bestjoy.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

public class ComNotificationManager {

	private Context mContext;
	private NotificationManager mNM;
	private static final ComNotificationManager INSTANCE = new ComNotificationManager();
	private ComNotificationManager(){}
	
	public static final int ALL_IM_UNSEEN = 1;
	
	public static ComNotificationManager getInstance() {
		return INSTANCE;
	}
	
	public void setContext(Context context) {
		mContext = context;
		mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	
	public void showNotification(Notification notification, int notificationId) {
		mNM.notify(notificationId, notification);
	}
	
	public void cancel(int notificationId) {
		if (notificationId == 0) {
			mNM.cancelAll();
		} else {
			mNM.cancel(notificationId);
		}
	}
}
