package com.sunnybear.library.view.emoji;

import java.io.Serializable;

/**
 * 表情封装
 * Created by guchenkai on 2015/12/15.
 */
public class Emoji implements Serializable {
    private String name;
    private String path;

    public Emoji(String name) {
        this.name = name;
    }

    public Emoji(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Emoji{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
