package com.putao.camera;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.putao.account.AccountHelper;
import com.putao.mtlib.tcp.PTMessageReceiver;
import com.sunnybear.library.controller.eventbus.EventBusHelper;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.PreferenceUtils;


public class RedDotReceiver extends PTMessageReceiver {
    public static final String EVENT_DOT_INDEX = "dot_index";
    public static final String EVENT_DOT_MATERIAL = "dot_material";
    public static final String EVENT_DOT_MATTER_CENTER = "event_dot_matter_center";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String message = intent.getExtras().getString(KeyMessage);
            setResult(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setResult(String result) {
        Logger.d("ptl", result);
        //接收结果示例:{"location_dot":{"index":1,"material":1,"sticker_pic":0,"dynamic_pic":1,"template_pic":0}}
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject location_dot = jsonObject.getJSONObject("location_dot");
        boolean index = false;
        boolean material = false;
        int sticker_pic = 0;
        int dynamic_pic = 0;
        int template_pic = 0;
        if (null != location_dot) {
            index = location_dot.getBoolean("index");
            material = location_dot.getBoolean("material");
            sticker_pic = location_dot.getInteger("sticker_pic");
            dynamic_pic = location_dot.getInteger("dynamic_pic");
            template_pic = location_dot.getInteger("template_pic");
        }
        //相机红点
        if (material) {
            EventBusHelper.post("", EVENT_DOT_MATERIAL);
        }

        int[] dots = new int[3];
        //贴纸红点
        if (0 != sticker_pic)
            dots[0] = sticker_pic;
        if (0 != dynamic_pic)
            dots[1] = dynamic_pic;
        if (0 != template_pic)
            dots[2] = template_pic;
        //缓存红点数据
        int[] value = PreferenceUtils.getValue(EVENT_DOT_MATTER_CENTER, dots);
        if (value != dots) {
            for (int i = 0; i < 3; i++) {
                dots[i] = value[i] + dots[i];
            }
        }
        PreferenceUtils.save(EVENT_DOT_MATTER_CENTER, dots);
        EventBusHelper.post("", EVENT_DOT_MATTER_CENTER);
    }

}
