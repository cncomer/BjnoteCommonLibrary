package com.shwy.bestjoy.utils;

import android.content.Context;

import com.shwy.bestjoy.R;

public class ServiceAppInfoCompat extends ServiceAppInfo{

	public ServiceAppInfoCompat(Context context) {
		super(context);
	}
	
	public ServiceAppInfoCompat(Context context, String token) {
		super(context, token);
	}
	
	public String buildReleasenote() {
		StringBuilder sb = new StringBuilder();
		sb.append(mContext.getString(R.string.msg_app_release_time, mReleaseDate)).append("\n");
		sb.append(mContext.getString(R.string.msg_app_release_size, mSizeStr)).append("\n").append("\n");
		sb.append(mReleaseNote);
		return sb.toString();
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Find new version[mVersionCode=").append(mVersionCode).append(", mVersionName=").append(mVersionName)
		.append(", content=").append(buildReleasenote()).append("]");
		return sb.toString();
	}

}
