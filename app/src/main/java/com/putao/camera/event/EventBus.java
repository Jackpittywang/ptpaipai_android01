package com.putao.camera.event;

/**
 * Created by jidongdong on 15/5/27.
 */
public class EventBus {
    public static de.greenrobot.event.EventBus getEventBus() {
        return de.greenrobot.event.EventBus.getDefault();
    }
}
