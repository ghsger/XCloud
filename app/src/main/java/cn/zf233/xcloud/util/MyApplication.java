package cn.zf233.xcloud.util;

import android.app.Application;
import android.content.Context;

import cn.zf233.xcloud.exception.GlobalCrashHandler;

public class MyApplication extends Application {

    /**
     * System context
     */
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler(GlobalCrashHandler.getGlobalCrashHandler());
    }

    /**
     * get system context used for ToastUtil class
     */
    public static Context getAppContext() {
        return mAppContext;
    }
}