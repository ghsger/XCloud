package cn.zf233.xcloud.common;

/**
 * Created by zf233 on 12/2/20
 */
public enum RequestTypeENUM {

    UNKNOWN_TYPE(0, "unknown"),
    DOWNLOAD_TYPE(2, "download"),
    UPLOAD_TYPE(3, "upload"),
    REMOVE_TYPE(4, "remove"),
    FLUSH_LISTVIEW(5, "flushListView");

    private final Integer code;
    private final String desc;

    RequestTypeENUM(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (RequestTypeENUM value : RequestTypeENUM.values()) {
            if (code.equals(value.getCode())) {
                return value.getDesc();
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
