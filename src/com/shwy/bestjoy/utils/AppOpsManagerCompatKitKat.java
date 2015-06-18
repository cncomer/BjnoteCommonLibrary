package com.shwy.bestjoy.utils;

import android.app.AppOpsManager;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by bestjoy on 15/6/18.
 */
public class AppOpsManagerCompatKitKat {
    private static final String TAG = "AppOpsManagerKitKat";
    private Context mContext;
    private AppOpsManager mAppOpsManager;

    public AppOpsManagerCompatKitKat(Context context) {
        mContext = context;
        mAppOpsManager = (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
    }

    public boolean checkOp(int op) {
        try {
            Method checkOpM = AppOpsManager.class.getDeclaredMethod("checkOp", new Class[]{int.class, int.class, String.class});

            int check = (int) checkOpM.invoke(mAppOpsManager, op, android.os.Process.myUid(), mContext.getPackageName());
            Log.d(TAG, "checkOp op=" + op + ", check=" + check);
            if (check == AppOpsManager.MODE_ALLOWED) {
                return true;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
