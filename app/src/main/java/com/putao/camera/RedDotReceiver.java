package com.putao.camera;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.putao.mtlib.tcp.PTMessageReceiver;
import com.sunnybear.library.controller.eventbus.EventBusHelper;
import com.sunnybear.library.util.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RedDotReceiver extends PTMessageReceiver {
    public static final String ME_TABBAR = "me_tabbar";
    public static final String COMPANION_TABBAR = "companion_tabbar";
    public static final String ME_MESSAGECENTER = "me_messageCenter";
    public static final String MESSAGECENTER_NOTICE = "messageCenter_notice";
    public static final String MESSAGECENTER_REPLY = "messageCenter_reply";
    public static final String MESSAGECENTER_PRAISE = "messageCenter_praise";
    public static final String MESSAGECENTER_REMIND = "messageCenter_remind";
    public static final String APPPRODUCT_ID = "appProduct_id";

    public static final String MESSAGECENTER = "messageCenter";
    public static final String REPLY = "reply";
    public static final String PRAISE = "praise";
    public static final String REMIND = "remind";
    public static final String NOTICE = "notice";
    public static final String ACCOMPANYNUMBER = "accompanyNumber";
    public static final String SERVICE_ID = "service_id";
    public static final String ID = "id";
    public static final String TYPE = "type";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String message = intent.getExtras().getString(KeyMessage);
            setResult(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*String result = message.substring(message.indexOf("{") + 1, message.indexOf("}") + 1);
        result = result.substring(message.indexOf("{"), result.length());
        JSONObject jsonObject = JSONObject.parseObject(result);*/

    }

    private void setResult(String result) {
        result = result.replaceAll("\\{\"messageCenter\":null,\"accompanyNumber\":\\[\\{\"service_id\":\"6000\",\"id\":234,\"type\":\"article\"\\}\\]\\}", "");
        Logger.d("result-----------------", result);
        Pattern p1 = Pattern.compile(result.endsWith("]}") ? "\\{.+?\\]\\}" : "\\{.+?null\\}");
        Matcher m1 = p1.matcher(result);
        if (m1.find()) {
            JSONObject object = JSONObject.parseObject(m1.group(0));
            JSONObject messageCenter = object.getJSONObject(MESSAGECENTER);
            if (null != messageCenter) {
                String reply = messageCenter.getString(REPLY);
                String praise = messageCenter.getString(PRAISE);
                String remind = messageCenter.getString(REMIND);
                String notice = messageCenter.getString(NOTICE);
                //主页"我"位置红点
                EventBusHelper.post(ME_TABBAR, ME_TABBAR);
                //"我"位置"消息中心"红点
                EventBusHelper.post(ME_MESSAGECENTER, ME_MESSAGECENTER);
                //消息中心通知红点
                if ("1".equals(notice)) {
                    EventBusHelper.post(MESSAGECENTER_NOTICE, MESSAGECENTER);
                }
                //消息中心回复红点
                if ("1".equals(remind)) {
                    EventBusHelper.post(MESSAGECENTER_REMIND, MESSAGECENTER);
                }
                //消息中心赞红点
                if ("1".equals(praise)) {
                    EventBusHelper.post(MESSAGECENTER_PRAISE, MESSAGECENTER);
                }
                //消息中心提醒红点
                if ("1".equals(reply)) {
                    EventBusHelper.post(MESSAGECENTER_REPLY, MESSAGECENTER);
                }
            }

            JSONArray accompanyNumber = object.getJSONArray(ACCOMPANYNUMBER);
            //陪伴位置提醒红点
            if (null != accompanyNumber) {
                //if (result.contains("\"id\":234")) return;
                EventBusHelper.post(accompanyNumber, COMPANION_TABBAR);
            }
        }
    }
       /* Pattern p1 = Pattern.compile("\"location_dot\":([^}]*)");
        Matcher m1 = p1.matcher(result);
        String group;
        while (m1.find()) {
            group = m1.group(0).substring(15);//截取json字符串
            JSONObject jsonObject = JSONObject.parseObject(group + "}");
            //主页"我"位置红点
            if (me == 0 && jsonObject.getInteger(ME_TABBAR) == 1) {
                me = 1;
                EventBusHelper.post(ME_TABBAR, ME_TABBAR);
            }
            //"我"位置"消息中心"红点
            if (messagecenter == 0 && jsonObject.getInteger(ME_MESSAGECENTER) == 1) {
                messagecenter = 1;
                EventBusHelper.post(ME_MESSAGECENTER, ME_MESSAGECENTER);
            }
            //消息中心通知红点
            if (notice == 0 && jsonObject.getInteger(MESSAGECENTER_NOTICE) == 1) {
                notice = 1;
                EventBusHelper.post(MESSAGECENTER_NOTICE, MESSAGECENTER);
            }
            //消息中心回复红点
            if (reply == 0 && jsonObject.getInteger(MESSAGECENTER_REPLY) == 1) {
                reply = 1;
                EventBusHelper.post(MESSAGECENTER_REPLY, MESSAGECENTER);
            }
            //消息中心赞红点
            if (praise == 0 && jsonObject.getInteger(MESSAGECENTER_PRAISE) == 1) {
                praise = 1;
                EventBusHelper.post(MESSAGECENTER_PRAISE, MESSAGECENTER);
            }
            //消息中心提醒红点
            if (remind == 0 && jsonObject.getInteger(MESSAGECENTER_REMIND) == 1) {
                remind = 1;
                EventBusHelper.post(MESSAGECENTER_REMIND, MESSAGECENTER);
            }
            String appproduct_id = jsonObject.getString(APPPRODUCT_ID);
            if (!TextUtils.isEmpty(appproduct_id)) {
                EventBusHelper.post(appproduct_id, APPPRODUCT_ID);
            }*/
//        return result;
}
