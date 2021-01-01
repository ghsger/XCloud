package cn.zf233.xcloud.common;

/**
 * Created by zf233 on 11/29/20
 */
public enum Const {

    CURRENT_USER(0, "current_user"),
    MSG(1, "msg"),
    LOG_FILE_NAME(2, "xcloud_log.txt"),
    ERROR(3, "error");

    private Integer code;
    private String desc;

    Const(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }



    public String getDesc() {
        return desc;
    }

    public Integer getCode() {
        return code;
    }
}
