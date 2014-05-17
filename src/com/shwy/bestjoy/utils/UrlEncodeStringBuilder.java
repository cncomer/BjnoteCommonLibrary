package com.shwy.bestjoy.utils;

import java.net.URLEncoder;

public class UrlEncodeStringBuilder{

	private StringBuilder mSb;
	public UrlEncodeStringBuilder() {
		mSb = new StringBuilder();
	}
	public UrlEncodeStringBuilder(CharSequence charSequence) {
		mSb = new StringBuilder(charSequence);
	}
	
	public <T> UrlEncodeStringBuilder append(T param) {
		mSb.append(param);
		return this;
	}
	
	public UrlEncodeStringBuilder appendUrlEncodedString(String param) {
		mSb.append(URLEncoder.encode(param));
		return this;
	}
	
	@Override
	public String toString() {
		return mSb.toString();
	}
}
