/**
 * Copyright (c) 2013 CommonsWare, LLC
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.putao.camera.camera.utils;

import android.hardware.Camera;
import android.view.View;

import java.io.IOException;

public interface PreviewStrategy {
    void bindCamera(Camera camera) throws IOException;

    View getWidget();

    void setPreviewSize(int w, int h);

    void onPause();

    void onDestory();

}