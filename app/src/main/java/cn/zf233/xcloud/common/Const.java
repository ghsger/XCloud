package cn.zf233.xcloud.common;

/**
 * Created by zf233 on 11/29/20
 */
public enum Const {

    CURRENT_USER(0, "current_user"),
    MSG(1, "msg"),
    WELCOME_MSG(2, "welcome_msg"),
    LOG_FILE_NAME(3, "xcloud_log.txt");

    private Integer code;
    private String desc;

    Const(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
