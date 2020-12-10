package cn.zf233.xcloud.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

/**
 * Created by zf233 on 12/7/20
 */
public class JumpActivityUtil {

    public static void jumpActivity(Activity activity, Intent intent, Long delayMillis, Boolean isNotFinish) {
        new Handler().postDelayed(() -> {
            activity.startActivity(intent);
            if (isNotFinish) {
                activity.finish();
            }
        }, delayMillis);
    }
}
