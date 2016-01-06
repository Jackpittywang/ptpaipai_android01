package com.putao.camera.bean;

import com.putao.ahibernate.annotation.Column;
import com.putao.ahibernate.annotation.Id;
import com.putao.ahibernate.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;

@Table(name = "WaterMarkIconInfo")
public class WaterMarkIconInfo implements Serializable {
    private static final long serialVersionUID = -7971488953798453288L;

    @Id
    private Long _id;

    @Column(name = "sample_image")
    public String sample_image;

    @Column(name = "watermark_image")
    public String watermark_image;

    @Column(name = "position")
    public String position;

    @Column(name = "id")
    public String id;

    @Column(name = "type")
    public String type;

    @Column(name = "categoryId")
    public String categoryId;

    @Column(name = "textElement")
    public String textElement;


    public ArrayList<WaterText> textElements;

    public WaterMarkIconInfo() {

    }

}
