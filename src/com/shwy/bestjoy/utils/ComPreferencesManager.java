package com.shwy.bestjoy.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ComPreferencesManager {
	
	private static final ComPreferencesManager INSTANCE = new ComPreferencesManager();
	private Context Context;
	public SharedPreferences mFirstPreferManager;
	public SharedPreferences mPreferManager;
	
	public static final String KEY_LATEST_VERSION = "preferences_latest_version";
	public static final String KEY_LATEST_VERSION_CODE_NAME = "preferences_latest_version_code_name";
	public static final String KEY_LATEST_VERSION_INSTALL = "preferences_latest_version_install";
	public static final String KEY_LATEST_VERSION_LEVEL = "preferences_latest_version_level";

	private ComPreferencesManager() {}
	
	public static ComPreferencesManager getInstance() {
		return INSTANCE;
	}
	
	public void setContext(Context context) {
		Context = context;
		mFirstPreferManager = context.getSharedPreferences("first_launch", Context.MODE_PRIVATE);
		mPreferManager = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	/***
	 * 是否第一次载入
	 * @param key
	 * @param defaultValues
	 * @return
	 */
	public boolean isFirstLaunch(String key, boolean defaultValues) {
		return mFirstPreferManager.getBoolean(key, defaultValues);
	}
	
	public boolean setFirstLaunch(String key, boolean defaultValues) {
		return mFirstPreferManager.edit().putBoolean(key, defaultValues).commit();
	}
	/**
	 * 该操作会清空所有已设置的第一次载入状态
	 */
	public void resetFirsetLaunch() {
		mFirstPreferManager.edit().clear().commit();
	}

}
