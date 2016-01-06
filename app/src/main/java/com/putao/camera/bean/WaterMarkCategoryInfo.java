
package com.putao.camera.bean;

import com.putao.ahibernate.annotation.Column;
import com.putao.ahibernate.annotation.Id;
import com.putao.ahibernate.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;

@Table(name = "WaterMarkCategoryInfo")
public class WaterMarkCategoryInfo implements Serializable {
    private static final long serialVersionUID = -4169682109977772335L;

    public final static String camera = "camera_watermark";
    public final static String photo = "photo_watermark";

    @Id
    private Long _id;


    @Column(name = "id")
    public String id;

    @Column(name = "category")
    public String category;

    @Column(name = "updated")
    public String updated;

    @Column(name = "icon")
    public String icon;

    @Column(name = "icon_selected")
    public String icon_selected;

    @Column(name = "totals")
    public Integer totals;

    @Column(name = "attachment_size")
    public Integer attachment_size;

    @Column(name = "watermark_cover")
    public String watermark_cover;

    /**
     * "camera_watermark" or "photo_watermark" ;
     */
    @Column(name = "type")
    public String type;


    @Column(name = "isInner")
    public String isInner = "0";

    public ArrayList<WaterMarkIconInfo> elements = new ArrayList<WaterMarkIconInfo>();

}
