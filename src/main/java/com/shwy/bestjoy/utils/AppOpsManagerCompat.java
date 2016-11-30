package com.shwy.bestjoy.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

/**
 * Created by bestjoy on 15/4/16.
 */
public class AppOpsManagerCompat {
    private static final String TAG = "AppOpsManagerCompat";

    private Context mContext;
    private PackageManager mPackageManager;
    private static final AppOpsManagerCompat INSTANCE = new AppOpsManagerCompat();

    public void setContext(Context context) {
        mContext = context;
        mPackageManager = context.getPackageManager();
        IMPL.wrap(context);
    }

    public static AppOpsManagerCompat getInstance() {
        return INSTANCE;
    }


    public boolean checkAppOp(int op) {
        return IMPL.checkOp(op);
    }

    /**
     * 检查某个App是否有permission权限
     * @param permission
     * @return
     */
    public boolean checkPermission(String permission) {
        boolean granted = (PackageManager.PERMISSION_GRANTED == mPackageManager.checkPermission(permission, mContext.getPackageName()));
        Log.d(TAG, "checkPermission permission=" + permission + ", granted=" + granted);
        return granted;
    }


    static final AppOpsManagerCompatImpl IMPL;
    static {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.KITKAT) {
            IMPL = new KitkatAppOpsManagerImpl();
        } else {
            IMPL = new BaseAppOpsManagerImpl();
        }
    }

    public static interface AppOpsManagerCompatImpl{
        boolean checkOp(int op);
        void wrap(Context context);
    }

    public static class BaseAppOpsManagerImpl implements AppOpsManagerCompatImpl{
        private Context _context;

        @Override
        public boolean checkOp(int op) {
            return true;
        }

        @Override
        public void wrap(Context context) {
            _context = context;
        }
    }

    public static class KitkatAppOpsManagerImpl implements AppOpsManagerCompatImpl {
        private Context _context;
        private AppOpsManagerCompatKitKat mAppOpsManagerKitKat;

        @Override
        public boolean checkOp(int op) {
            return mAppOpsManagerKitKat.checkOp(op);
        }

        @Override
        public void wrap(Context context) {
            _context = context;
            mAppOpsManagerKitKat = new AppOpsManagerCompatKitKat(context);

        }
    }
}
