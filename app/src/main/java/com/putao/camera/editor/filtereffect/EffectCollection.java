package com.putao.camera.editor.filtereffect;

import com.putao.camera.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jidongdong on 14/12/30.
 */
public class EffectCollection {
    public static final String none = "None";
    public static final String autofix = "autofix";
    public static final String tint = "tint";
    public static final String temperature = "temperature";
    public static final String sharpen = "sharpen";
    public static final String sepia = "sepia";
    public static final String saturate = "saturate";//饱和度
    public static final String rotate = "rotate";
    public static final String posterize = "posterize";
    public static final String negative = "negative";
    public static final String lomoish = "lomoish";
    public static final String grayscale = "grayscale";
    public static final String grain = "grain";
    public static final String fliphor = "fliphor";
    public static final String flipvert = "flipvert";
    public static final String fisheye = "fisheye";
    public static final String filllight = "filllight";
    public static final String duotone = "duotone";
    public static final String documentary = "documentary";
    public static final String crossprocess = "crossprocess";
    public static final String contrast = "contrast";
    public static final String brightness = "brightness";
    public static final String bw = "bw";
    public static final String vignette = "vignette";
    public static final String sketch = "sketch";
    public static final String test1 = "test1";
    public static final String test2 = "test2";
    private static Map<String, String> filterMap = new HashMap<String, String>() {
        {
            put(autofix, "温暖如玉");
            put(tint, "蔚蓝海岸");
            put(temperature, "一米阳光");
            put(sharpen, "sharpen");
            put(sepia, "指尖流年");
            put(saturate, "秋日私语");
            put(rotate, "rotate");
            put(posterize, "posterize");
            put(negative, "negative");
            put(lomoish, "经典LOMO");
            put(grayscale, "童年记忆");
            put(grain, "grain");
            put(fliphor, "fliphor");
            put(flipvert, "flipvert");
            put(fisheye, "调皮鱼眼");
            put(filllight, "白白嫩嫩");
            put(duotone, "duotone");
            put(documentary, "偷偷瞅你");
            put(crossprocess, "陌上花开");
            put(contrast, "contrast");
            put(brightness, "白亮晨曦");
            put(bw, "bw");
            put(vignette, "闪亮登场");
            put(none, "原图");
            put(sketch, "素描");
            put(test1,"样式1");
            put(test2,"样式2");
        }
    };

    private static Map<String, String> moviefFlterMap = new HashMap<String, String>() {
        {
            put(autofix, "温暖如玉");
            put(tint, "王家卫");
            put(temperature, "爱情");
            put(sharpen, "sharpen");
            put(sepia, "西部");
            put(saturate, "文艺");
            put(rotate, "rotate");
            put(posterize, "posterize");
            put(negative, "negative");
            put(lomoish, "经典LOMO");
            put(grayscale, "童年记忆");
            put(grain, "grain");
            put(fliphor, "fliphor");
            put(flipvert, "flipvert");
            put(fisheye, "调皮鱼眼");
            put(filllight, "魔幻");
            put(duotone, "duotone");
            put(documentary, "偷偷瞅你");
            put(crossprocess, "剧情");
            put(contrast, "contrast");
            put(brightness, "记录");
            put(bw, "bw");
            put(vignette, "周星星");
            put(none, "原图");
        }
    };
    private static Map<String, Integer> filterSimpleMap = new HashMap<String, Integer>() {
        {
            //        put(autofix, R.drawable.filter_autofix);
            put(tint, R.drawable.filter_tint);
            put(temperature, R.drawable.filter_temperature);
            //        put(sharpen,R.drawable.filter_autofix);
            put(sepia, R.drawable.filter_sepia);
            put(saturate, R.drawable.filter_saturate);
            //        put(rotate,R.drawable.filter_autofix);
            //        put(posterize,R.drawable.filter_autofix);
            //        put(negative,R.drawable.filter_autofix);
            //        put(lomoish,R.drawable.filter_lomoish);
            //        put(grayscale,R.drawable.filter_grayscale);
            //        put(grain,R.drawable.filter_autofix);
            //        put(fliphor,R.drawable.filter_autofix);
            //        put(flipvert,R.drawable.filter_autofix);
            //        put(fisheye,R.drawable.filter_fisheye);
            put(filllight, R.drawable.filter_filllight);
            //        put(duotone,R.drawable.filter_autofix);
            //        put(documentary,R.drawable.filter_documentary);
            put(crossprocess, R.drawable.filter_crossprocess);
            //        put(contrast,R.drawable.filter_autofix);
            put(brightness, R.drawable.filter_brightness);
            //        put(bw,R.drawable.filter_none);
            put(vignette, R.drawable.filter_vignette);
            put(none, R.drawable.filter_none);
        }
    };

    public static int getFilterSample(String key) {
        return filterSimpleMap.get(key);
    }

    public static String getFilterName(String key) {
        return filterMap.get(key);
    }

    public static String getMovieFilterName(String key) {
        return moviefFlterMap.get(key);
    }
}
