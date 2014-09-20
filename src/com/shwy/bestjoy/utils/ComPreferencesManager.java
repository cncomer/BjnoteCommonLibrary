package com.shwy.bestjoy.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ComPreferencesManager {
	
	private static final ComPreferencesManager INSTANCE = new ComPreferencesManager();
	private Context Context;
	public SharedPreferences mPreferManager;
	
	private ComPreferencesManager() {}
	
	public static ComPreferencesManager getInstance() {
		return INSTANCE;
	}
	
	public void setContext(Context context) {
		Context = context;
		mPreferManager = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	/***
	 * 是否第一次载入
	 * @param key
	 * @param defaultValues
	 * @return
	 */
	public boolean isFirstLaunch(String key, boolean defaultValues) {
		return mPreferManager.getBoolean(key, defaultValues);
	}
	
	public boolean setFirstLaunch(String key, boolean defaultValues) {
		return mPreferManager.edit().putBoolean(key, defaultValues).commit();
	}

}
