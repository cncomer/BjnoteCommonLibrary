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
	/**
	 * If param is null, we use fallback instead of param.
	 * @param param
	 * @param fallback
	 * @return
	 */
	public UrlEncodeStringBuilder appendUrlEncodedString(String param, String fallback) {
		if (param == null) {
			mSb.append(fallback);
		} else {
			mSb.append(URLEncoder.encode(param));
		}
		return this;
	}
	/**
	 * If param is null, we use "" instead of param.
	 * @param param
	 * @param fallback
	 * @return
	 */
	public UrlEncodeStringBuilder appendUrlEncodedStringNotNull(String param) {
		return appendUrlEncodedString(param, "");
	}
	
	@Override
	public String toString() {
		return mSb.toString();
	}
}
