package com.shwy.bestjoy.utils;

import android.os.AsyncTask;

public class AsyncTaskUtils {

	public static void cancelTask(AsyncTask<?,?,?> task) {
		if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
			task.cancel(true);
		}
	}
	
	public static void cancelTask(AsyncTaskCompat<?,?,?> task) {
		if (task != null && task.getStatus() != AsyncTaskCompat.Status.FINISHED) {
			task.cancel(true);
		}
	}
}
