package cn.zf233.xcloud.exception;

import androidx.annotation.NonNull;

import cn.zf233.xcloud.util.FileUtil;

/**
 * Created by zf233 on 12/9/20
 */
public class GlobalCrashHandler implements Thread.UncaughtExceptionHandler {

    private static final GlobalCrashHandler globalCrashHandler = new GlobalCrashHandler();

    public synchronized static GlobalCrashHandler getGlobalCrashHandler() {
        return globalCrashHandler;
    }

    private GlobalCrashHandler() {
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        FileUtil.createAndWriteUncaughtExceptionLog(e.getMessage());
    }
}
