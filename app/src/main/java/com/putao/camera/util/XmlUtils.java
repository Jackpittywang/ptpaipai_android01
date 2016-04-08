package com.putao.camera.util;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.Serializable;

/**
 * Created by guchenkai on 2016/1/6.
 */
public class XmlUtils {

    public static String xmlToJson(String xml, String rootName) {
        try {
            JSONObject object = XML.toJSONObject(xml);
            return object.getString(rootName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends Serializable> T xmlToModel(String xml, String rootName, Class<? extends Serializable> clazz) {
        String json = xmlToJson(xml, rootName);
        return (T) JSON.parseObject(json, clazz);
    }
}
