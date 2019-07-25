package org.guoxm.mybatis.expand.tools;

import com.google.gson.Gson;

import java.io.Reader;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    // disableHtmlEscaping 防止特殊字符出现乱码
    // enableComplexMapKeySerialization  当Map的key为复杂对象时,需要开启该方法
    private static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().create();
    
    public static String toJson(Object o) {
        return gson.toJson(o);
    }
    public static<T> T fromJson(String str, Class<T> clz) {
        return gson.fromJson(str, clz);
    }
    public static<T> T fromJson(String str, Type type) {
        return gson.fromJson(str, type);
    }
    public static<T> T fromJson(Reader r, Class<T> clz) {
        return gson.fromJson(r, clz);
    }
}
