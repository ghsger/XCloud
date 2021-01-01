package cn.zf233.xcloud.common;

/**
 * Created by zf233 on 11/28/20
 */
public enum RequestURL {

    LOGIN_URL(0, "http://182.92.233.100:8080/xcloud/user/login"),
    HOME_URL(1, "http://182.92.233.100:8080/xcloud/user/home"),
    REGIST_URL(2, "http://182.92.233.100:8080/xcloud/user/regist"),
    UPDATE_URL(3, "http://182.92.233.100:8080/xcloud/user/update"),
    DOWNLOAD_URL(4, "http://182.92.233.100:8080/xcloud/file/download"),
    REMOVE_FILE_URL(5, "http://182.92.233.100:8080/xcloud/file/delete"),
    UPLOAD_FILE_URL(6, "http://182.92.233.100:8080/xcloud/file/upload"),
    CREATE_FOLDER(6, "http://182.92.233.100:8080/xcloud/file/createfolder");

    private final Integer code;
    private final String desc;

    RequestURL(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

