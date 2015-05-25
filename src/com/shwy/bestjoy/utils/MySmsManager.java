package com.shwy.bestjoy.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bestjoy on 15/4/30.
 */
public class MySmsManager {
    private static final String TAG = "MySmsManager";
    private Context mContext;
    private static final MySmsManager INSTANCE = new MySmsManager();



    public static final String EXTRA_TOKEN = "token";
    public static final String EXTRA_TARGET_PHONE = "target_phone";
    public static final String ACTION_SEND_STATUS = "com.shwy.bestjoy.utils.ACTION_SEND_STATUS";
    public static final String ACTION_DELIVERY_STATUS = "com.shwy.bestjoy.utils.ACTION_DELIVERY_STATUS";
    private static final int REQUEST_SEND_CODE = 1;
    private static final int REQUEST_DELIVERY_CODE = 2;
    private List<ICallback> mCallBacks = new LinkedList<>();
    private SmsManager mSmsManager;

    private boolean mHasRegisterReceiver = false;
    public void setContext(Context context) {
        mContext = context;
        mSmsManager = SmsManager.getDefault();
        mCallBacks.clear();

        if (mHasRegisterReceiver == false) {
            IntentFilter mSMSResultFilter = new IntentFilter();
            mSMSResultFilter.addAction(ACTION_SEND_STATUS);
            mSMSResultFilter.addAction(ACTION_DELIVERY_STATUS);
            mContext.registerReceiver(new SMSSendResultReceiver(), mSMSResultFilter);
            mHasRegisterReceiver = true;
        }
    }

    public void sendTextMessage(String targetPhone, String text, ICallback callback) {
        Intent send = new Intent(ACTION_SEND_STATUS);
        Intent delivery = new Intent(ACTION_DELIVERY_STATUS);
        if (callback != null) {
            addCallback(callback);
            send.putExtra(EXTRA_TOKEN, callback.getToken());
            delivery.putExtra(EXTRA_TOKEN, callback.getToken());
        }
        send.putExtra(EXTRA_TARGET_PHONE, targetPhone);
        delivery.putExtra(EXTRA_TARGET_PHONE, targetPhone);
        PendingIntent sendPendingIntent = PendingIntent.getBroadcast(mContext, REQUEST_SEND_CODE, send, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent deliveryPendingIntent = PendingIntent.getBroadcast(mContext, REQUEST_DELIVERY_CODE, delivery, PendingIntent.FLAG_UPDATE_CURRENT);

        mSmsManager.sendTextMessage(targetPhone, null, text, sendPendingIntent, deliveryPendingIntent);

    }

    class SMSSendResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_SEND_STATUS.equals(intent.getAction())) {
                String phoneNum = intent.getStringExtra(EXTRA_TARGET_PHONE);
                String token = intent.getStringExtra(EXTRA_TOKEN);
                int resultCode = getResultCode();
                switch(resultCode) {
                    case Activity.RESULT_OK:
                        DebugUtils.logD(TAG, "Send Message to " + phoneNum + " success!");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                    default:
                        DebugUtils.logD(TAG, "Send Message to " + phoneNum + " fail!");
                        break;
                }
                notifySendStatusCallback(resultCode, token);
            } else  if (ACTION_DELIVERY_STATUS.equals(intent.getAction())) {

            }

        }
    }

    public synchronized void addCallback(ICallback callback) {
       if (!mCallBacks.contains(callback)) {
           mCallBacks.add(callback);
       }
    }
    public synchronized void removeCallback(ICallback callback) {
        if (mCallBacks.contains(callback)) {
            mCallBacks.remove(callback);
        }
    }

    private void notifySendStatusCallback(int status, String token) {
        List<ICallback> callbacks = new LinkedList<>(mCallBacks);
        for(ICallback callback : callbacks){
            if (callback.getToken().equals(token)) {
                callback.onSendStatus(status);
            }
        }
    }

    private void notifyDeliveryStatusCallback(int status, String token) {
        List<ICallback> callbacks = new LinkedList<>(mCallBacks);
        for(ICallback callback : callbacks){
            if (callback.getToken().equals(token)) {
                callback.onDeliveryStatus(status);
            }
        }
    }

    public interface ICallback {
        boolean onSendStatus(int status);
        boolean onDeliveryStatus(int status);
        String getToken();
    }

}
