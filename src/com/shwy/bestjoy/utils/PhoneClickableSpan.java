package com.shwy.bestjoy.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.shwy.bestjoy.R;

public class PhoneClickableSpan extends ClickableSpan implements DialogInterface.OnClickListener{
	 private String phoneNumber;
	 private Context context;
	 private String[] items;
	 public PhoneClickableSpan(String number, Context context, String[] items) {
		 phoneNumber = number;
		 this.context = context;
		 this.items = items;
	 }
	 @Override
		public void updateDrawState(TextPaint ds) {
		    ds.setColor(ds.linkColor);
		    ds.setUnderlineText(false);
		}
		
		@Override
		public void onClick(View widget) {
			if (phoneNumber != null && phoneNumber.length()==11) {
				//�ֻ���룬֧�ַ����źʹ�绰
				showItemDialog();
			} else {
				call();
			}
		}
		
		public void sendMessage() {
			Uri smsToUri = Uri.parse("smsto:" + phoneNumber); 
			Intent intent = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri ); 
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity( intent );
		}
		
		public void call() {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           intent.setData(Uri.parse("tel:" + phoneNumber));
           context.startActivity(intent);
		}
		
		public void showItemDialog() {
			if (items== null) {
				items = context.getResources().getStringArray(R.array.phoneClickableSpan);
			}
			new AlertDialog.Builder(context)
			.setItems(items, this)
			.setPositiveButton(android.R.string.cancel, null)
			.show();
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which){
			case 0:
				call();
				break;
			case 1:
				sendMessage();
				break;
			}
			
		}
}
