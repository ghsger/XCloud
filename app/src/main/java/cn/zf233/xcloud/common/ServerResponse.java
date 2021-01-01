package cn.zf233.xcloud.common;

import java.util.List;

import cn.zf233.xcloud.entity.AbsolutePath;

/**
 * Created by zf233 on 12/1/20
 */
public class ServerResponse<T> {
    private Integer status;
    private String msg;
    private List<AbsolutePath> absolutePath;
    private T data;

    public boolean isSuccess() {
        return this.status == ResponseCodeENUM.SUCCESS.getCode();
    }

    public ServerResponse() {
    }

    public ServerResponse(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<AbsolutePath> getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(List<AbsolutePath> absolutePath) {
        this.absolutePath = absolutePath;
    }
}
