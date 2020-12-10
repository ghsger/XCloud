package cn.zf233.xcloud.common;

import android.os.Environment;

/**
 * Created by zf233 on 12/2/20
 */
public interface SdPathConst {
    String sdPath = Environment.getExternalStorageDirectory().getPath() + "/xcloud";
}
