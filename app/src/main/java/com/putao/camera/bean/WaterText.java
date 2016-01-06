package com.putao.camera.bean;

import java.io.Serializable;

import com.putao.ahibernate.annotation.Column;
import com.putao.ahibernate.annotation.Id;
import com.putao.ahibernate.annotation.Table;
import com.putao.camera.editor.view.TextWaterMarkView.WaterTextAlign;

@Table(name = "WaterText")
public class WaterText implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long _id;

    @Column(name = "text")
    public String text;

    @Column(name = "textColor")
    public String textColor;

    @Column(name = "textSize")
    public int textSize;

    @Column(name = "textAlign")
    public String textAlign = WaterTextAlign.CENTER;

    @Column(name = "left")
    public float left;

    @Column(name = "top")
    public float top;

    @Column(name = "right")
    public float right;

    @Column(name = "bottom")
    public float bottom;

    @Column(name = "eventType")
    public String eventType;

    @Column(name = "IconInfoId")
    public String IconInfoId;

    public WaterText() {

    }

    public WaterText(String wText, float Left, float Top, float Right, float Bottom, String EventType) {
        this(wText, "#000000", 25, Left, Top, Right, Bottom, EventType);
    }

    public WaterText(String wText, String wTextColor, int wTextSize, float Left, float Top, float Right, float Bottom, String EventType) {
        text = wText;
        textColor = wTextColor;
        textSize = wTextSize;
        left = Left;
        top = Top;
        right = Right;
        bottom = Bottom;
        eventType = EventType;
    }
}