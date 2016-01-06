
package com.putao.camera.bean;

import java.io.Serializable;

import com.putao.camera.base.BaseItem;

public class PhotoInfo extends BaseItem implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public String _ID;
    public String _DATA;
    public String _DATE_ADDED;
    public String _SIZE;
    public String _TITLE;
    public String _MIME_TYPE;
    public long _ID_LONG;
    public boolean Checked = false;// represent the file is selected or not with album editting

    public long getDate_Added() {
        return Long.valueOf(_DATE_ADDED).longValue() * 1000;
    }
}