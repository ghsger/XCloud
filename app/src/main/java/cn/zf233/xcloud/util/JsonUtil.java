package cn.zf233.xcloud.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * Created by zf233 on 11/28/20
 */
public class JsonUtil {

    private static final Gson gson = new Gson();

    // Object-->Json
    public static String toGson(Object object) {
        return gson.toJson(object);
    }

    // Json-->Object
    public static <T> T toObject(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    // Json-->Object
    public static <T> T toObject(String json, TypeToken<T> token) {
        return gson.fromJson(json, token.getType());
    }

}
