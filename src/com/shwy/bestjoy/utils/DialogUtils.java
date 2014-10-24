package com.shwy.bestjoy.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtils {
	
	public interface DialogCallback extends DialogInterface.OnClickListener, 
		DialogInterface.OnDismissListener, 
		DialogInterface.OnCancelListener {
		@Override
        public void onCancel(DialogInterface dialog); 
		@Override
        public void onDismiss(DialogInterface dialog);
		@Override
        public void onClick(DialogInterface dialog, int which);
		
	}
	
	public static class DialogCallbackSimpleImpl implements DialogCallback {

		@Override
        public void onCancel(DialogInterface dialog) {
	        
        }

		@Override
        public void onDismiss(DialogInterface dialog) {
	        
        }

		@Override
        public void onClick(DialogInterface dialog, int which) {
	        
        }
		
	}
	
	public static DialogCallback getDialogCallbackSimpleImpl() {
		return new DialogCallbackSimpleImpl();
	}
	/**
	 * 创建一个简单的确认对话框
	 * @param context
	 * @param message
	 * @param callback
	 */
	public static void createSimpleConfirmAlertDialog(Context context, String message, String positiveButton, String negativeButton, DialogCallback callback) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context)
		.setMessage(message);
		if (positiveButton != null) {
			builder.setPositiveButton(positiveButton, callback);
		}
		if (negativeButton != null) {
			builder.setNegativeButton(negativeButton, callback);
		}
		builder.setOnCancelListener(callback);
		builder.show();
	}
	
	public static void createSimpleConfirmAlertDialog(Context context, int messageResId, int positiveButton, int negativeButton, DialogCallback callback) {
		createSimpleConfirmAlertDialog(context, context.getString(messageResId), positiveButton == -1?null:context.getString(positiveButton), negativeButton == -1?null:context.getString(negativeButton), callback);
	}

}
