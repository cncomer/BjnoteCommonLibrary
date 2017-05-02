package com.shwy.bestjoy.account;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;

import com.shwy.bestjoy.ComApplication;
import com.shwy.bestjoy.utils.SecurityUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bestjoy on 2017/5/2.
 */

public abstract class AbstractAccountManager {
    private static final String TAG = "AccountManagerBaseImpl";
    protected AbstractAccountObject accountObject;
    protected Context mContext;
    protected SharedPreferences mSharedPreferences;

    private List<IAccountChangeCallback> mAccountChangeCallbackList = new LinkedList<IAccountChangeCallback>();

    protected ContentResolver mContentResolver;



    public synchronized void setContext(Context context) {
        mContext = context;
        if (mContext == null) {
            throw new RuntimeException("MyAccountManager.setContext(null), you must apply a Context object.");
        }
        mContentResolver = mContext.getContentResolver();
        accountObject = null;
        mSharedPreferences = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        initAccountObject();
        initAccountOtherData();
        notifyAccountChange(accountObject);
    }

    public synchronized void addAccountChangeCallback(IAccountChangeCallback callback) {
        if (!mAccountChangeCallbackList.contains(callback)) {
            mAccountChangeCallbackList.add(callback);
        }
    }

    public synchronized void removeAccountChangeCallback(IAccountChangeCallback callback) {
        if (mAccountChangeCallbackList.contains(callback)) {
            mAccountChangeCallbackList.remove(callback);
        }
    }

    public synchronized void notifyAccountChange(final AbstractAccountObject accountObject) {

        ComApplication.getInstance().postAsync(new Runnable() {

            @Override
            public void run() {
                for(IAccountChangeCallback callback : mAccountChangeCallbackList) {
                    callback.onAccountChanged(accountObject);
                }
            }
        });

    }

    public abstract void initAccountObject();
    public abstract void initAccountOtherData();


    public synchronized AbstractAccountObject getAccountObject() {
        return accountObject;
    }

    public synchronized String getDefaultPhoneNumber() {
        return accountObject != null ? getAccountObject().mAccountTel : null;
    }

    public synchronized String getCurrentAccountMd() {
        return getCurrentAccountUid();
    }

    public synchronized String getCurrentAccountUid() {
        return accountObject != null ? getAccountObject().mAccountUid : "";
    }
    /***
     * 只有明确知道uid是数字的时候才调用这个方法，否则使用getCurrentAccountUid()
     * @return
     */
    public synchronized long getCurrentAccountId() {
        return accountObject != null ? Long.valueOf(getAccountObject().mAccountUid) : -1;
    }
    /**默认情况即使用户没有登录，系统会初始化一个演示账户，一旦用户登陆了，就会将演示账户删掉*/
    public synchronized boolean hasLoginned() {
        return accountObject != null && getAccountObject().mAccountId > 0;
    }

    /**
     * 返回上一次登陆时候使用的用户名
     * @return
     */
    public synchronized String getLastUsrTel() {
        return mSharedPreferences.getString("lastUserTel", "");
    }

    public synchronized void saveLastUsrTel(String userName) {
        mSharedPreferences.edit().putString("lastUserTel", (userName == null ? "" : userName)).commit();
    }

    public synchronized boolean saveAccountObject(ContentResolver cr, AbstractAccountObject accountObject) {

        if (this.accountObject != accountObject) {
            boolean success = accountObject.saveInDatebase(cr, null);
            if (success) {
                updateAccount();
                return true;
            }
        }
        return false;

    }
    /**
     * 更新账户，每当我们增删家和保修卡数据的时候，调用该方法可以同步当前账户信息.
     */
    public synchronized void updateAccount() {
        accountObject = null;
        initAccountObject();
        initAccountOtherData();
        notifyAccountChange(accountObject);
    }

    /**
     * md5（cell+pwd）
     * @return
     */
    public synchronized String getMd5Key() {
        if (accountObject == null) {
            return "";
        }
        return SecurityUtils.MD5.md5(getAccountObject().mAccountTel + getAccountObject().mAccountPwd);
    }
}
