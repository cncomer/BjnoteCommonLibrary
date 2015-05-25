package com.shwy.bestjoy.utils;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

public class NotifyRegistrant {

    private static NotifyRegistrant INSTANCE = new NotifyRegistrant();
    
    private List<Handler> mRegistrant = null;
    
    /** 接收到通知事件，参数为Bundle */
    public static final int EVENT_NOTIFY_MESSAGE_RECEIVED = 0x3000;
  
    private NotifyRegistrant() {}
    
    public static NotifyRegistrant getInstance() {
        return INSTANCE;
    }
    /**
     * 注册Handler
     * @param handler  事件处理器
     * @return 注册是否成功
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
      * 去注册Handler
      * @param handler  事件处理器
      * @return None
      */
     public void unRegister(Handler handler){
         if(null != mRegistrant){
             mRegistrant.remove(handler);
         }
     }
     
     /**
      * 消息通知
      * @param object  封装的消息体
      * @return None
      */
     public void notify(Object object){
         if(null == mRegistrant){
             return;
         }
         for(Handler h : mRegistrant){
            h.sendMessage(h.obtainMessage(EVENT_NOTIFY_MESSAGE_RECEIVED, object));
         }
     }

    /**
      * 消息通知
      * @param what  封装的消息体类型
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
      * 消息通知
      * @param what  封装的消息体类型
      * @return None
      */
     public void notify(Handler handler, int what){
    	 if (handler != null) {
    		 handler.sendMessage(handler.obtainMessage(what));
    	 }
     }
     
     /**
      * 消息通知
      * @param msg
      * @return None
      */
     public void notify(Handler handler, Message msg){
    	 if (handler != null) {
    		 handler.sendMessage(msg);
    	 }
     }
     
     /**
      * 消息通知
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
      * 判断注册器中是否有重复的事件处理器
      * @param handler  事件处理器
      * @return true重复,false不重复
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
