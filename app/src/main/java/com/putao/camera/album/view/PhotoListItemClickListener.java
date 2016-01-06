
package com.putao.camera.album.view;

import com.putao.camera.bean.PhotoInfo;

public interface PhotoListItemClickListener {
    public void onPhotoListItemClick(PhotoInfo info);

    public void onPhotoListItemLongClick(PhotoInfo info);

    public void onCheckedChanged(PhotoInfo info, boolean isChecked);
}