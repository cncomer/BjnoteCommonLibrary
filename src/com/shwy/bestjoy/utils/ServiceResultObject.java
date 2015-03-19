package com.shwy.bestjoy.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServiceResultObject {

	public int mStatusCode = 0;
	public String mStatusMessage;
	public JSONObject mJsonData;
	public String mStrData;
	public JSONArray mJsonArrayData;
    public String mRawString = "";

    public Object mObject;

	public static ServiceResultObject parse(String content) {
		ServiceResultObject resultObject = new ServiceResultObject();
        resultObject.mRawString = content;
		if (TextUtils.isEmpty(content)) {
			return resultObject;
		}
		try {
			JSONObject jsonObject = new JSONObject(content);
			resultObject.mStatusCode = Integer.parseInt(jsonObject.getString("StatusCode"));
			resultObject.mStatusMessage = jsonObject.getString("StatusMessage");
			DebugUtils.logD("HaierResultObject", "StatusCode = " + resultObject.mStatusCode);
			DebugUtils.logD("HaierResultObject", "StatusMessage = " + resultObject.mStatusMessage);
			try {
				resultObject.mJsonData = jsonObject.getJSONObject("Data");
			} catch (JSONException e) {
				resultObject.mStrData = jsonObject.getString("Data");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			resultObject.mStatusMessage = e.getMessage();
		}
		return resultObject;
	}

	public static ServiceResultObject parseArray(String content) {
		ServiceResultObject resultObject = new ServiceResultObject();
        resultObject.mRawString = content;
		if (TextUtils.isEmpty(content)) {
			return resultObject;
		}
		try {
			JSONObject jsonObject = new JSONObject(content);
			resultObject.mStatusCode = Integer.parseInt(jsonObject.getString("StatusCode"));
			resultObject.mStatusMessage = jsonObject.getString("StatusMessage");
			DebugUtils.logD("HaierResultObject", "StatusCode = " + resultObject.mStatusCode);
			DebugUtils.logD("HaierResultObject", "StatusMessage = " + resultObject.mStatusMessage);
			
			try {
				resultObject.mJsonArrayData = jsonObject.getJSONArray("Data");
			} catch (JSONException e) {
				resultObject.mStrData = jsonObject.getString("Data");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			resultObject.mStatusMessage = e.getMessage();
		}
		return resultObject;
	}

	public boolean isOpSuccessfully() {
		return mStatusCode == 1;
	}

}
