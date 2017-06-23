package com.shwy.bestjoy.update;

import com.shwy.bestjoy.utils.ServiceAppInfo;

/**
 * Created by bestjoy on 2017/6/22.
 */

public class UpdateResultEvent {
    /**检查APP*/
    public static final int ACTION_CHECK_APP = 1;
    /**检查APP基本数据*/
    public static final int ACTION_CHECK_APP_BASIC_DATABASE = 2;

    /**上一次检查的app新版本信息对象*/
    public ServiceAppInfo serviceAppInfo;

    public int action = 0;

}
