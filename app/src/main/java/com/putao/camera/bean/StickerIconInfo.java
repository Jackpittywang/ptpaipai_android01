package com.putao.camera.bean;

import com.putao.ahibernate.annotation.Column;
import com.putao.ahibernate.annotation.Id;
import com.putao.ahibernate.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;

@Table(name = "StickerIconInfo")
public class StickerIconInfo implements Serializable {
    private static final long serialVersionUID = -7971488953798453286L;
    @Id
    private Long _id;

    @Column(name = "id")
    public String id ="111";

    public ArrayList<StickerCategoryInfo> data;


}
