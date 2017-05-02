package com.shwy.bestjoy.account;

import android.database.Cursor;

import com.shwy.bestjoy.utils.InfoInterfaceImpl;

/**
 * Created by bestjoy on 2017/5/2.
 */

public abstract class AbstractAccountObject extends InfoInterfaceImpl {
    public String mAccountUid = "";
    public String mAccountTel = "";
    public String mAccountPwd = "";
    public long mAccountId = -1;
    public String mAccountName = "";
    public String mAccountAvator = "";

    public abstract void initAccountFromDatabase(Cursor cursor);
}
