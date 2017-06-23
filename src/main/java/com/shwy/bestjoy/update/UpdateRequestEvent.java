package com.shwy.bestjoy.update;

import com.shwy.bestjoy.utils.ServiceAppInfo;

/**
 * Created by bestjoy on 2017/6/22.
 */

public class UpdateRequestEvent {
    /**检查APP*/
    public final int ACTION_CHECK_APP = 1;
    /**检查APP基本数据*/
    public final int ACTION_CHECK_APP_BASIC_DATABASE = 2;

    /***
     * 是否是强制的
     */
    public boolean force=false;

    /**上一次检查的app新版本信息对象*/
    public ServiceAppInfo lastServiceAppInfo;

}
