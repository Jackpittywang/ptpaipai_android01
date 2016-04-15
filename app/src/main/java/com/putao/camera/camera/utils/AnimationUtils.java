package com.putao.camera.camera.utils;

import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.StickerUnZipInfo;
import com.putao.camera.camera.model.AnimationModel;
import com.putao.camera.util.FileUtils;
import com.putao.camera.util.XmlUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        //第一个name为包名,第二个为文件名
//        String modelSetFile = FileUtils.getARStickersPath() + animationName + File.separator + animationName + ".xml";
        String modelSetFile="";
        Map<String, String> map = new HashMap<String, String>();
        map.put("zipName", animationName);
//        List<DynamicIconInfo>   list = MainApplication.getDBServer().getDynamicIconInfoByWhere(map);
        List<StickerUnZipInfo> list= MainApplication.getDBServer().getStickerUnZipInfoByWhere(map);
        if(list.size()==0){
            modelSetFile = FileUtils.getARStickersPath() + animationName + File.separator + animationName + ".xml";
        }else {
            String xmlName=list.get(0).xmlName;
            modelSetFile = FileUtils.getARStickersPath() + animationName + File.separator + xmlName + ".xml";
        }
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
