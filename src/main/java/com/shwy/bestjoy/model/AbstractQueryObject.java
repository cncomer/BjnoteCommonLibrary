package com.shwy.bestjoy.model;

import android.text.TextUtils;

import com.shwy.bestjoy.utils.DebugUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by chenkai on 15/12/14.
 */
public abstract class AbstractQueryObject {

        public HashMap<String, String> mQueryMap = new HashMap<String, String>();

    public AbstractQueryObject() {
        initQueryMap(mQueryMap);
    }

    /**
     * 初始化条件键值对,比如
     * mQueryMap.put(key, value);
     * ............
     * @param mQueryMap
     */
    public abstract void initQueryMap(HashMap<String, String> mQueryMap);

    /**
     * 设置条件，比如设置潜客id
     * setCondition(key, value).setCondition(key, value);
     * @param key
     * @param value
     * @return
     */
        public AbstractQueryObject setCondition(String key, String value) {
                mQueryMap.put(key, value);
                return this;
        }
        /**
         * 设置条件，比如设置潜客id
         * setUrlDecodedCondition(key, value).setUrlDecodedCondition(key, value);
         * @param key
         * @param value  URLDecoder后的字符串
         * @return
         */
        public AbstractQueryObject setUrlDecodedCondition(String key, String value) throws UnsupportedEncodingException {
                mQueryMap.put(key, URLDecoder.decode(value, "utf-8"));
                return this;
        }


        public String buildQuery(){
                StringBuilder query = new StringBuilder();
                Iterator<String> keys = mQueryMap.keySet().iterator();
                while(keys.hasNext()) {
                      String key= keys.next();
                      if(!TextUtils.isEmpty(mQueryMap.get(key))) {
                          query.append(key).append("=").append(mQueryMap.get(key)).append('&');
                      }
                }
                query.deleteCharAt(query.length()-1);
                DebugUtils.logD("QueryObjectImpl", "buildQuery " + query.toString());
                return query.toString();
        }

    public HashMap<String, String> buildQueryMap(){
        HashMap<String, String> param = new HashMap<>();
        StringBuilder query = new StringBuilder();
        Iterator<String> keys = mQueryMap.keySet().iterator();
        while(keys.hasNext()) {
            String key= keys.next();
            if(!TextUtils.isEmpty(mQueryMap.get(key))) {
                param.put(key, mQueryMap.get(key));
                query.append(key).append("=").append(mQueryMap.get(key)).append('&');
            }
        }
        query.deleteCharAt(query.length()-1);
        DebugUtils.logD("QueryObjectImpl", "buildQueryMap " + query.toString());
        return param;
    }

    public static HashMap<String, String> buildQueryMap(HashMap<String, String> map){
        HashMap<String, String> param = new HashMap<>();
        StringBuilder query = new StringBuilder();
        Iterator<String> keys = map.keySet().iterator();
        while(keys.hasNext()) {
            String key= keys.next();
            if(!TextUtils.isEmpty(map.get(key))) {
                param.put(key, map.get(key));
                query.append(key).append("=").append(map.get(key)).append('&');
            }
        }
        query.deleteCharAt(query.length()-1);
        DebugUtils.logD("QueryObjectImpl", "buildQueryMap " + query.toString());
        return param;
    }

    public static HashMap<String,String> buildQueryStringToMap(String strQuery){
        HashMap<String,String> map=new HashMap<String,String>();
        if(!TextUtils.isEmpty(strQuery)){
            if(strQuery.contains("&")){
                String[] strs=strQuery.split("&");
                for(String query:strs) {
                    if(query.contains("=")) {
                        String[] mapString = query.split("=");
                        if(mapString.length==2){
                            if(!TextUtils.isEmpty(mapString[1])) {
                                map.put(mapString[0], mapString[1]);
                            }
                        }else{
                            continue;
                        }
                    }else{
                        continue;
                    }
                }
            }else{
                return map;
            }
        }
        return map;
    }

    public static String buildQuery(HashMap<String, String> map){
        StringBuilder query = new StringBuilder();
        Iterator<String> keys = map.keySet().iterator();
        while(keys.hasNext()) {
            String key= keys.next();
            if(!TextUtils.isEmpty(map.get(key))) {
                query.append(key).append("=").append(map.get(key)).append('&');
            }
        }
        query.deleteCharAt(query.length()-1);
        DebugUtils.logD("QueryObjectImpl", "buildQuery " + query.toString());
        return query.toString();
    }


    public JSONObject buildJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        Iterator<String> keys = mQueryMap.keySet().iterator();
        while(keys.hasNext()) {
            String key= keys.next();
            jsonObject.put(key, mQueryMap.get(key));
        }
        DebugUtils.logD("QueryObjectImpl", "buildJsonObject " + jsonObject.toString());
        return jsonObject;
    }

}
