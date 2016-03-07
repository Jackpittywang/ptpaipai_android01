package com.putao.camera.camera.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.putao.camera.camera.model.AnimationModel;
import com.putao.camera.util.FileUtils;
import com.putao.camera.util.XmlUtils;

import java.io.File;

/**
 * Created by yanguoqiang on 16/2/22.
 * 针对动画的一些工具类
 */
public class AnimationUtils {


    /**
     * load animation model data from xml file;
     * @param animationName
     * @return
     */
    public static AnimationModel getModelFromXML(String animationName) {
        // xxx/xxx/xxx.xml
        String modelSetFile = FileUtils.getARStickersPath() + animationName + File.separator + animationName + ".xml";
        String xmlStr = FileUtils.getFileContent(modelSetFile);
        if (xmlStr == null) return null;
        AnimationModel model = XmlUtils.xmlToModel(xmlStr, "animation", com.putao.camera.camera.model.AnimationModel.class);
        if (model == null) return null;
        if (model.getEye() != null) {
            model.setCenterX(model.getEye().getCenterX());
            model.setCenterY(model.getEye().getCenterY());
            model.setDistance(model.getEye().getDistance());
            model.setDuration(model.getEye().getDuration());
            model.setAnimationImageSize(model.getEye().getImageList().size());
        } else if (model.getMouth() != null) {
            model.setCenterX(model.getMouth().getCenterX());
            model.setCenterY(model.getMouth().getCenterY());
            model.setDistance(model.getMouth().getDistance());
            model.setDuration(model.getMouth().getDuration());
            model.setAnimationImageSize(model.getMouth().getImageList().size());
        } else {
            model.setCenterX(0);
            model.setCenterY(0);
            model.setDistance(100);
            model.setDuration(1000);
            model.setAnimationImageSize(0);
        }
        return model;
    }





}
