package com.shwy.bestjoy.utils;

import android.os.AsyncTask;

public class AsyncTaskUtils {

	public static void cancelTask(AsyncTask<?,?,?> task) {
		if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
			task.cancel(true);
		}
	}
}
