package com.shwy.bestjoy.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

public class AlertDialogWrapper {
	
	public View mCustomView;
	public AlertDialog.Builder mBuilder;

	public AlertDialogWrapper(Context context) {
		mBuilder = new AlertDialog.Builder(context);
	}
	
	public AlertDialog.Builder setView(View view) {
		mCustomView = view;
		return mBuilder.setView(view);
	}

}
