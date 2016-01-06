package com.putao.camera.setting.watermark.management;

/**
 * Created by yanglun on 15/4/5.
 */
public interface UpdateCallback<K> {
    public void startProgress(K info, int position);

    public void delete(K info, int position);
}
