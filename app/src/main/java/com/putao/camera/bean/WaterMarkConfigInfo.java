package com.putao.camera.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.putao.camera.base.BaseItem;


public class WaterMarkConfigInfo extends BaseItem {

    public String version;

    public WaterMarkContent content;

    public class WaterMarkContent implements Serializable {
        public ArrayList<WaterMarkCategoryInfo> camera_watermark;
        public ArrayList<WaterMarkCategoryInfo> photo_watermark;

        public WaterMarkContent() {
            camera_watermark = new ArrayList<WaterMarkCategoryInfo>();
            photo_watermark = new ArrayList<WaterMarkCategoryInfo>();
        }
    }

    public WaterMarkConfigInfo() {
        content = new WaterMarkContent();
    }
}
