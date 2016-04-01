package com.putao.camera.bean;

import com.putao.ahibernate.annotation.Column;
import com.putao.ahibernate.annotation.Id;
import com.putao.ahibernate.annotation.Table;

import java.io.Serializable;

@Table(name = "StickerCategoryInfo")
public class StickerCategoryInfo implements Serializable {
    private static final long serialVersionUID = -4169682109977772336L;
    @Id
    private Long _id;

    @Column(name = "type")
    public String type;

    @Column(name = "name")
    public String name;

    @Column(name = "download_url")
    public String download_url;

    @Column(name = "position")
    public String position;

    @Column(name = "num")
    public String num;

    @Column(name = "id")
    public String id;

    @Column(name = "cover_pic")
    public String cover_pic;

    @Column(name = "is_new")
    public String is_new;

    @Column(name = "size")
    public String size;

    @Column(name = "categoryId")
    public String categoryId;



    @Column(name = "icon")
    public String icon;

    public StickerCategoryInfo() {

    }

}
