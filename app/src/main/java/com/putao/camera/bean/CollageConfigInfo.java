package com.putao.camera.bean;

import com.putao.ahibernate.annotation.Column;
import com.putao.ahibernate.annotation.Id;
import com.putao.ahibernate.annotation.Table;
import com.putao.camera.base.BaseItem;
import com.putao.camera.editor.view.TextWaterMarkView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author jidongdong
 */
public class CollageConfigInfo extends BaseItem {
    public String version;
    public WaterMarkContent content = new WaterMarkContent();

    static public class WaterMarkContent implements Serializable {
        public ArrayList<CollageCategoryInfo> collage_image = new ArrayList<CollageCategoryInfo>();
        public ArrayList<ConnectImageInfo> connect_image = new ArrayList<ConnectImageInfo>();
    }

    /**
     * 拼图单个模板分类信息
     *
     * @author jidongdong
     */
    static public class CollageCategoryInfo implements Serializable {
        private static final long serialVersionUID = -4169682109977772335L;

        @Column(name = "id")
        public String id;

        @Column(name = "category")
        public String category;

        public ArrayList<CollageItemInfo> elements = new ArrayList<CollageItemInfo>();
    }

    /**
     * 拼接模板
     *
     * @author chen
     */
    @Table(name = "ConnectImageInfo")
    static public class ConnectImageInfo implements Serializable {

        @Id
        private Long _id;

        @Column(name = "id")
        public String id;

        @Column(name = "sample_image")
        public String sample_image;

        @Column(name = "background_image")
        public String background_image;

        @Column(name = "mask_image")
        public String mask_image;
    }


    /**
     * 单个模板信息
     *
     * @author jidongdong
     */
    @Table(name = "CollageItemInfo")
    static public class CollageItemInfo implements Serializable {


        private static final long serialVersionUID = -7971488953798453288L;

        @Id
        private Long _id;

        @Column(name = "sample_image")
        public String sample_image;

        @Column(name = "mask_image")
        public String mask_image;

        @Column(name = "id")
        public String id;

        @Column(name = "parentId")
        public String parentId;

        @Column(name = "parentCategory")
        public String parentCategory;

        @Column(name = "isInner")
        public String isInner = "0";

        /**
         * textElements转换而来
         */
        @Column(name = "textElementsGson")
        public String textElementsGson;

        /**
         * imageElements 转换而来
         */
        @Column(name = "imageElementsGson")
        public String imageElementsGson;


        public ArrayList<CollageText> textElements = new ArrayList<CollageText>();

        public ArrayList<CollageImageInfo> imageElements = new ArrayList<CollageImageInfo>();
    }

    /**
     * 单个模板中的图片信息
     *
     * @author jidongdong
     */
    public class CollageImageInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        public ArrayList<CollageImagePoint> pointArray;
    }

    /**
     * 模板中单个图片对应的坐标信息
     *
     * @author jidongdong
     */
    public class CollageImagePoint implements Serializable {
        private static final long serialVersionUID = 4536490201716185429L;
        public float point_x;
        public float point_y;
    }

    public class CollageText implements Serializable {
        private static final long serialVersionUID = 1L;

        public String text;
        public String textColor;
        public int textSize;
        public String textAlign = TextWaterMarkView.WaterTextAlign.CENTER;
        public float left;
        public float top;
        public float right;
        public float bottom;
        public String textType;

    }
}
