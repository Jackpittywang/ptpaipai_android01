package com.putao.camera.collage.mode;

import com.putao.camera.bean.CollageConfigInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jidongdong on 15/3/11.
 */
public class CollageSampleItem implements Serializable {

    private static final long serialVersionUID = -7971488953798453288L;



    public String sample_image;
    public String mask_image;
    public String id;
    public ArrayList<CollageConfigInfo.CollageText> textElements;
    public ArrayList<CollageConfigInfo.CollageImageInfo> imageElements;
    public String category;

    public CollageSampleItem(CollageConfigInfo.CollageItemInfo iteminfo, String cate) {
        sample_image = iteminfo.sample_image;
        mask_image = iteminfo.mask_image;
        id = iteminfo.id;
        textElements = iteminfo.textElements;
        imageElements = iteminfo.imageElements;
        category = cate;

    }
}
