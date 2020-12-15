package cn.zf233.xcloud.util;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import cn.zf233.xcloud.common.RequestTypeENUM;
import cn.zf233.xcloud.common.ServerResponse;
import cn.zf233.xcloud.entity.RequestBody;
import cn.zf233.xcloud.entity.User;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zf233 on 11/28/20
 * Singleton
 */
public class RequestUtil {

    private Boolean isUsed;
    private Integer requestType;
    private String fileName;

    public static final RequestUtil requestUtil = new RequestUtil();

    public static synchronized RequestUtil getRequestUtil() {
        return requestUtil;
    }

    // common request
    public <T> ServerResponse<T> requestUserXCloudServer(String url, RequestBody requestBody, TypeToken<ServerResponse<T>> token) {
        try {
            OkHttpClient client = new OkHttpClient();
            FormBody body = new FormBody.Builder()
                    .add("requestBody", JsonUtil.toGson(requestBody))
                    .build();
            Request request = new Request.Builder()
                    .url(url).post(body)
                    .build();
            Response response;
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String json = response.body() != null ? response.body().string() : null;
                if (json != null) {
                    return JsonUtil.toObject(json, token);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // file download
    public File fileDownload(String url, User user, Integer fileId) {
        try {
            OkHttpClient client = new OkHttpClient
                    .Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();
            FormBody body = new FormBody.Builder()
                    .add("id", user.getId().toString())
                    .add("username", user.getUsername())
                    .add("password", user.getPassword())
                    .add("fileid", fileId.toString())
                    .add("appVersionCode", RequestTypeENUM.VERSION_FAILURE.getDesc())
                    .build();
            Request request = new Request.Builder()
                    .url(url).post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String header = response.header("Content-Disposition");
                if (header != null && !"".equals(header)) {
                    String filename = header.substring(header.indexOf("filename") + "filename".length() + 2, header.lastIndexOf("\""));
                    InputStream inputStream = response.body() != null ? response.body().byteStream() : null;
                    if (null == inputStream) {
                        return null;
                    }
                    return FileUtil.outputFile(inputStream, filename);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    // file remove
    public ServerResponse fileRemove(String url, User user, Integer fileID) {
        try {
            OkHttpClient client = new OkHttpClient();
            FormBody body = new FormBody.Builder()
                    .add("username", user.getUsername())
                    .add("password", user.getPassword())
                    .add("fileid", fileID.toString())
                    .add("appVersionCode", RequestTypeENUM.VERSION_FAILURE.getDesc())
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response;
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String json = response.body() != null ? response.body().string() : null;
                if (json != null) {
                    return JsonUtil.toObject(json, ServerResponse.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    // file upload
    public ServerResponse uploadFile(String url, User user, File uploadFile) {
        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("myFile", uploadFile.getName(),
                        okhttp3.RequestBody.create(MediaType.parse("multipart/form-data"), uploadFile))
                .addFormDataPart("id", user.getId().toString())
                .addFormDataPart("username", user.getUsername())
                .addFormDataPart("password", user.getPassword())
                .addFormDataPart("appVersionCode", RequestTypeENUM.VERSION_FAILURE.getDesc())
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ServerResponse serverResponse = new ServerResponse();
                serverResponse.setStatus(200);
                return serverResponse;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public Boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Boolean used) {
        isUsed = used;
    }

    public Integer getRequestType() {
        return requestType;
    }

    public void setRequestType(Integer requestType) {
        this.requestType = requestType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private RequestUtil() {
        this.isUsed = false;
        this.requestType = RequestTypeENUM.UNKNOWN_TYPE.getCode();
        this.fileName = null;
    }
}
