package com.shwy.bestjoy.utils;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class NotifyRegistrant {

    private static NotifyRegistrant INSTANCE = new NotifyRegistrant();
    
    private List<Handler> mRegistrant = null;
    
    /** ���յ�֪ͨ�¼�������ΪBundle */
    public static final int EVENT_NOTIFY_MESSAGE_RECEIVED = 0x3000;
  
    private NotifyRegistrant() {}
    
    public static NotifyRegistrant getInstance() {
        return INSTANCE;
    }
    /**
     * ע��Handler
     * @param handler  �¼�������
     * @return ע���Ƿ�ɹ�
     */
     public boolean register(Handler handler){
         if(null == mRegistrant){
             mRegistrant = new ArrayList<Handler>();
         }
         
         if(!isHasExisted(handler)){
             mRegistrant.add(handler);
         }
         return true;
     }
     
     /**
      * ȥע��Handler
      * @param handler  �¼�������
      * @return None
      */
     public void unRegister(Handler handler){
         if(null != mRegistrant){
             mRegistrant.remove(handler);
         }
     }
     
     /**
      * ��Ϣ֪ͨ
      * @param bundle  ��װ����Ϣ��
      * @return None
      */
     public void notify(Bundle bundle){
         if(null == mRegistrant){
             return;
         }
         for(Handler h : mRegistrant){
            h.sendMessage(h.obtainMessage(EVENT_NOTIFY_MESSAGE_RECEIVED, bundle));
         }
     }  
     
     /**
      * ��Ϣ֪ͨ
      * @param what  ��װ����Ϣ������
      * @return None
      */
     public void notify(int what){
         if(null == mRegistrant){
             return;
         }
         for(Handler h : mRegistrant){
            h.sendMessage(h.obtainMessage(what));
         }
     }
     
     /**
      * ��Ϣ֪ͨ
      * @param what  ��װ����Ϣ������
      * @return None
      */
     public void notify(Handler handler, int what){
    	 if (handler != null) {
    		 handler.sendMessage(handler.obtainMessage(what));
    	 }
     }
     
     /**
      * ��Ϣ֪ͨ
      * @param msg
      * @return None
      */
     public void notify(Handler handler, Message msg){
    	 if (handler != null) {
    		 handler.sendMessage(msg);
    	 }
     }
     
     /**
      * ��Ϣ֪ͨ
      * @param msg
      * @return None
      */
     public void notify(Message msg){
         if(null != mRegistrant){
        	 for(Handler h : mRegistrant){
                 h.sendMessage(msg);
              }
         }
     }
     
     /**
      * �ж�ע�������Ƿ����ظ����¼�������
      * @param handler  �¼�������
      * @return true�ظ�,false���ظ�
      */
     private boolean isHasExisted(Handler handler){
         if(null == mRegistrant){
             return false;
         }
         for(Handler h : mRegistrant){
             if(h == handler){
                 return true;
             }
         }
         return false;
     }
}
