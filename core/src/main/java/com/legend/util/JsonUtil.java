package com.legend.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Created by breeze on 2017/2/26.
 */
public class JsonUtil {
    public static String toJson(Object object) {
        return JSONObject.toJSONStringWithDateFormat(object, "yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat);
    }

    public static JSONObject parse(String text) {
        return JSONObject.parseObject(text);
    }
    public static JSONArray parseArray(String text){
        return JSONArray.parseArray(text);
    }

    public static <T> T parse(String text, Class<T> clazz) {
        return JSONObject.parseObject(text, clazz);
    }

    public static Object getObject(JSONObject jsonObject, String key){
        try {
            if(!jsonObject.containsKey(key))
                return null;
            return jsonObject.get(key);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static Object toJsonObj(Object object) {
        String s = JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
        return JSON.toJSONString( object, SerializerFeature.DisableCircularReferenceDetect);
    }


}
