package com.putao.camera.bean;

import com.putao.ahibernate.annotation.Column;
import com.putao.ahibernate.annotation.Id;
import com.putao.ahibernate.annotation.Table;

import java.io.Serializable;

@Table(name = "StickerUnZipInfo")
public class StickerUnZipInfo implements Serializable {
    private static final long serialVersionUID = -4169682109977772362L;
    @Id
    private Long _id;

    @Column(name = "parentid")
    public String parentid;

    @Column(name = "zipName")
    public String zipName;

    @Column(name = "imgName")
    public String imgName;

    @Column(name = "position")
    public String position;

    @Column(name = "xmlName")
    public String xmlName;
}
