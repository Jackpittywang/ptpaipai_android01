package com.putao.camera.bean;

import com.putao.ahibernate.annotation.Column;
import com.putao.ahibernate.annotation.Id;
import com.putao.ahibernate.annotation.Table;

import java.io.Serializable;

@Table(name = "DynamicIconInfo")
public class DynamicIconInfo implements Serializable {
    private static final long serialVersionUID = -4169682109977772339L;
    @Id
    private Long _id;

    @Column(name = "name")
    public String name;

    @Column(name = "download_url")
    public String download_url;

    @Column(name = "second_type")
    public String second_type;

    @Column(name = "type")
    public String type;

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

    @Column(name = "zipSize")
    public String zipSize;

    @Column(name = "zipName")
    public String zipName;



    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    public DynamicIconInfo() {

    }

}
