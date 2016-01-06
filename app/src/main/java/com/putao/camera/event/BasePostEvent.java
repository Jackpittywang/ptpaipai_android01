package com.putao.camera.event;

import android.os.Bundle;

public class BasePostEvent {
    public int eventCode = 0;
    public Bundle bundle;

    public BasePostEvent(int eventCode, Bundle bundle) {
        this.eventCode = eventCode;
        this.bundle = bundle;
    }

    public BasePostEvent(Bundle bundle) {
        this.bundle = bundle;
    }

    public BasePostEvent(int eventCode) {
        this.eventCode = eventCode;
    }

    public BasePostEvent() {
        super();
    }
}
