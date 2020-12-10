package cn.zf233.xcloud.common;

/**
 * Created by zf233 on 11/28/20
 */
public enum  ResponseCodeENUM {

    SUCCESS(200, "SUCCESS"),
    ERROR(500, "ERROR"),
    NEED_LOGIN(401, "NEED_LOGIN"),
    ILLEGAL_ARGUMENT(400, "ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;


    ResponseCodeENUM(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
