package com.shwy.bestjoy.utils;


/**
 * version 版本号  从1开始
 * date 日期  
 * importance 重要程度  重要程度分为0和1，0表示强制更新, 1表示普通，其他的暂时不支持
 * size 更新大小 
 * apk 更新apk的地址，如果是default表示的是默认路径http://www.bjnote.com/down4/bjnote.apk
 * note 更新说明 如果是http开头的，会以网页的形式显示
 * @author chenkai
 *
 */
public interface IServiceAppInfo {
	 /**最近一次执行过自动检测的时间*/
    public static final String KEY_SERVICE_APP_INFO_CHECK_TIME = "service_app_info_timestamp";
    public static final String KEY_SERVICE_APP_INFO_VERSION_CODE = "service_app_info_version_code";
    public static final String KEY_SERVICE_APP_INFO_VERSION_NAME = "service_app_info_version_name";
    public static final String KEY_SERVICE_APP_INFO_RELEASENOTE = "service_app_info_releasenote";
    public static final String KEY_SERVICE_APP_INFO_APK_URL = "service_app_info_apk_url";
    public static final String KEY_SERVICE_APP_INFO_APK_SIZE = "service_app_info_apk_sizel";
    public static final String KEY_SERVICE_APP_INFO_RELEASEDATE = "service_app_info_releasedate";
    public static final String KEY_SERVICE_APP_INFO_IMPORTANCE = "service_app_info_importance";


	public static final String KEY_VERSION_CODE = "version";
	public static final String KEY_VERSION_NAME = "versionCodeName";
	public static final String KEY_DATE = "date";
	public static final String KEY_IMPORTANCE = "importance";
	public static final String KEY_SIZE = "size";
	public static final String KEY_APK = "apk";
	public static final String KEY_NOTE = "note";

	public static final String KEY_TIME = "check_time";

    public static final String KEY_SERVICE_APP_INFO_MD5 = "service_app_info_md5";
    public static final String KEY_MD5 = "md5";

	public static final int IMPORTANCE_MUST = 1;
	public static final int IMPORTANCE_OPTIONAL = 0;

}
